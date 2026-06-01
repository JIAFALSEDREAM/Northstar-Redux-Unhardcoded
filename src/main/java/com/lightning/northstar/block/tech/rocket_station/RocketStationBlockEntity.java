package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketHandler;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.sealer.SealingMode;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RocketStationBlockEntity extends SmartBlockEntity implements IDisplayAssemblyExceptions, IControlContraption, MenuProvider {

    boolean assembleNextTick;
    public Player owner;
    protected AssemblyException lastException;
    public TrackTargetingBehaviour<GlobalStation> edgePoint;
    protected ItemStackHandler inventory;
    protected IItemHandlerModifiable itemCapability;
    public String name = "Bing Bong's Big Bonanza";

    protected int failedCarriageIndex;
    Direction assemblyDirection;
    int assemblyLength;

    public float offset;
    public int fuelCost;
    public int fuelReturnCost;
    public boolean running;
    public boolean needsContraption;
    public AbstractContraptionEntity movedContraption;
    protected boolean forceMove;
    protected ScrollOptionBehaviour<MovementMode> movementMode;
    protected boolean waitingForSpeedChange;
    protected double sequencedOffsetLimit;


    int i = 0;

    // Custom position sync
    protected float clientOffsetDiff;
    public ResourceKey<Level> target;


    public final Container container = new SimpleContainer(1);


    public static WorldAttached<Map<BlockPos, BoundingBox>> assemblyAreas = new WorldAttached<>(w -> new HashMap<>());

    public RocketStationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inventory = new ItemStackHandler();
        itemCapability = new CombinedInvWrapper(inventory);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inventory);
    }

    public void queueAssembly(Player player) {
        owner = player;
        assembleNextTick = true;
    }


    public void enterAssembly() {
        assembleNextTick = true;
    }

    public void exitAssembly() {
        assembleNextTick = false;
    }

    @Override
    public void tick() {
        super.tick();
        i++;
        ItemStack item = container.getItem(0);
        target = getTargetFromItem(item);
        fuelCost = fuelCalc();
        fuelReturnCost = fuelReturnCalc();

        if (assembleNextTick) {
            tryAssemble();
            assembleNextTick = false;
        }
    }


    private void tryAssemble() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof RocketStationBlock)) {
            return;
        }

        RocketContraption contraption = new RocketContraption();

        BlockEntity blockEntity = level.getBlockEntity(worldPosition);
        if (!(blockEntity instanceof RocketStationBlockEntity)) {
            return;
        }
        Direction movementDirection = Direction.UP;

        int engines = 0;
        boolean hasStation = false;
        float fuelAmount = 0;
        int requiredJets = 0;
        int heatShielding = 0;
        double heatCost = 0;
        double heatCostHome = 0;
        try {
            lastException = null;
            contraption.owner = owner;
            if (!contraption.assemble(level, worldPosition)) {
                return;
            }
            engines = contraption.hasJetEngine();
            fuelAmount = contraption.fuelAmount();
            heatShielding = contraption.heatShielding();
            hasStation |= contraption.hasRocketStation();
            contraption.fuelCost = fuelCost;
            contraption.fuelReturnCost = fuelReturnCost;
            contraption.dest = target;
            contraption.isUsingTicket = isReturnTicketForTarget(container.getItem(0), target);
            heatCost = (NorthstarTemperature.getHeatRating(target) * (contraption.blockCount)) + NorthstarTemperature.getHeatConstant(target);
            heatCostHome = (NorthstarTemperature.getHeatRating(level.dimension()) * (contraption.blockCount)) + NorthstarTemperature.getHeatConstant(level.dimension());
            if (heatCostHome > heatCost) {
                heatCost = heatCostHome;
            }
            requiredJets = engineCalc();

            sendData();
        } catch (AssemblyException e) {
            owner.displayClientMessage(Component.translatable("northstar.gui.rocket_too_big").withStyle(ChatFormatting.RED), false);
            owner.displayClientMessage(Component.translatable("northstar.gui.current_config_size").withStyle(ChatFormatting.RED), false);
            owner.displayClientMessage(Component.literal(AllConfigs.server().kinetics.maxBlocksMoved.get().toString()).withStyle(ChatFormatting.RED), false);
            lastException = e;
            sendData();
            return;
        }
        if (ContraptionCollider.isCollidingWithWorld(level, contraption, worldPosition.relative(movementDirection),
                movementDirection)) {

            if (!this.level.getBlockState(worldPosition.above()).isAir()) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.blocked_above").withStyle(ChatFormatting.RED), false);
            } else {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.blocked").withStyle(ChatFormatting.RED), false);
            }
            return;
        } else {
            Northstar.LOGGER.debug("Obamna");
        }

        ProgressiveBlockSealer sealer = new ProgressiveBlockSealer(SealingMode.OXYGEN);
        // cannot rely on getContraptionWorld() yet as it depends on the entity to get the level
        Level contraptionWorld = new ContraptionWorld(level, contraption);
        int maximumSealedBlocks = NorthstarConfigs.server().oxygenSealerMaxContraptionSealed.get();
        boolean oxygenSealed = sealer.beginSeal(contraptionWorld, BlockPos.ZERO, Direction.UP) &&
                sealer.updateSeal(contraptionWorld, maximumSealedBlocks, maximumSealedBlocks) &&
                !sealer.hasLeak();

        boolean interplanetaryFlag = NorthstarPlanets.isInterplanetary(level.dimension(), target);
        if (interplanetaryFlag) {
            if (contraption.hasInterplanetaryNavigation) {
                interplanetaryFlag = false;
            }
        }
        if (interplanetaryFlag) {
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.interplanetary_navigator_required").withStyle(ChatFormatting.RED), false);
        }
        //Assuming we have everything we need to assemble, let's do it
        if (engines >= requiredJets && hasStation && fuelAmount > (fuelCost + contraption.weightCost) && heatShielding >= heatCost && oxygenSealed && !interplanetaryFlag && contraption.hasControls && contraption.dest != null && contraption.dest != this.level.dimension()) {
            //Create the new contraption entity
            Northstar.LOGGER.debug("{}", engines);
            contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
            RocketContraptionEntity movedContraption = RocketContraptionEntity.create(level, contraption);
            BlockPos anchor = worldPosition;
            movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);
            movedContraption.destination = target;
            //Assign auto lander
            movedContraption.auto_land_mode = contraption.hasAutoLander;
            movedContraption.home = level.dimension();
            RocketHandler.ROCKETS.add(movedContraption);
            level.addFreshEntity(movedContraption);
        } else {
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.full_fuel_cost", contraption.weightCost + contraption.fuelCost).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.current_fuel_supply", (int) contraption.fuelAmount()).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.estimated_return_cost", contraption.weightCost + fuelReturnCost).withStyle(ChatFormatting.GOLD), false);
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.required_heat_shielding", heatCost).withStyle(ChatFormatting.YELLOW), false);
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.current_heat_shielding", contraption.heatShielding()).withStyle(ChatFormatting.YELLOW), false);
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.required_engines", requiredJets).withStyle(ChatFormatting.BLUE), false);
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.current_engine_count", contraption.hasJetEngine()).withStyle(ChatFormatting.BLUE), false);
            if (!oxygenSealed) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.cockpit_unsealed").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (contraption.fuelAmount() < contraption.fuelCost) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.insufficient_fuel").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (contraption.heatShielding() < heatCost) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.insufficient_heat_shielding").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (contraption.hasJetEngine() < requiredJets) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.not_enough_jet_engines").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (!contraption.hasControls) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.no_controls").withStyle(ChatFormatting.DARK_RED), false);
            }
            if (container.getItem(0).isEmpty()) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.no_target_item").withStyle(ChatFormatting.DARK_RED), false);
            } else if (contraption.dest == null || contraption.dest == this.level.dimension()) {
                contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.invalid_target").withStyle(ChatFormatting.DARK_RED), false);
            }
            contraption.owner.displayClientMessage(Component.translatable("northstar.gui.rocket_station.assembly_failed").withStyle(ChatFormatting.RED), false);
            Northstar.LOGGER.debug("No station or jet engine, Bruh!");
            Northstar.LOGGER.debug("Heat Cost: {}     Heat Shielding: {}", heatCost, heatShielding);
            Northstar.LOGGER.debug("Weight Cost: {}      Fuel Cost: {}", contraption.weightCost, fuelCost);
            exception(new AssemblyException(CreateLang.translateDirect("train_assembly.no_controls")), -1);
        }
    }

    public boolean isAssembling() {
        BlockState state = getBlockState();
        return state.hasProperty(StationBlock.ASSEMBLING) && state.getValue(StationBlock.ASSEMBLING);
    }

    public int fuelCalc() {
        return NorthstarPlanets.getTravelFuelCost(this.level.dimension(), target);
    }

    public int fuelReturnCalc() {
        return NorthstarPlanets.getReturnFuelCost(this.level.dimension(), target);
    }

    public int engineCalc() {
        return NorthstarPlanets.getRequiredEngines(level.dimension(), target);
    }

    private boolean isReturnTicketForTarget(ItemStack item, ResourceKey<Level> target) {
        return item.is(NorthstarItems.RETURN_TICKET.get())
                && item.has(NorthstarDataComponents.PLANET)
                && NorthstarPlanets.isRocketTarget(item.get(NorthstarDataComponents.PLANET), target);
    }

    private ResourceKey<Level> getTargetFromItem(ItemStack item) {
        if ((item.is(NorthstarItems.STAR_MAP.get()) || item.is(NorthstarItems.RETURN_TICKET.get()))
                && item.has(NorthstarDataComponents.PLANET)) {
            return NorthstarPlanets.getPlanetDimension(item.get(NorthstarDataComponents.PLANET));
        }
        return null;
    }

    // this is extremely buggy for some reason, this NEEDS to be fixed before release
    //not sure what's making it so buggy but it saves really inconsistently despite sharing the code of the oxygen filler which works fine

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.put("item", container.getItem(0).saveOptional(registries));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        if (tag.contains("item", Tag.TAG_COMPOUND)) {
            container.setItem(0, ItemStack.parseOptional(registries, tag.getCompound("item")));
        }
    }

    private void exception(AssemblyException exception, int carriage) {
        failedCarriageIndex = carriage;
        lastException = exception;
        sendData();
    }

    @SuppressWarnings("unused")
    private boolean shouldAssemble() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof RocketStationBlock))
            return false;
        return true;
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }

    public Direction getAssemblyDirection() {
        if (assemblyDirection != null)
            return assemblyDirection;
        if (!edgePoint.hasValidTrack())
            return null;
        BlockPos targetPosition = edgePoint.getGlobalPosition();
        BlockState trackState = edgePoint.getTrackBlockState();
        ITrackBlock track = edgePoint.getTrack();
        AxisDirection axisDirection = edgePoint.getTargetDirection();
        Vec3 axis = track.getTrackAxes(level, targetPosition, trackState)
                .get(0)
                .normalize()
                .scale(axisDirection.getStep());
        return assemblyDirection = Direction.getNearest(axis.x, axis.y, axis.z);
    }

    @Override
    public boolean isValid() {
        return !isRemoved();
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        this.movedContraption = contraption;
        if (!level.isClientSide) {
            this.running = true;
            sendData();
        }
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return movedContraption == contraption;
    }

    @Override
    public BlockPos getBlockPosition() {
        return worldPosition;
    }

    @Override
    public void onStall() {
        if (!level.isClientSide) {
            forceMove = true;
            sendData();
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return RocketStationMenu.create(id, inv, this);
    }

    @Override
    public Component getDisplayName() {
        return NorthstarBlocks.ROCKET_STATION.get().getName();
    }

}
