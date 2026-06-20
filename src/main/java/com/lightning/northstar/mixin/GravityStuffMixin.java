package com.lightning.northstar.mixin;

import com.lightning.northstar.contraption.rocket.RocketHandler;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public class GravityStuffMixin {
    @Unique
    private static final double CONSTANT = 0.08;
    private int fall_disabled = 0;

    @Inject(method = "travel", at = @At("TAIL"))
    public void northstar$travel(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (fall_disabled > 0) {
            fall_disabled--;
            entity.fallDistance = 0;
        }
        if (RocketHandler.isInRocket(entity) && entity.getY() > 1500) {
            fall_disabled = 400;
        }
        Vec3 velocity = entity.getDeltaMovement();
        boolean isInOrbit = NorthstarPlanets.isInOrbit(entity.level().dimension());
        double planetGravity = NorthstarPlanets.getLivingGravityMultiplier(entity.level().dimension());

        if (entity.isFallFlying() || entity.isInFluidType()) {
            planetGravity = 1;
        }
        if (!entity.isNoGravity() && !NorthstarEntityTags.IGNORES_PLANET_GRAVITY.matches(entity) && !entity.isInWater() && !entity.isInLava() && !entity.hasEffect(MobEffects.SLOW_FALLING)) {
            float dust_push = 0;
            if (entity.level().getRainLevel(0) > 0 && entity.level().getRawBrightness(entity.blockPosition(), -1) == 16 && !entity.isSpectator() && (NorthstarPlanets.hasDustStormPush(entity.level()) && !NorthstarOxygen.hasOxygen(entity.level(), entity.getEyePosition()))
                    && entity.level().isInWorldBounds(entity.blockPosition()) && !RocketHandler.isInRocket(entity)) {
                dust_push = 0.005f;
            }
            if (entity instanceof Player ply) {
                if (ply.isCreative()) {
                    dust_push = 0;
                }
            }

            double newGrav = CONSTANT * planetGravity;
            float crouchPush = 0;
            if (!isInOrbit) {
                entity.setDeltaMovement(velocity.x() + dust_push, velocity.y() + (CONSTANT - newGrav), velocity.z() - dust_push);
            } else {
                if (entity.isCrouching()) {
                    crouchPush = 0.05f;
                }
                float vel_y = (float) Mth.clamp(velocity.y(), -0.3, 15);
                entity.setDeltaMovement(velocity.x() + dust_push, vel_y + (CONSTANT - newGrav) - crouchPush, velocity.z() - dust_push);
            }
        }
        /*if (isInOrbit) {
            if (entity.getY() < 0 && !entity.level().isClientSide) {
                if (NorthstarPlanets.isInOrbit(entity.level().dimension())) {
                    ServerLevel destLevel = entity.level().getServer().getLevel(Level.OVERWORLD);
                    if (entity instanceof ServerPlayer player) {
                        changePlayerDimension(destLevel, player);
                    } else {
                        changeDimensionCustom(destLevel, entity);
                    }
                }
            }
        }*/
    }

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    public void calculateFallDamage(float pFallDistance, float pDamageMultiplier, CallbackInfoReturnable<Integer> info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!NorthstarPlanets.hasNormalGrav(entity.level().dimension()) && !NorthstarEntityTags.IGNORES_PLANET_GRAVITY.matches(entity)) {
            MobEffectInstance mobeffectinstance = entity.getEffect(MobEffects.JUMP);
            double mult = NorthstarPlanets.getLivingGravityMultiplier(entity.level().dimension());
            float f = (float) (mobeffectinstance == null ? 0.0F : (float) (mobeffectinstance.getAmplifier() + 1) * mult);
            info.setReturnValue(Mth.ceil(((pFallDistance * mult) - 3.0F - f) * pDamageMultiplier));
        }
    }
}
