package com.lightning.northstar.mixin.gravitystuff;

import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityGravityMixin {
    @Unique
    private static final double CONSTANT = 0.03;

    @Inject(method = "tick", at = @At("TAIL"))
    public void northstar$tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Vec3 velocity = entity.getDeltaMovement();
        double planetGravity = NorthstarPlanets.getItemGravityMultiplier(entity.level().dimension());
        if (!entity.isNoGravity() && !NorthstarEntityTags.IGNORES_PLANET_GRAVITY.matches(entity)) {
            double newGrav = CONSTANT * planetGravity;
            entity.setDeltaMovement(velocity.x(), velocity.y() + CONSTANT - newGrav, velocity.z());
        }
        if (NorthstarPlanets.hasDustStormPush(entity.level()) && entity.level().getRainLevel(0) > 0
                && entity.level().getRawBrightness(entity.blockPosition(), 0) == 15 && !entity.isSpectator()) {
            entity.setDeltaMovement(velocity.x() + 0.01, velocity.y(), velocity.z() - 0.01);
        }
    }

}
