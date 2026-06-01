package com.lightning.northstar.world.dimension;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.planet.OrbitDefinition;
import com.lightning.northstar.api.planet.PlanetDefinition;
import com.lightning.northstar.api.planet.PlanetRegistry;
import com.lightning.northstar.api.planet.SkyProfile;
import com.lightning.northstar.api.planet.WindDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class NorthstarPlanets {

    private static final double EARTH_GRAV = 1;
    private static final double MOON_GRAV = 0.16;
    private static final double OUTER_MOON_GRAV = 0.06;
    private static final double MARS_GRAV = 0.37;
    private static final double VENUS_GRAV = 0.89;
    private static final double MERCURY_GRAV = 0.38;

    public static double mercury_x = 0;
    public static double mercury_y = 0;
    public static float mercury_orbit_speed = (float) (Math.PI / 12000);
    public static double mercury_orbit_radius_x = 100;
    public static double mercury_orbit_radius_y = 100;
    public static double mercury_origin_x = 0;
    public static double mercury_origin_y = 0;
    static int mercury_time;

    public static double venus_x = 0;
    public static double venus_y = 0;
    public static float venus_orbit_speed = (float) (Math.PI / 32000);
    public static double venus_orbit_radius_x = 150;
    public static double venus_orbit_radius_y = 150;
    public static double venus_origin_x = 0;
    public static double venus_origin_y = 0;
    static int venus_time;

    public static double earth_x = 0;
    public static double earth_y = 0;
    public static float earth_orbit_speed = (float) (Math.PI / 32000);
    public static double earth_orbit_radius_x = 200;
    public static double earth_orbit_radius_y = 200;
    public static double earth_origin_x = 0;
    public static double earth_origin_y = 0;
    static int earth_time;

    public static double earth_moon_x = 0;
    public static double earth_moon_y = 0;
    public static float earth_moon_orbit_speed = (float) (Math.PI / 150000);
    public static double earth_moon_orbit_radius_x = 40;
    public static double earth_moon_orbit_radius_y = 40;
    public static double earth_moon_origin_x = 0;
    public static double earth_moon_origin_y = 0;
    static int earth_moon_time;

    public static double moon_x = 0;
    public static double moon_y = 0;
    public static float moon_orbit_speed = (float) (Math.PI / 10000);
    public static double moon_orbit_radius_x = 20;
    public static double moon_orbit_radius_y = 20;
    static int moon_time;

    public static double mars_x = 0;
    public static double mars_y = 0;
    public static float mars_orbit_speed = (float) (Math.PI / 100000);
    public static double mars_orbit_radius_x = 250;
    public static double mars_orbit_radius_y = 250;
    public static double mars_origin_x = 0;
    public static double mars_origin_y = 0;
    static int mars_time;

    public static double pd_x = 0;
    public static double pd_y = 0;
    public static float pd_orbit_speed = (float) (Math.PI / 10000);
    public static double pd_orbit_radius_x = 20;
    public static double pd_orbit_radius_y = 20;
    static int pd_time;

    public static double ceres_x = 0;
    public static double ceres_y = 0;
    public static float ceres_orbit_speed = (float) (Math.PI / 200000);
    public static double ceres_orbit_radius_x = 260;
    public static double ceres_orbit_radius_y = 260;
    public static double ceres_origin_x = 0;
    public static double ceres_origin_y = 0;
    static int ceres_time;

    public static double jupiter_x = 0;
    public static double jupiter_y = 0;
    public static float jupiter_orbit_speed = (float) (Math.PI / 32000);
    public static double jupiter_orbit_radius_x = 270;
    public static double jupiter_orbit_radius_y = 270;
    public static double jupiter_origin_x = 0;
    public static double jupiter_origin_y = 0;
    static int jupiter_time;

    public static double saturn_x = 0;
    public static double saturn_y = 0;
    public static float saturn_orbit_speed = (float) (Math.PI / 40000);
    public static double saturn_orbit_radius_x = 300;
    public static double saturn_orbit_radius_y = 300;
    public static double saturn_origin_x = 0;
    public static double saturn_origin_y = 0;
    static int saturn_time;

    public static double uranus_x = 0;
    public static double uranus_y = 0;
    public static float uranus_orbit_speed = (float) (Math.PI / 50000);
    public static double uranus_orbit_radius_x = 375;
    public static double uranus_orbit_radius_y = 375;
    public static double uranus_origin_x = 0;
    public static double uranus_origin_y = 0;
    static int uranus_time;

    public static double neptune_x = 0;
    public static double neptune_y = 0;
    public static float neptune_orbit_speed = (float) (Math.PI / 80000);
    public static double neptune_orbit_radius_x = 450;
    public static double neptune_orbit_radius_y = 450;
    public static double neptune_origin_x = 0;
    public static double neptune_origin_y = 0;
    static int neptune_time;

    public static double pluto_x = 0;
    public static double pluto_y = 0;
    public static float pluto_orbit_speed = (float) (Math.PI / 150000);
    public static double pluto_orbit_radius_x = 525;
    public static double pluto_orbit_radius_y = 515;
    public static double pluto_origin_x = 0;
    public static double pluto_origin_y = 0;
    static int pluto_time;

    public static double eris_x = 0;
    public static double eris_y = 0;
    public static float eris_orbit_speed = (float) (Math.PI / 16000);
    public static double eris_orbit_radius_x = 1000;
    public static double eris_orbit_radius_y = 600;
    public static double eris_origin_x = 0;
    public static double eris_origin_y = 0;
    static int eris_time;

    static long time;

    /**
     * Keeps legacy public coordinate fields synchronized while the registry is
     * the actual source of planet positions.
     */
    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        time = event.getLevel().getGameTime() * 2L;
        PlanetRegistry.tick(time);
        syncLegacyCoordinates();
    }

    public static double getPlanetX(String name) {
        return PlanetRegistry.x(name);
    }

    public static double getPlanetY(String name) {
        return PlanetRegistry.y(name);
    }

    public static String getPlanetName(@Nullable ResourceKey<Level> level) {
        if (level == NorthstarDimensions.EARTH_ORBIT_DIM_KEY) {
            // Preserve old return-ticket/star-map strings: earth orbit returns
            // to earth rather than storing earth_orbit.
            return "earth";
        }
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::id)
                .orElse("earth");
    }

    public static boolean planetHasSky(ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::hasSky)
                .orElse(false);
    }

    public static int getPlanetTemp(ResourceKey<Level> level) {
        if (level == Level.NETHER) {
            return 230;
        }
        if (level == Level.END) {
            return 4;
        }
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::temperature)
                .orElse(15);
    }

    public static float getBaseTemperature(Level level, BlockPos pos) {
        if (level.dimension() == NorthstarDimensions.MERCURY_DIM_KEY) {
            // Exact built-in Mercury behavior is kept for compatibility.
            return level.canSeeSky(pos) && !level.isNight() ? 434 : -200;
        }
        return PlanetRegistry.byDimension(level.dimension())
                .filter(definition -> definition.skyProfile() == SkyProfile.MERCURY_LIKE)
                .map(definition -> level.canSeeSky(pos) && !level.isNight() ? (float) definition.temperature() : -200F)
                .orElseGet(() -> (float) getPlanetTemp(level.dimension()));
    }

    public static int getPlanetAtmosphereCost(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::atmosphereCost)
                .orElse(0);
    }

    public static int getComputingCost(ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::computingCost)
                .orElse(0);
    }

    public static @Nullable ResourceKey<Level> getPlanetDimension(String name) {
        return PlanetRegistry.byId(name)
                // Old items may contain telescope-only ids. They should not
                // become rocket targets unless the definition opts in.
                .filter(PlanetDefinition::reachableByRocket)
                .map(PlanetDefinition::dimension)
                .orElse(null);
    }

    public static boolean isRocketTarget(String name, @Nullable ResourceKey<Level> target) {
        return target != null && target == getPlanetDimension(name);
    }

    public static boolean getPlanetOxy(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::hasOxygen)
                .orElse(true);
    }

    public static boolean hasAtmosphere(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::hasAtmosphere)
                .orElse(true);
    }

    public static boolean hasNormalGrav(ResourceKey<Level> level) {
        return getGravMultiplier(level) == EARTH_GRAV;
    }

    public static boolean canSeeSkyAtDay(ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::canSeeSkyAtDay)
                .orElse(false);
    }

    public static boolean hasWeather(ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::hasWeather)
                .orElse(true);
    }

    public static SkyProfile getSkyProfile(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::skyProfile)
                .orElse(SkyProfile.DEFAULT);
    }

    public static double getGravMultiplier(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::gravityMultiplier)
                .orElse(EARTH_GRAV);
    }

    public static double getLivingGravityMultiplier(@Nullable ResourceKey<Level> level) {
        return getGravMultiplier(level);
    }

    /**
     * Item and throwable gravity historically used a slightly different Venus
     * value than the main planet gravity. Keep that quirk centralized until
     * entity-category gravity becomes explicit planet data.
     */
    public static double getItemGravityMultiplier(@Nullable ResourceKey<Level> level) {
        if (level == NorthstarDimensions.VENUS_DIM_KEY) {
            return 0.88;
        }
        return getGravMultiplier(level);
    }

    public static double getThrowableGravityMultiplier(@Nullable ResourceKey<Level> level) {
        if (level == NorthstarDimensions.VENUS_DIM_KEY) {
            return 0.88;
        }
        return getGravMultiplier(level);
    }

    /**
     * Boats, minecarts, and arrows had gameplay-tuned gravity values that do
     * not match physical planet gravity. These helpers preserve old behavior
     * while removing per-mixin dimension switches.
     */
    public static double getBoatGravityMultiplier(@Nullable ResourceKey<Level> level) {
        if (level == NorthstarDimensions.VENUS_DIM_KEY) {
            return 0.92;
        }
        if (level == NorthstarDimensions.MOON_DIM_KEY || level == NorthstarDimensions.MARS_DIM_KEY
                || level == NorthstarDimensions.MERCURY_DIM_KEY || isInOrbit(level)) {
            return 0.65;
        }
        return getGravMultiplier(level);
    }

    public static double getMinecartGravityMultiplier(@Nullable ResourceKey<Level> level) {
        if (level == NorthstarDimensions.MOON_DIM_KEY) {
            return 0.64;
        }
        if (level == NorthstarDimensions.VENUS_DIM_KEY) {
            return 0.92;
        }
        if (level == NorthstarDimensions.MARS_DIM_KEY || level == NorthstarDimensions.MERCURY_DIM_KEY || isInOrbit(level)) {
            return 0.65;
        }
        return getGravMultiplier(level);
    }

    public static double getArrowGravityMultiplier(@Nullable ResourceKey<Level> level) {
        if (level == NorthstarDimensions.VENUS_DIM_KEY) {
            return 0.92;
        }
        if (level == NorthstarDimensions.MOON_DIM_KEY || level == NorthstarDimensions.MARS_DIM_KEY
                || level == NorthstarDimensions.MERCURY_DIM_KEY || isInOrbit(level)) {
            return 0.5;
        }
        return getGravMultiplier(level);
    }

    /** True when Mars-like weather should push entities/items during storms. */
    public static boolean hasDustStormPush(Level level) {
        return PlanetRegistry.byDimension(level.dimension())
                .map(definition -> definition.skyProfile() == SkyProfile.MARS_LIKE && definition.hasWeather() && !definition.hasOxygen())
                .orElse(false);
    }

    public static double getEngineConstant(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::engineConstant)
                .orElse(1.0);
    }

    public static float getWindMultiplier(Level level) {
        return PlanetRegistry.byDimension(level.dimension())
                .map(PlanetDefinition::wind)
                .map(wind -> wind.multiplier(level))
                .orElse(1.0f);
    }

    public static boolean hasWind(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::wind)
                .map(wind -> wind.clearMultiplier() != 0 || wind.weatherMultiplier() != 0)
                .orElse(true);
    }

    public static boolean isCustomDimension(ResourceLocation resourceLocation) {
        return PlanetRegistry.isCustomDimension(resourceLocation);
    }

    public static long getSeedOffset(ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::seedOffset)
                .orElse(0L);
    }

    public static float getSunMultiplier(ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::sunMultiplier)
                .orElse(1.0f);
    }

    public static boolean isInOrbit(@Nullable ResourceKey<Level> level) {
        return PlanetRegistry.byDimension(level)
                .map(PlanetDefinition::orbitDimension)
                .orElse(false);
    }

    public static boolean isInterplanetary(@Nullable ResourceKey<Level> home, @Nullable ResourceKey<Level> dest) {
        if (home == null || dest == null) {
            return true;
        }
        if (dest == home) {
            return false;
        }
        return requiresInterplanetaryNavigator(home, dest);
    }

    public static boolean requiresInterplanetaryNavigator(@Nullable ResourceKey<Level> home, @Nullable ResourceKey<Level> dest) {
        if (home == null || dest == null || dest == home) {
            return false;
        }
        return PlanetRegistry.byDimension(dest)
                .map(PlanetDefinition::requiresInterplanetaryNavigator)
                .orElse(true);
    }

    public static int getTravelFuelCost(@Nullable ResourceKey<Level> home, @Nullable ResourceKey<Level> dest) {
        return getTravelFuelCost(home, dest, home);
    }

    public static int getReturnFuelCost(@Nullable ResourceKey<Level> home, @Nullable ResourceKey<Level> dest) {
        return getTravelFuelCost(home, dest, dest);
    }

    private static int getTravelFuelCost(@Nullable ResourceKey<Level> home, @Nullable ResourceKey<Level> dest, @Nullable ResourceKey<Level> atmosphereSource) {
        String homeId = getPlanetName(home);
        String destId = getPlanetName(dest);

        int homeX = (int) getPlanetX(homeId);
        int homeY = (int) getPlanetY(homeId);
        int destX = (int) getPlanetX(destId);
        int destY = (int) getPlanetY(destId);

        int distance = (int) (Math.pow(homeX - destX, 2) + Math.pow(homeY - destY, 2));
        distance = Mth.roundToward(distance, 100) / 20;
        int cost = distance + getPlanetAtmosphereCost(atmosphereSource) + 1000;
        return cost * 8;
    }

    public static int getRequiredEngines(@Nullable ResourceKey<Level> home, @Nullable ResourceKey<Level> dest) {
        int homeAtmosphere = getPlanetAtmosphereCost(home) / 100;
        int destinationAtmosphere = getPlanetAtmosphereCost(dest) / 100;

        double gravity = Math.max(getGravMultiplier(dest), getGravMultiplier(home));
        double constant = Math.max(getEngineConstant(dest), getEngineConstant(home));

        return (int) (Mth.clamp(((destinationAtmosphere + homeAtmosphere) * gravity), 6, 64) + constant);
    }

    public static void register() {
        PlanetRegistry.clear();

        // Built-ins intentionally mirror Redux behavior first. New content
        // should be added through PlanetRegistry callbacks or later datapacks.
        registerBuiltIn(PlanetDefinition.builder("earth")
                .dimension(Level.OVERWORLD)
                .orbit(OrbitDefinition.aroundOrigin(earth_origin_x, earth_origin_y, earth_orbit_radius_x, earth_orbit_radius_y, earth_orbit_speed))
                .gravityMultiplier(EARTH_GRAV)
                .temperature(15)
                .oxygen(true)
                .atmosphere(true)
                .atmosphereCost(1600)
                .computingCost(0)
                .engineConstant(1)
                .sunMultiplier(1)
                .wind(WindDefinition.NORMAL)
                .skyProfile(SkyProfile.DEFAULT)
                .telescopeTexture(Northstar.asResource("textures/environment/earth_far.png"))
                .telescopeHiddenIn(Level.OVERWORLD, NorthstarDimensions.MOON_DIM_KEY)
                .reachableByRocket(true)
                .requiresInterplanetaryNavigator(false)
                .hasSky(true)
                .hasWeather(true)
                .heat(0.4, 100)
                .build());

        registerBuiltIn(PlanetDefinition.builder("moon")
                .dimension(NorthstarDimensions.MOON_DIM_KEY)
                .orbit(OrbitDefinition.around("earth", moon_orbit_radius_x, moon_orbit_radius_y, moon_orbit_speed))
                .gravityMultiplier(MOON_GRAV)
                .temperature(-183)
                .oxygen(false)
                .atmosphere(false)
                .atmosphereCost(0)
                .computingCost(50)
                .engineConstant(1)
                .sunMultiplier(1.5f)
                .wind(WindDefinition.NONE)
                .skyProfile(SkyProfile.MOON_LIKE)
                .telescopeTexture(Northstar.asResource("textures/environment/moon_far.png"))
                .telescopeHiddenIn(Level.OVERWORLD, NorthstarDimensions.MOON_DIM_KEY)
                .reachableByRocket(true)
                .hasSky(true)
                .canSeeSkyAtDay(true)
                .hasWeather(false)
                .customDimension(true)
                .seedOffset(3)
                .heat(0, 0)
                .build());

        registerBuiltIn(PlanetDefinition.builder("earth_moon")
                .dimension(NorthstarDimensions.MOON_DIM_KEY)
                .displayNameLangKey("planets.earth_moon.name")
                .orbit(OrbitDefinition.aroundOrigin(earth_moon_origin_x, earth_moon_origin_y, earth_moon_orbit_radius_x, earth_moon_orbit_radius_y, earth_moon_orbit_speed))
                .gravityMultiplier(MOON_GRAV)
                .temperature(-183)
                .oxygen(false)
                .atmosphere(false)
                .telescopeTexture(Northstar.asResource("textures/environment/moon_far.png"))
                .telescopeTooltipId("moon")
                .telescopeHitbox(24, 0)
                .telescopeMoonPhase(true)
                .telescopeVisibleOnlyIn(Level.OVERWORLD)
                .reachableByRocket(true)
                .hasWeather(false)
                .build());

        registerBuiltIn(PlanetDefinition.builder("earth_orbit")
                .dimension(NorthstarDimensions.EARTH_ORBIT_DIM_KEY)
                .orbit(OrbitDefinition.fixed(0, 0))
                .gravityMultiplier(OUTER_MOON_GRAV)
                .temperature(15)
                .oxygen(true)
                .atmosphere(false)
                .atmosphereCost(0)
                .computingCost(0)
                .engineConstant(1)
                .sunMultiplier(1)
                .wind(WindDefinition.NONE)
                .skyProfile(SkyProfile.SPACE)
                .observable(false)
                .reachableByRocket(false)
                .hasSky(true)
                .canSeeSkyAtDay(true)
                .hasWeather(false)
                .orbitDimension(true)
                .build());

        registerBuiltIn(PlanetDefinition.builder("mars")
                .dimension(NorthstarDimensions.MARS_DIM_KEY)
                .orbit(OrbitDefinition.aroundOrigin(mars_origin_x, mars_origin_y, mars_orbit_radius_x, mars_orbit_radius_y, mars_orbit_speed))
                .gravityMultiplier(MARS_GRAV)
                .temperature(-100)
                .oxygen(false)
                .atmosphere(true)
                .atmosphereCost(200)
                .computingCost(400)
                .engineConstant(3)
                .sunMultiplier(1.2f)
                .wind(new WindDefinition(0, 1))
                .skyProfile(SkyProfile.MARS_LIKE)
                .telescopeTexture(Northstar.asResource("textures/environment/mars_far.png"))
                .reachableByRocket(true)
                .hasSky(true)
                .hasWeather(true)
                .customDimension(true)
                .seedOffset(1)
                .heat(0.05, 50)
                .build());

        registerBuiltIn(PlanetDefinition.builder("phobos_deimos")
                .orbit(OrbitDefinition.around("mars", pd_orbit_radius_x, pd_orbit_radius_y, pd_orbit_speed))
                .gravityMultiplier(0.01)
                .temperature(-100)
                .oxygen(false)
                .atmosphere(false)
                .telescopeTexture(Northstar.asResource("textures/environment/phobos_and_deimos_far.png"))
                .telescopeHitbox(5, 5)
                .telescopeHiddenIn(NorthstarDimensions.MARS_DIM_KEY)
                .reachableByRocket(false)
                .hasWeather(false)
                .build());

        registerBuiltIn(PlanetDefinition.builder("venus")
                .dimension(NorthstarDimensions.VENUS_DIM_KEY)
                .orbit(OrbitDefinition.aroundOrigin(venus_origin_x, venus_origin_y, venus_orbit_radius_x, venus_orbit_radius_y, venus_orbit_speed))
                .gravityMultiplier(VENUS_GRAV)
                .temperature(464)
                .oxygen(false)
                .atmosphere(true)
                .atmosphereCost(4000)
                .computingCost(200)
                .engineConstant(9)
                .sunMultiplier(0.6f)
                .wind(new WindDefinition(0.5f, 0.7f))
                .skyProfile(SkyProfile.VENUS_LIKE)
                .telescopeTexture(Northstar.asResource("textures/environment/venus_far.png"))
                .reachableByRocket(true)
                .hasSky(true)
                .hasWeather(true)
                .customDimension(true)
                .seedOffset(4)
                .heat(5, 1000)
                .build());

        registerBuiltIn(PlanetDefinition.builder("mercury")
                .dimension(NorthstarDimensions.MERCURY_DIM_KEY)
                .orbit(OrbitDefinition.aroundOrigin(mercury_origin_x, mercury_origin_y, mercury_orbit_radius_x, mercury_orbit_radius_y, mercury_orbit_speed))
                .gravityMultiplier(MERCURY_GRAV)
                .temperature(400)
                .oxygen(false)
                .atmosphere(false)
                .atmosphereCost(0)
                .computingCost(800)
                .engineConstant(6)
                .sunMultiplier(8)
                .wind(WindDefinition.NONE)
                .skyProfile(SkyProfile.MERCURY_LIKE)
                .telescopeTexture(Northstar.asResource("textures/environment/mercury_far.png"))
                .telescopeHitbox(8, 7)
                .reachableByRocket(true)
                .hasSky(true)
                .canSeeSkyAtDay(true)
                .hasWeather(false)
                .customDimension(true)
                .seedOffset(2)
                .heat(0, 0)
                .build());

        registerObservable("ceres", ceres_origin_x, ceres_origin_y, ceres_orbit_radius_x, ceres_orbit_radius_y, ceres_orbit_speed,
                0.03, -173, false, false, "textures/environment/ceres_far.png", 6, 6);
        registerObservable("jupiter", jupiter_origin_x, jupiter_origin_y, jupiter_orbit_radius_x, jupiter_orbit_radius_y, jupiter_orbit_speed,
                2.53, -166, false, true, "textures/environment/jupiter_far.png", 12, 12);
        registerObservable("saturn", saturn_origin_x, saturn_origin_y, saturn_orbit_radius_x, saturn_orbit_radius_y, saturn_orbit_speed,
                1.06, -176, false, true, "textures/environment/saturn_far.png", 8, 8);
        registerObservable("uranus", uranus_origin_x, uranus_origin_y, uranus_orbit_radius_x, uranus_orbit_radius_y, uranus_orbit_speed,
                0.9, -195, false, true, "textures/environment/uranus_far.png", 8, 7);
        registerObservable("neptune", neptune_origin_x, neptune_origin_y, neptune_orbit_radius_x, neptune_orbit_radius_y, neptune_orbit_speed,
                1.14, -200, false, true, "textures/environment/neptune_far.png", 8, 7);
        registerObservable("pluto", pluto_origin_x, pluto_origin_y, pluto_orbit_radius_x, pluto_orbit_radius_y, pluto_orbit_speed,
                0.06, -225, false, false, "textures/environment/pluto_far.png", 6, 6);
        registerObservable("eris", eris_origin_x, eris_origin_y, eris_orbit_radius_x, eris_orbit_radius_y, eris_orbit_speed,
                0.08, -230, false, false, "textures/environment/eris_far.png", 6, 6);

        PlanetRegistry.applyRegistrationCallbacks();
        PlanetRegistry.tick(0);
        syncLegacyCoordinates();
    }

    private static void registerObservable(String id, double originX, double originY, double radiusX, double radiusY, double speed,
                                           double gravityMultiplier, int temperature, boolean hasOxygen, boolean hasAtmosphere,
                                           String telescopeTexture, int telescopeHitRadius, int telescopeHitOffset) {
        registerBuiltIn(PlanetDefinition.builder(id)
                .orbit(OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed))
                .gravityMultiplier(gravityMultiplier)
                .temperature(temperature)
                .oxygen(hasOxygen)
                .atmosphere(hasAtmosphere)
                .telescopeTexture(Northstar.asResource(telescopeTexture))
                .telescopeHitbox(telescopeHitRadius, telescopeHitOffset)
                .reachableByRocket(false)
                .hasWeather(false)
                .wind(WindDefinition.NONE)
                .build());
    }

    private static void registerBuiltIn(PlanetDefinition definition) {
        PlanetRegistry.register(definition);
    }

    private static void syncLegacyCoordinates() {
        mercury_x = PlanetRegistry.x("mercury");
        mercury_y = PlanetRegistry.y("mercury");
        venus_x = PlanetRegistry.x("venus");
        venus_y = PlanetRegistry.y("venus");
        earth_x = PlanetRegistry.x("earth");
        earth_y = PlanetRegistry.y("earth");
        earth_moon_x = PlanetRegistry.x("earth_moon");
        earth_moon_y = PlanetRegistry.y("earth_moon");
        moon_x = PlanetRegistry.x("moon");
        moon_y = PlanetRegistry.y("moon");
        mars_x = PlanetRegistry.x("mars");
        mars_y = PlanetRegistry.y("mars");
        pd_x = PlanetRegistry.x("phobos_deimos");
        pd_y = PlanetRegistry.y("phobos_deimos");
        ceres_x = PlanetRegistry.x("ceres");
        ceres_y = PlanetRegistry.y("ceres");
        jupiter_x = PlanetRegistry.x("jupiter");
        jupiter_y = PlanetRegistry.y("jupiter");
        saturn_x = PlanetRegistry.x("saturn");
        saturn_y = PlanetRegistry.y("saturn");
        uranus_x = PlanetRegistry.x("uranus");
        uranus_y = PlanetRegistry.y("uranus");
        neptune_x = PlanetRegistry.x("neptune");
        neptune_y = PlanetRegistry.y("neptune");
        pluto_x = PlanetRegistry.x("pluto");
        pluto_y = PlanetRegistry.y("pluto");
        eris_x = PlanetRegistry.x("eris");
        eris_y = PlanetRegistry.y("eris");
    }

}
