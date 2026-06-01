package com.lightning.northstar.api.planet;

import net.minecraft.world.level.Level;

/**
 * Windmill output multipliers for clear and rainy/stormy weather.
 */
public record WindDefinition(float clearMultiplier, float weatherMultiplier) {

    /** No wind generation. */
    public static final WindDefinition NONE = new WindDefinition(0, 0);
    /** Vanilla-like wind generation. */
    public static final WindDefinition NORMAL = new WindDefinition(1, 1);

    /** Returns the current multiplier for a level's weather state. */
    public float multiplier(Level level) {
        return level.isRaining() ? weatherMultiplier : clearMultiplier;
    }

}
