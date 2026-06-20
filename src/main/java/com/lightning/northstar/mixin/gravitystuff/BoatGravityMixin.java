package com.lightning.northstar.mixin.gravitystuff;

import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public class BoatGravityMixin {
    @Unique
    private static final double CONSTANT = 0.08;

    @Inject(method = "tick", at = @At("TAIL"))
    public void northstar$travel(CallbackInfo ci) {
        Boat entity = (Boat) (Object) this;
        Vec3 velocity = entity.getDeltaMovement();
        double planetGravity = NorthstarPlanets.getBoatGravityMultiplier(entity.level().dimension());
        if (!entity.isNoGravity() && !NorthstarEntityTags.IGNORES_PLANET_GRAVITY.matches(entity) && !entity.isInWater() && !entity.isInLava()) {
            double newGrav = CONSTANT * planetGravity;
            entity.setDeltaMovement(velocity.x(), velocity.y() + CONSTANT - newGrav, velocity.z());
        }
    }
}
