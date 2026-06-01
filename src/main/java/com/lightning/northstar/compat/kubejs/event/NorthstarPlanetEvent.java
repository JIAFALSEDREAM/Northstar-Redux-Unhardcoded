package com.lightning.northstar.compat.kubejs.event;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.planet.PlanetDefinition;
import com.lightning.northstar.api.planet.PlanetRegistry;
import com.lightning.northstar.compat.kubejs.KubePlanetBuilder;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.ArrayList;
import java.util.List;

/**
 * KubeJS startup event for scripted planet registration.
 * <p>
 * Scripts should use this as an adapter over the public Java API, not as a
 * separate scripting-only planet model.
 */
public class NorthstarPlanetEvent implements KubeEvent {

    private final List<Runnable> actions = new ArrayList<>();

    /**
     * Creates and schedules a builder registration.
     * <p>
     * Example:
     * NorthstarEvents.planets(event => event.createPlanet("example").reachableByRocket(true))
     */
    @Info(value = "Defines a planet", params = {
            @Param(name = "id", value = "The stable planet id stored on star maps and return tickets")
    })
    public KubePlanetBuilder createPlanet(String id) {
        KubePlanetBuilder builder = new KubePlanetBuilder(id);
        actions.add(() -> PlanetRegistry.registerCallback(planets -> registerFromKubeJs(builder.build())));
        return builder;
    }

    /** Schedules a builder to be built and registered after startup scripts load. */
    public void planet(KubePlanetBuilder builder) {
        actions.add(() -> PlanetRegistry.registerCallback(planets -> registerFromKubeJs(builder.build())));
    }

    /** Schedules a Java builder to be built and registered after startup scripts load. */
    public void planet(PlanetDefinition.Builder builder) {
        actions.add(() -> PlanetRegistry.registerCallback(planets -> registerFromKubeJs(builder.build())));
    }

    /** Schedules an already-built planet definition for registration. */
    public void planet(PlanetDefinition planet) {
        actions.add(() -> PlanetRegistry.registerCallback(planets -> registerFromKubeJs(planet)));
    }

    /** Runs deferred registrations once KubeJS has finished collecting actions. */
    @HideFromJS
    public void postProcess() {
        actions.forEach(Runnable::run);
    }

    private static void registerFromKubeJs(PlanetDefinition planet) {
        PlanetRegistry.registerOrReplace(planet);
        Northstar.LOGGER.info("Registered KubeJS planet '{}' (dimension={}, observable={}, reachableByRocket={})",
                planet.id(),
                planet.dimension() == null ? "none" : planet.dimension().location(),
                planet.observable(),
                planet.reachableByRocket());
        if (planet.reachableByRocket() && planet.dimension() == null) {
            Northstar.LOGGER.warn("KubeJS planet '{}' is reachableByRocket but has no dimension; no creative star map will be generated.", planet.id());
        }
    }

}
