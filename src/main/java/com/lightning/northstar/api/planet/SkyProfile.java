package com.lightning.northstar.api.planet;

/**
 * Reusable first-phase sky/fog rendering presets.
 * <p>
 * These profiles let custom planets reuse existing Northstar sky behavior
 * before the renderer becomes fully data-driven.
 */
public enum SkyProfile {
    /** Vanilla-like sky and fog behavior. */
    DEFAULT,
    /** Black sky, no clouds, orbit-style fog/gravity expectations. */
    SPACE,
    /** Moon-style black sky with stars and nearby body rendering. */
    MOON_LIKE,
    /** Mars-style sky, dust storm weather, and high-altitude fade. */
    MARS_LIKE,
    /** Venus-style yellow atmosphere, acid rain, and dense fog. */
    VENUS_LIKE,
    /** Mercury-style black sky and day/night temperature handling. */
    MERCURY_LIKE
}
