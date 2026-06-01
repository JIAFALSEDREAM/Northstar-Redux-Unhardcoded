package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.planet.PlanetDefinition;
import com.lightning.northstar.api.planet.PlanetRegistry;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TelescopeScreen extends AbstractSimiContainerScreen<TelescopeMenu> {

    private static final ResourceLocation TELESCOPE_TEXTURE = Northstar.asResource("textures/gui/telescope_gui.png");
    private static final ResourceLocation TELESCOPE_TEXTURE_SIDE = Northstar.asResource("textures/gui/telescope_gui_side.png");
    private static final ResourceLocation BACKGROUND = Northstar.asResource("textures/environment/space_background.png");
    private static final ResourceLocation MOON_GLOW = ResourceLocation.parse("textures/environment/moon_phases.png");
    private static final ResourceLocation MOON_FLAT = Northstar.asResource("textures/environment/moon_flat.png");

    private boolean isScrolling;
    private double scrollX = 450;
    private double scrollY = 450;

    private Inventory inv;
    public String selectedPlanet = null;

    public TelescopeScreen(TelescopeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.inv = inv;

        imageWidth = 300;
        imageHeight = 300;
        titleLabelX = 150;
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        IconButton printButton = new IconButton(x - 33, y + 200, AllIcons.I_ADD);
        printButton.withCallback(() -> {
            if (selectedPlanet != null) {
                CatnipServices.NETWORK.sendToServer(new TelescopePrintPacket(menu.contentHolder.getBlockPos(), selectedPlanet));
            }
        });

        printButton.active = paperCheck();
        printButton.setToolTip(Component.translatable("northstar.gui.telescope.button_tooltip"));
        addRenderableWidget(printButton);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
        graphics.drawString(font, title, titleLabelX - font.width(title) / 2, titleLabelY, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.enableScissor(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3);
        graphics.blit(BACKGROUND, x, y, (int) -scrollX, (int) -scrollY, imageWidth, imageHeight, 900, 900);
        renderPlanets(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();

        graphics.blit(TELESCOPE_TEXTURE_SIDE, x - 174, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        graphics.blit(TELESCOPE_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        renderTooltip(graphics, mouseX, mouseY);
        renderPlanetTooltips(graphics, mouseX, mouseY);
        renderSelectedPlanet(graphics);
    }

    public void renderPlanets(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        PoseStack pose = graphics.pose();

        ResourceKey<Level> player_dim = Minecraft.getInstance().player.level().dimension();
        for (PlanetDefinition planet : PlanetRegistry.all()) {
            if (!shouldRenderPlanet(planet, player_dim)) {
                continue;
            }
            if (planet.telescopeMoonPhase()) {
                renderMoonPhasePlanet(graphics, pose, planet);
            } else {
                renderPlanetSprite(graphics, pose, planet);
            }
        }
    }

    private void renderPlanetSprite(GuiGraphics graphics, PoseStack pose, PlanetDefinition planet) {
        ResourceLocation texture = getPlanetSprite(planet.id());
        if (texture == null) {
            return;
        }

        int planetX = (int) NorthstarPlanets.getPlanetX(planet.id());
        int planetY = (int) NorthstarPlanets.getPlanetY(planet.id());
        pose.pushPose();
        pose.scale(0.05F, 0.05F, 0.05F);
        graphics.blit(texture, (planetX * 20) + (int) scrollX * 20, (planetY * 20) + (int) scrollY * 20, 0, 0, 255, 255);
        pose.popPose();
    }

    private void renderMoonPhasePlanet(GuiGraphics graphics, PoseStack pose, PlanetDefinition planet) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        pose.pushPose();
        pose.scale(2F, 1F, 1F);
        int moonPhase = Minecraft.getInstance().level.getMoonPhase();
        int moonUvX = (moonPhase % 4) * 64;
        int moonUvY = (moonPhase / 4) * 128;
        int x = ((int) NorthstarPlanets.getPlanetX(planet.id()) + (int) scrollX / 2) - 27;
        int y = ((int) NorthstarPlanets.getPlanetY(planet.id()) + (int) scrollY) - 57;
        graphics.blit(MOON_GLOW, x, y, moonUvX, moonUvY, 64, 128);
        pose.popPose();

        pose.pushPose();
        RenderSystem.disableBlend();
        pose.scale(2F, 1F, 1F);
        graphics.blit(MOON_FLAT, x, y, moonUvX, moonUvY, 64, 128);
        pose.popPose();
    }

    public boolean paperCheck() {
        if (inv.player.isCreative()) {
            return true;
        }

        boolean flag = false;

        if (inv != null) {
            for (int p = 0; p < 36; p++) {
                ItemStack items = inv.getItem(p);
                Item item = items.getItem();
                if (item == Items.PAPER) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public void renderSelectedPlanet(GuiGraphics graphics) {
        if (selectedPlanet != null) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            graphics.blit(getPlanetSprite(selectedPlanet), x - 40, y + 93, 0, 0, 35, 35, 35, 35);
            graphics.drawString(font, getPlanetName(selectedPlanet), x - 45, y + 130, 6944, false);
            graphics.drawString(font, Component.translatable("northstar.gui.telescope.coordinate_x", (int) NorthstarPlanets.getPlanetX(selectedPlanet)), x - 45, y + 140, 6944, false);
            graphics.drawString(font, Component.translatable("northstar.gui.telescope.coordinate_y", (int) NorthstarPlanets.getPlanetY(selectedPlanet)), x - 45, y + 150, 6944, false);
        }
    }

    public void renderPlanetTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        ResourceKey<Level> player_dim = Minecraft.getInstance().player.level().dimension();
        for (PlanetDefinition planet : PlanetRegistry.all()) {
            if (!shouldRenderPlanet(planet, player_dim) || !isMouseOverPlanet(planet, mouseX, mouseY)) {
                continue;
            }
            RenderSystem.colorMask(true, true, true, true);
            graphics.renderComponentTooltip(font, createPlanetTooltip(planet), mouseX, mouseY);
            return;
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            }
            scroll(pDragX, pDragY);
            return true;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        if (pButton == 0 || pButton == 1) {
            ResourceKey<Level> playerDim = Minecraft.getInstance().level.dimension();
            for (PlanetDefinition planet : PlanetRegistry.all()) {
                if (shouldRenderPlanet(planet, playerDim) && isMouseOverPlanet(planet, mouseX, mouseY)) {
                    selectedPlanet = planet.id();
                    break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, pButton);
    }


    public void scroll(double pDragX, double pDragY) {
        this.scrollX = Mth.clamp(this.scrollX + pDragX, 0, 900);
        this.scrollY = Mth.clamp(this.scrollY + pDragY, 0, 900);
    }


    public ResourceLocation getPlanetSprite(String planet) {
        return PlanetRegistry.byId(planet)
                .map(PlanetDefinition::telescopeTexture)
                .orElse(null);

    }

    public Component getPlanetName(String planet) {
        return Component.translatable("planets." + planet + ".name");
    }

    private boolean shouldRenderPlanet(PlanetDefinition planet, ResourceKey<Level> playerDimension) {
        if (!planet.observable() || planet.telescopeTexture() == null) {
            return false;
        }
        if (!planet.telescopeVisibleOnlyDimensions().isEmpty()
                && !planet.telescopeVisibleOnlyDimensions().contains(playerDimension)) {
            return false;
        }
        if (planet.telescopeHiddenDimensions().contains(playerDimension)) {
            return false;
        }
        if (planet.dimension() != null && planet.dimension() == playerDimension) {
            return false;
        }
        return true;
    }

    private boolean isMouseOverPlanet(PlanetDefinition planet, double mouseX, double mouseY) {
        double planetX = NorthstarPlanets.getPlanetX(planet.id());
        double planetY = NorthstarPlanets.getPlanetY(planet.id());
        int radius = planet.telescopeHitRadius();
        int offset = planet.telescopeHitOffset();
        return Math.abs(planetX + scrollX + offset - mouseX) < radius
                && Math.abs(planetY + scrollY + offset - mouseY) < radius;
    }

    private List<Component> createPlanetTooltip(PlanetDefinition planet) {
        String tooltipId = planet.telescopeTooltipId() == null ? planet.id() : planet.telescopeTooltipId();
        int x = (int) NorthstarPlanets.getPlanetX(planet.id());
        int y = (int) NorthstarPlanets.getPlanetY(planet.id());
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("planets." + tooltipId + ".name").withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("planets." + tooltipId + ".type").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("planets." + tooltipId + ".grav").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("planets." + tooltipId + ".temp").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("planets." + tooltipId + ".atmosphere").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("northstar.gui.telescope.coordinate_x", x).withStyle(ChatFormatting.WHITE));
        list.add(Component.translatable("northstar.gui.telescope.coordinate_y", y).withStyle(ChatFormatting.WHITE));
        return list;
    }

}
