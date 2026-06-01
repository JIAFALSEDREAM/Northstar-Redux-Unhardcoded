package com.lightning.northstar.api.planet;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Central planet registry queried by gameplay systems.
 * <p>
 * Registration order is:
 * built-in definitions, Java mod callbacks, KubeJS startup callbacks, and later
 * datapack/JSON definitions. Systems should query this registry instead of
 * hardcoding planet ids or dimensions.
 */
public final class PlanetRegistry {

    private static final Map<String, PlanetDefinition> BY_ID = new LinkedHashMap<>();
    private static final Map<ResourceKey<Level>, PlanetDefinition> BY_DIMENSION = new LinkedHashMap<>();
    private static final Map<String, PlanetPosition> POSITIONS = new LinkedHashMap<>();
    private static final List<Consumer<PlanetRegistrar>> REGISTRATION_CALLBACKS = new ArrayList<>();
    private static boolean initialized;

    private PlanetRegistry() {
    }

    /**
     * Registers a new planet id.
     *
     * @throws IllegalArgumentException when the id already exists
     */
    public static PlanetDefinition register(PlanetDefinition definition) {
        if (BY_ID.containsKey(definition.id())) {
            throw new IllegalArgumentException("Duplicate planet id: " + definition.id());
        }

        put(definition);
        return definition;
    }

    /**
     * Registers or replaces a planet definition.
     * <p>
     * This is mainly for script/datapack layers where overriding a built-in
     * definition is intentional. Java mods should prefer {@link #register} when
     * they own a new id.
     */
    public static PlanetDefinition registerOrReplace(PlanetDefinition definition) {
        PlanetDefinition previous = BY_ID.get(definition.id());
        if (previous != null && previous.dimension() != null && BY_DIMENSION.get(previous.dimension()) == previous) {
            BY_DIMENSION.remove(previous.dimension());
        }

        put(definition);
        return definition;
    }

    private static void put(PlanetDefinition definition) {
        BY_ID.put(definition.id(), definition);
        POSITIONS.put(definition.id(), definition.orbit().position(0, null));

        if (definition.dimension() != null) {
            // The first definition owns dimension lookups. This preserves the
            // moon dimension primary definition while earth_moon remains an
            // observable legacy alias.
            BY_DIMENSION.putIfAbsent(definition.dimension(), definition);
        }
    }

    /**
     * Clears runtime definitions before the built-in registration pass.
     * Registered callbacks are intentionally kept.
     */
    public static void clear() {
        BY_ID.clear();
        BY_DIMENSION.clear();
        POSITIONS.clear();
        initialized = false;
    }

    /**
     * Registers a callback for third-party Java mods.
     * <p>
     * Use this from mod setup instead of directly depending on Northstar init
     * ordering. If the registry is already initialized, the callback is applied
     * immediately.
     */
    public static void registerCallback(Consumer<PlanetRegistrar> callback) {
        REGISTRATION_CALLBACKS.add(callback);
        if (initialized) {
            callback.accept(PlanetRegistry::register);
        }
    }

    /**
     * Applies all deferred registration callbacks after built-ins are present.
     */
    public static void applyRegistrationCallbacks() {
        for (Consumer<PlanetRegistrar> callback : REGISTRATION_CALLBACKS) {
            callback.accept(PlanetRegistry::register);
        }
        initialized = true;
    }

    /**
     * Looks up a definition by stable planet id.
     */
    public static Optional<PlanetDefinition> byId(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    /**
     * Looks up the primary definition for a dimension.
     */
    public static Optional<PlanetDefinition> byDimension(@Nullable ResourceKey<Level> dimension) {
        if (dimension == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_DIMENSION.get(dimension));
    }

    /**
     * Returns definitions in registration order for deterministic UI rendering.
     */
    public static Collection<PlanetDefinition> all() {
        return Collections.unmodifiableCollection(BY_ID.values());
    }

    public static Collection<PlanetPosition> positions() {
        return Collections.unmodifiableCollection(POSITIONS.values());
    }

    public static PlanetPosition position(String id) {
        return POSITIONS.getOrDefault(id, new PlanetPosition(0, 0));
    }

    public static double x(String id) {
        return position(id).x();
    }

    public static double y(String id) {
        return position(id).y();
    }

    /**
     * Recomputes orbit positions for the current world time.
     */
    public static void tick(long time) {
        for (PlanetDefinition definition : BY_ID.values()) {
            PlanetPosition parentPosition = null;
            if (definition.orbit().parentId() != null) {
                parentPosition = POSITIONS.get(definition.orbit().parentId());
            }
            POSITIONS.put(definition.id(), definition.orbit().position(time, parentPosition));
        }
    }

    public static boolean isCustomDimension(ResourceLocation location) {
        return BY_DIMENSION.values().stream()
                .anyMatch(definition -> definition.customDimension()
                        && definition.dimension() != null
                        && definition.dimension().location().equals(location));
    }

}
