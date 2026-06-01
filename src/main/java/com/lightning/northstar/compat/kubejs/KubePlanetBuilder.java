package com.lightning.northstar.compat.kubejs;

import com.lightning.northstar.api.planet.OrbitDefinition;
import com.lightning.northstar.api.planet.PlanetDefinition;
import com.lightning.northstar.api.planet.SkyProfile;
import com.lightning.northstar.api.planet.WindDefinition;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * KubeJS-facing wrapper around {@link PlanetDefinition.Builder}.
 * <p>
 * Keeping KubeJS annotations here avoids coupling the public Java API package to
 * optional scripting classes while still giving ProbeJS a clean chainable type.
 */
public class KubePlanetBuilder {

    private final PlanetDefinition.Builder builder;

    public KubePlanetBuilder(String id) {
        this(PlanetDefinition.builder(id));
    }

    public KubePlanetBuilder(PlanetDefinition.Builder builder) {
        this.builder = builder;
    }

    /**
     * Sets the target dimension id used by star maps, return tickets, rocket
     * stations, and dimension environment lookups. Omit this for telescope-only
     * bodies.
     */
    @Info(value = "Sets the dimension reached by rockets. Required for creative star maps and rocket targets.", params = {
            @Param(name = "dimension", value = "Namespaced dimension id, for example 'northstar:mars'")
    })
    public KubePlanetBuilder dimension(String dimension) {
        builder.dimension(dimension);
        return this;
    }

    /**
     * Overrides the default display translation key. If unset, Northstar uses
     * {@code planets.<id>.name}.
     */
    @Info(value = "Sets the translation key used for this body's display name.", params = {
            @Param(name = "displayNameLangKey", value = "Translation key, for example 'planets.example.name'")
    })
    public KubePlanetBuilder displayNameLangKey(String displayNameLangKey) {
        builder.displayNameLangKey(displayNameLangKey);
        return this;
    }

    /**
     * Sets the telescope-space orbit. These are UI coordinates, not Minecraft
     * world coordinates, and the current rocket fuel distance estimate also
     * uses this moving position.
     */
    @Info(value = "Sets the telescope-space orbit. These coordinates are also used by current travel fuel distance estimates.", params = {
            @Param(name = "orbit", value = "Use OrbitDefinition.fixed, aroundOrigin, or around")
    })
    public KubePlanetBuilder orbit(OrbitDefinition orbit) {
        builder.orbit(orbit);
        return this;
    }

    /** Sets base entity gravity, where overworld gravity is {@code 1.0}. */
    @Info(value = "Sets base gravity where overworld gravity is 1.0.", params = {
            @Param(name = "gravityMultiplier", value = "Gravity multiplier")
    })
    public KubePlanetBuilder gravityMultiplier(double gravityMultiplier) {
        builder.gravityMultiplier(gravityMultiplier);
        return this;
    }

    /** Sets ambient temperature in Celsius for temperature and heat systems. */
    @Info(value = "Sets ambient temperature in Celsius.", params = {
            @Param(name = "temperature", value = "Temperature in Celsius")
    })
    public KubePlanetBuilder temperature(int temperature) {
        builder.temperature(temperature);
        return this;
    }

    /** Sets whether entities can breathe without oxygen support. */
    @Info(value = "Controls whether entities can breathe without oxygen support.", params = {
            @Param(name = "hasOxygen", value = "True when the atmosphere is breathable")
    })
    public KubePlanetBuilder oxygen(boolean hasOxygen) {
        builder.oxygen(hasOxygen);
        return this;
    }

    /**
     * Sets whether atmosphere-dependent machines and launch checks treat this
     * body as atmospheric. This is separate from breathable oxygen.
     */
    @Info(value = "Controls whether atmosphere machines and launch logic treat this body as atmospheric.", params = {
            @Param(name = "hasAtmosphere", value = "True when the body has an atmosphere")
    })
    public KubePlanetBuilder atmosphere(boolean hasAtmosphere) {
        builder.atmosphere(hasAtmosphere);
        return this;
    }

    /**
     * Sets the atmosphere launch cost. This is a gameplay balance value for
     * atmosphere thickness, pressure, and drag, not a real-world unit.
     */
    @Info(value = "Sets the launch cost contributed by atmosphere thickness and pressure.", params = {
            @Param(name = "atmosphereCost", value = "Gameplay cost value; 0 means no atmosphere launch cost")
    })
    public KubePlanetBuilder atmosphereCost(int atmosphereCost) {
        builder.atmosphereCost(atmosphereCost);
        return this;
    }

    /** Sets targeting/computing difficulty used by rocket systems. */
    @Info(value = "Sets targeting/computing difficulty used by rocket systems.", params = {
            @Param(name = "computingCost", value = "Gameplay cost value")
    })
    public KubePlanetBuilder computingCost(int computingCost) {
        builder.computingCost(computingCost);
        return this;
    }

    /** Adds a flat contribution to required rocket engine count. */
    @Info(value = "Sets the additional engine requirement constant for rocket assembly.", params = {
            @Param(name = "engineConstant", value = "Additional engine requirement")
    })
    public KubePlanetBuilder engineConstant(double engineConstant) {
        builder.engineConstant(engineConstant);
        return this;
    }

    /** Sets solar panel output multiplier for this body. */
    @Info(value = "Sets solar panel output multiplier.", params = {
            @Param(name = "sunMultiplier", value = "Solar output multiplier")
    })
    public KubePlanetBuilder sunMultiplier(double sunMultiplier) {
        builder.sunMultiplier((float) sunMultiplier);
        return this;
    }

    /** Sets windmill output multipliers for clear and weather states. */
    @Info(value = "Sets clear-weather and storm-weather wind multipliers.", params = {
            @Param(name = "wind", value = "Use WindDefinition.NONE, NORMAL, or new WindDefinition(clear, weather)")
    })
    public KubePlanetBuilder wind(WindDefinition wind) {
        builder.wind(wind);
        return this;
    }

    /**
     * Selects one of the first-phase sky/fog presets. This does not yet expose
     * fully data-driven colors, clouds, or renderer parameters.
     */
    @Info(value = "Selects one of Northstar's first-pass sky and fog profiles.", params = {
            @Param(name = "skyProfile", value = "SkyProfile.DEFAULT, SPACE, MOON_LIKE, MARS_LIKE, VENUS_LIKE, or MERCURY_LIKE")
    })
    public KubePlanetBuilder skyProfile(SkyProfile skyProfile) {
        builder.skyProfile(skyProfile);
        return this;
    }

    /** Sets the texture drawn by the telescope for this body. */
    @Info(value = "Sets the telescope sprite texture.", params = {
            @Param(name = "telescopeTexture", value = "Texture id, for example 'northstar:textures/environment/mars_far.png'")
    })
    public KubePlanetBuilder telescopeTexture(String telescopeTexture) {
        builder.telescopeTexture(telescopeTexture);
        return this;
    }

    /**
     * Uses another id for telescope tooltip translation keys, useful for legacy
     * aliases such as bodies that share one tooltip set.
     */
    @Info(value = "Uses another id for telescope tooltip translation keys.", params = {
            @Param(name = "telescopeTooltipId", value = "Tooltip id used in keys like planets.<id>.grav")
    })
    public KubePlanetBuilder telescopeTooltipId(String telescopeTooltipId) {
        builder.telescopeTooltipId(telescopeTooltipId);
        return this;
    }

    /** Sets telescope hover/click hitbox tuning for this body's sprite. */
    @Info(value = "Sets telescope hover and click hitbox values.", params = {
            @Param(name = "radius", value = "Half-size of the hitbox"),
            @Param(name = "offset", value = "Offset applied to match the rendered sprite")
    })
    public KubePlanetBuilder telescopeHitbox(int radius, int offset) {
        builder.telescopeHitbox(radius, offset);
        return this;
    }

    /** Enables the vanilla moon-phase render path for this telescope body. */
    @Info(value = "Uses the vanilla moon-phase texture rendering path for this telescope body.", params = {
            @Param(name = "telescopeMoonPhase", value = "True for moon-phase rendering")
    })
    public KubePlanetBuilder telescopeMoonPhase(boolean telescopeMoonPhase) {
        builder.telescopeMoonPhase(telescopeMoonPhase);
        return this;
    }

    /** Hides this telescope body while the player is in any listed dimension. */
    @Info(value = "Hides this body in the listed dimensions.", params = {
            @Param(name = "dimensions", value = "Dimension ids such as 'minecraft:overworld'")
    })
    public KubePlanetBuilder telescopeHiddenIn(String... dimensions) {
        builder.telescopeHiddenIn(toDimensionKeys(dimensions));
        return this;
    }

    /**
     * Restricts this telescope body to the listed dimensions. Leaving this unset
     * means the body can render from any dimension unless hidden separately.
     */
    @Info(value = "Only shows this body in the listed dimensions. Leave unset to show it anywhere.", params = {
            @Param(name = "dimensions", value = "Dimension ids such as 'northstar:mars'")
    })
    public KubePlanetBuilder telescopeVisibleOnlyIn(String... dimensions) {
        builder.telescopeVisibleOnlyIn(toDimensionKeys(dimensions));
        return this;
    }

    /** Controls whether this body is included in telescope rendering. */
    @Info(value = "Controls whether this body appears in the telescope.", params = {
            @Param(name = "observable", value = "True to render in the telescope when a texture is set")
    })
    public KubePlanetBuilder observable(boolean observable) {
        builder.observable(observable);
        return this;
    }

    /**
     * Controls whether star maps and rockets can target this body. Reachable
     * bodies must also have a dimension.
     */
    @Info(value = "Controls whether star maps and rockets can target this body.", params = {
            @Param(name = "reachableByRocket", value = "True for rocket destinations; requires a dimension")
    })
    public KubePlanetBuilder reachableByRocket(boolean reachableByRocket) {
        builder.reachableByRocket(reachableByRocket);
        return this;
    }

    /** Controls whether rocket assembly requires an interplanetary navigator. */
    @Info(value = "Controls whether this destination requires an interplanetary navigator on the rocket.", params = {
            @Param(name = "requiresInterplanetaryNavigator", value = "True to require the navigator")
    })
    public KubePlanetBuilder requiresInterplanetaryNavigator(boolean requiresInterplanetaryNavigator) {
        builder.requiresInterplanetaryNavigator(requiresInterplanetaryNavigator);
        return this;
    }

    /** Marks this body as having Northstar-managed sky behavior. */
    @Info(value = "Controls whether this dimension uses Northstar sky rendering.", params = {
            @Param(name = "hasSky", value = "True when Northstar should handle sky checks for this dimension")
    })
    public KubePlanetBuilder hasSky(boolean hasSky) {
        builder.hasSky(hasSky);
        return this;
    }

    /** Allows the sky to be visible during daytime for this dimension. */
    @Info(value = "Controls whether the sky is visible during daytime.", params = {
            @Param(name = "canSeeSkyAtDay", value = "True when the sky should render during the day")
    })
    public KubePlanetBuilder canSeeSkyAtDay(boolean canSeeSkyAtDay) {
        builder.canSeeSkyAtDay(canSeeSkyAtDay);
        return this;
    }

    /** Controls whether normal weather is enabled for this body. */
    @Info(value = "Controls normal weather behavior.", params = {
            @Param(name = "hasWeather", value = "True when the body has ordinary weather")
    })
    public KubePlanetBuilder hasWeather(boolean hasWeather) {
        builder.hasWeather(hasWeather);
        return this;
    }

    /** Marks this as a Northstar-style custom dimension for compatibility code. */
    @Info(value = "Marks this as a Northstar-style custom dimension for compatibility checks.", params = {
            @Param(name = "customDimension", value = "True for custom planet dimensions")
    })
    public KubePlanetBuilder customDimension(boolean customDimension) {
        builder.customDimension(customDimension);
        return this;
    }

    /** Marks this as an orbit/space dimension for orbit-style gravity handling. */
    @Info(value = "Marks this as an orbit or space dimension for orbit-style gravity handling.", params = {
            @Param(name = "orbitDimension", value = "True for space/orbit dimensions")
    })
    public KubePlanetBuilder orbitDimension(boolean orbitDimension) {
        builder.orbitDimension(orbitDimension);
        return this;
    }

    /** Sets the legacy worldgen seed offset compatibility value. */
    @Info(value = "Sets the legacy worldgen seed offset compatibility value.", params = {
            @Param(name = "seedOffset", value = "Seed offset")
    })
    public KubePlanetBuilder seedOffset(long seedOffset) {
        builder.seedOffset(seedOffset);
        return this;
    }

    /** Sets heat shielding cost inputs used by rocket checks. */
    @Info(value = "Sets heat shielding requirements used by rocket checks.", params = {
            @Param(name = "heatRating", value = "Heat rating multiplied by rocket block count"),
            @Param(name = "heatConstant", value = "Flat heat shielding cost")
    })
    public KubePlanetBuilder heat(double heatRating, double heatConstant) {
        builder.heat(heatRating, heatConstant);
        return this;
    }

    @HideFromJS
    public PlanetDefinition build() {
        return builder.build();
    }

    @HideFromJS
    public PlanetDefinition.Builder unwrap() {
        return builder;
    }

    @SuppressWarnings("unchecked")
    private static ResourceKey<Level>[] toDimensionKeys(String[] dimensions) {
        ResourceKey<Level>[] keys = new ResourceKey[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            keys[i] = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimensions[i]));
        }
        return keys;
    }

}
