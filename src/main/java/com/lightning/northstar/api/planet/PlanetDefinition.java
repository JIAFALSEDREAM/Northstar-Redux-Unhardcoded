package com.lightning.northstar.api.planet;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Public data model for one planet or observable celestial body.
 * <p>
 * This is the source of truth queried by rocket, telescope, environment, and
 * rendering systems. Keep new planet behavior representable here before adding
 * system-specific branches elsewhere.
 */
public record PlanetDefinition(
        /** Stable id stored on star maps, return tickets, readings, and saves. */
        String id,
        /** Dimension reached by rockets; null is valid for telescope-only bodies. */
        @Nullable ResourceKey<Level> dimension,
        /** Translation key used by UI. Defaults to planets.[id].name. */
        String displayNameLangKey,
        /** Telescope/orbit position definition. */
        OrbitDefinition orbit,
        /** Entity gravity multiplier where overworld gravity is 1.0. */
        double gravityMultiplier,
        /** Default ambient temperature in Celsius unless a sky profile overrides it. */
        int temperature,
        /** True when entities can breathe without oxygen systems. */
        boolean hasOxygen,
        /** True when atmosphere-dependent machines should treat the body as atmospheric. */
        boolean hasAtmosphere,
        /** Fuel cost contribution for launching through this atmosphere. */
        int atmosphereCost,
        /** Targeting/computing difficulty used by rocket systems. */
        int computingCost,
        /** Additional engine requirement constant used by rocket systems. */
        double engineConstant,
        /** Solar panel multiplier. */
        float sunMultiplier,
        /** Windmill output behavior. */
        WindDefinition wind,
        /** First-phase rendering preset; not a fully data-driven sky model yet. */
        SkyProfile skyProfile,
        /** Texture used by the telescope UI; null hides the sprite unless custom UI handles it. */
        @Nullable ResourceLocation telescopeTexture,
        /** Optional id used for telescope tooltip translation keys. Defaults to id. */
        @Nullable String telescopeTooltipId,
        /** Hit radius in telescope screen pixels. */
        int telescopeHitRadius,
        /** Hit offset in telescope screen pixels. */
        int telescopeHitOffset,
        /** True when the telescope should render this body with vanilla moon phases. */
        boolean telescopeMoonPhase,
        /** Dimensions where this body is hidden in the telescope. */
        Set<ResourceKey<Level>> telescopeHiddenDimensions,
        /** If non-empty, dimensions where this body is allowed to appear. */
        Set<ResourceKey<Level>> telescopeVisibleOnlyDimensions,
        /** True when the body appears in telescope listings. */
        boolean observable,
        /** True when star maps and rockets can target this body. */
        boolean reachableByRocket,
        /** True when rocket travel to this body requires an interplanetary navigator. */
        boolean requiresInterplanetaryNavigator,
        /** True when telescopes can work in this dimension. */
        boolean hasSky,
        /** True when telescope use is allowed during daytime in this dimension. */
        boolean canSeeSkyAtDay,
        /** True when the dimension uses weather restrictions/effects. */
        boolean hasWeather,
        /** True when this dimension should be treated as a Northstar custom dimension. */
        boolean customDimension,
        /** True for space/orbit dimensions that need orbit-style gravity handling. */
        boolean orbitDimension,
        /** Terrain seed offset for dimension generation compatibility. */
        long seedOffset,
        /** Heat shielding multiplier used by rockets. */
        double heatRating,
        /** Fixed heat shielding cost used by rockets. */
        double heatConstant
) {

    /**
     * Creates a mutable builder with conservative earth-like defaults.
     */
    public static Builder builder(String id) {
        return new Builder(id);
    }

    /**
     * Builder used by built-in registration, Java mod callbacks, and KubeJS.
     * Defaults are intentionally usable for simple test planets, but real
     * planets should explicitly set environment and rocket values.
     */
    public static final class Builder {
        private final String id;
        @Nullable
        private ResourceKey<Level> dimension;
        private String displayNameLangKey;
        private OrbitDefinition orbit = OrbitDefinition.fixed(0, 0);
        private double gravityMultiplier = 1;
        private int temperature = 15;
        private boolean hasOxygen = true;
        private boolean hasAtmosphere = true;
        private int atmosphereCost = 0;
        private int computingCost = 0;
        private double engineConstant = 1;
        private float sunMultiplier = 1;
        private WindDefinition wind = WindDefinition.NORMAL;
        private SkyProfile skyProfile = SkyProfile.DEFAULT;
        @Nullable
        private ResourceLocation telescopeTexture;
        @Nullable
        private String telescopeTooltipId;
        private int telescopeHitRadius = 8;
        private int telescopeHitOffset = 8;
        private boolean telescopeMoonPhase = false;
        private Set<ResourceKey<Level>> telescopeHiddenDimensions = Set.of();
        private Set<ResourceKey<Level>> telescopeVisibleOnlyDimensions = Set.of();
        private boolean observable = true;
        private boolean reachableByRocket = false;
        private boolean requiresInterplanetaryNavigator = true;
        private boolean hasSky = false;
        private boolean canSeeSkyAtDay = false;
        private boolean hasWeather = true;
        private boolean customDimension = false;
        private boolean orbitDimension = false;
        private long seedOffset = 0;
        private double heatRating = 1;
        private double heatConstant = 1;

        private Builder(String id) {
            this.id = id;
            this.displayNameLangKey = "planets." + id + ".name";
        }

        /** Sets the dimension reached by rockets; leave null for telescope-only bodies. */
        public Builder dimension(@Nullable ResourceKey<Level> dimension) {
            this.dimension = dimension;
            return this;
        }

        /** Sets the dimension from a namespaced location. */
        public Builder dimension(ResourceLocation dimension) {
            return dimension(ResourceKey.create(Registries.DIMENSION, dimension));
        }

        /** Sets the dimension from a string such as "northstar:mars". */
        public Builder dimension(String dimension) {
            return dimension(ResourceLocation.parse(dimension));
        }

        public Builder displayNameLangKey(String displayNameLangKey) {
            this.displayNameLangKey = displayNameLangKey;
            return this;
        }

        /** Sets the telescope/orbit path used for UI position and fuel distance. */
        public Builder orbit(OrbitDefinition orbit) {
            this.orbit = orbit;
            return this;
        }

        public Builder gravityMultiplier(double gravityMultiplier) {
            this.gravityMultiplier = gravityMultiplier;
            return this;
        }

        public Builder temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder oxygen(boolean hasOxygen) {
            this.hasOxygen = hasOxygen;
            return this;
        }

        /** Controls atmosphere machines and launch atmosphere behavior separately from oxygen. */
        public Builder atmosphere(boolean hasAtmosphere) {
            this.hasAtmosphere = hasAtmosphere;
            return this;
        }

        public Builder atmosphereCost(int atmosphereCost) {
            this.atmosphereCost = atmosphereCost;
            return this;
        }

        public Builder computingCost(int computingCost) {
            this.computingCost = computingCost;
            return this;
        }

        public Builder engineConstant(double engineConstant) {
            this.engineConstant = engineConstant;
            return this;
        }

        public Builder sunMultiplier(float sunMultiplier) {
            this.sunMultiplier = sunMultiplier;
            return this;
        }

        public Builder wind(WindDefinition wind) {
            this.wind = wind;
            return this;
        }

        public Builder skyProfile(SkyProfile skyProfile) {
            this.skyProfile = skyProfile;
            return this;
        }

        /** Sets the telescope sprite texture. Null keeps the planet data-only. */
        public Builder telescopeTexture(@Nullable ResourceLocation telescopeTexture) {
            this.telescopeTexture = telescopeTexture;
            return this;
        }

        /** Sets the telescope sprite texture from a string location. */
        public Builder telescopeTexture(String telescopeTexture) {
            return telescopeTexture(ResourceLocation.parse(telescopeTexture));
        }

        /** Overrides the id used for tooltip translation keys such as planets.[id].grav. */
        public Builder telescopeTooltipId(@Nullable String telescopeTooltipId) {
            this.telescopeTooltipId = telescopeTooltipId;
            return this;
        }

        /** Sets telescope click/hover hitbox data. */
        public Builder telescopeHitbox(int radius, int offset) {
            this.telescopeHitRadius = radius;
            this.telescopeHitOffset = offset;
            return this;
        }

        /** Uses the vanilla moon-phase texture path for this telescope body. */
        public Builder telescopeMoonPhase(boolean telescopeMoonPhase) {
            this.telescopeMoonPhase = telescopeMoonPhase;
            return this;
        }

        /** Hides this body in the listed dimensions. */
        @SafeVarargs
        public final Builder telescopeHiddenIn(ResourceKey<Level>... dimensions) {
            this.telescopeHiddenDimensions = Set.of(dimensions);
            return this;
        }

        /** Restricts this body to the listed dimensions. Empty means visible anywhere. */
        @SafeVarargs
        public final Builder telescopeVisibleOnlyIn(ResourceKey<Level>... dimensions) {
            this.telescopeVisibleOnlyDimensions = Set.of(dimensions);
            return this;
        }

        public Builder observable(boolean observable) {
            this.observable = observable;
            return this;
        }

        public Builder reachableByRocket(boolean reachableByRocket) {
            this.reachableByRocket = reachableByRocket;
            return this;
        }

        public Builder requiresInterplanetaryNavigator(boolean requiresInterplanetaryNavigator) {
            this.requiresInterplanetaryNavigator = requiresInterplanetaryNavigator;
            return this;
        }

        public Builder hasSky(boolean hasSky) {
            this.hasSky = hasSky;
            return this;
        }

        public Builder canSeeSkyAtDay(boolean canSeeSkyAtDay) {
            this.canSeeSkyAtDay = canSeeSkyAtDay;
            return this;
        }

        public Builder hasWeather(boolean hasWeather) {
            this.hasWeather = hasWeather;
            return this;
        }

        public Builder customDimension(boolean customDimension) {
            this.customDimension = customDimension;
            return this;
        }

        public Builder orbitDimension(boolean orbitDimension) {
            this.orbitDimension = orbitDimension;
            return this;
        }

        public Builder seedOffset(long seedOffset) {
            this.seedOffset = seedOffset;
            return this;
        }

        public Builder heat(double heatRating, double heatConstant) {
            this.heatRating = heatRating;
            this.heatConstant = heatConstant;
            return this;
        }

        /** Builds an immutable definition suitable for registry registration. */
        public PlanetDefinition build() {
            return new PlanetDefinition(
                    id,
                    dimension,
                    displayNameLangKey,
                    orbit,
                    gravityMultiplier,
                    temperature,
                    hasOxygen,
                    hasAtmosphere,
                    atmosphereCost,
                    computingCost,
                    engineConstant,
                    sunMultiplier,
                    wind,
                    skyProfile,
                    telescopeTexture,
                    telescopeTooltipId,
                    telescopeHitRadius,
                    telescopeHitOffset,
                    telescopeMoonPhase,
                    Set.copyOf(telescopeHiddenDimensions),
                    Set.copyOf(telescopeVisibleOnlyDimensions),
                    observable,
                    reachableByRocket,
                    requiresInterplanetaryNavigator,
                    hasSky,
                    canSeeSkyAtDay,
                    hasWeather,
                    customDimension,
                    orbitDimension,
                    seedOffset,
                    heatRating,
                    heatConstant
            );
        }
    }

}
