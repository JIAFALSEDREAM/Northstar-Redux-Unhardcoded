package com.lightning.northstar.api.planet;

/**
 * Registration sink passed to Java mod callbacks.
 * <p>
 * Keeping this tiny gives Northstar room to change registry internals without
 * forcing third-party mods to call implementation details.
 */
@FunctionalInterface
public interface PlanetRegistrar {
    /** Registers one planet definition during the active registration pass. */
    PlanetDefinition register(PlanetDefinition definition);
}
