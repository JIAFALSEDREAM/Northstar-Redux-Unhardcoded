# Planet API

This document describes the Java-facing planet API for the
`Create: Northstar - Redux Unhardcoded` fork.

The API target is Minecraft 1.21.1 on NeoForge. It is intentionally Java-first:
third-party mods should register planet definitions through public classes under
`com.lightning.northstar.api.planet`, and KubeJS support adapts to the same model
instead of introducing separate planet behavior.

## Registration Lifecycle

Northstar registers built-in planet definitions first, then applies Java mod
callbacks, then KubeJS startup registrations.

Use `PlanetRegistry.registerCallback(...)` from your mod setup code. This avoids
depending on Northstar constructor ordering.

```java
import com.lightning.northstar.api.planet.OrbitDefinition;
import com.lightning.northstar.api.planet.PlanetDefinition;
import com.lightning.northstar.api.planet.PlanetRegistry;
import com.lightning.northstar.api.planet.SkyProfile;
import com.lightning.northstar.api.planet.WindDefinition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class ExamplePlanets {
    public static final ResourceKey<Level> TEST_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("examplemod", "test_planet")
    );

    public static void register() {
        PlanetRegistry.registerCallback(planets -> planets.register(
                PlanetDefinition.builder("example_test")
                        .dimension(TEST_DIMENSION)
                        .displayNameLangKey("planets.example_test.name")
                        .orbit(OrbitDefinition.aroundOrigin(0, 0, 320, 300, Math.PI / 90000))
                        .gravityMultiplier(0.52)
                        .temperature(-45)
                        .oxygen(false)
                        .atmosphere(true)
                        .atmosphereCost(300)
                        .computingCost(500)
                        .engineConstant(4)
                        .sunMultiplier(0.8f)
                        .wind(new WindDefinition(0.2f, 0.9f))
                        .skyProfile(SkyProfile.MARS_LIKE)
                        .telescopeTexture(ResourceLocation.fromNamespaceAndPath(
                                "examplemod",
                                "textures/environment/test_planet_far.png"
                        ))
                        .reachableByRocket(true)
                        .requiresInterplanetaryNavigator(true)
                        .hasSky(true)
                        .hasWeather(true)
                        .customDimension(true)
                        .heat(0.08, 70)
                        .build()
        ));
    }
}
```

Registering a planet does not create a dimension, biome source, chunk generator,
texture asset, language entry, or worldgen data. The target dimension and assets
must already exist in the registering mod or pack.

## PlanetDefinition Fields

`PlanetDefinition` is the shared source of truth for rocket, telescope,
environment, and first-pass rendering behavior.

Core identity:

- `id`: Stable string id stored on star maps, return tickets, readings, and
  saves. Do not rename ids casually.
- `dimension`: Dimension reached by rockets. `null` is valid for telescope-only
  bodies.
- `displayNameLangKey`: Translation key used by UI. Defaults to
  `planets.<id>.name`.

Orbit and telescope:

- `orbit`: 2D telescope/orbit position, also used by fuel distance calculations.
- `telescopeTexture`: Sprite used by telescope UI.
- `telescopeTooltipId`: Optional alias for tooltip translation keys such as
  `planets.<id>.grav`.
- `telescopeHitbox(radius, offset)`: Hover/click hitbox data in telescope screen
  pixels.
- `telescopeMoonPhase`: Uses the vanilla moon phase texture path for this body.
- `telescopeHiddenIn(...)`: Hides this body in selected dimensions.
- `telescopeVisibleOnlyIn(...)`: Restricts this body to selected dimensions.
- `observable`: Controls whether the body appears in telescope listings.

Environment:

- `gravityMultiplier`: Base entity gravity multiplier, where overworld is `1.0`.
- `temperature`: Ambient temperature in Celsius, unless a sky profile has
  legacy special handling.
- `oxygen`: Whether entities can breathe without oxygen systems.
- `atmosphere`: Whether atmosphere machines should treat the body as
  atmospheric.
- `sunMultiplier`: Solar panel output multiplier.
- `wind`: Clear-weather and rainy/stormy windmill multipliers.
- `hasSky`, `canSeeSkyAtDay`, `hasWeather`: Sky/weather behavior flags.
- `customDimension`: Marks Northstar-style custom dimensions for compatibility
  checks.
- `orbitDimension`: Marks space/orbit dimensions that need orbit-style gravity
  handling.

Rocket:

- `reachableByRocket`: Whether star maps and rockets can target this body.
- `requiresInterplanetaryNavigator`: Whether travel requires the navigator item.
- `atmosphereCost`: Fuel/engine contribution for atmospheric launch.
- `computingCost`: Targeting/computing difficulty.
- `engineConstant`: Additional engine requirement constant.
- `heat(heatRating, heatConstant)`: Heat shielding values used by rocket checks.

Generation compatibility:

- `seedOffset`: Existing Northstar dimension seed offset compatibility value.

## OrbitDefinition

`OrbitDefinition` currently provides a simple 2D model:

- `OrbitDefinition.fixed(x, y)`: Static body in telescope space.
- `OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed)`:
  Elliptical orbit around an absolute origin.
- `OrbitDefinition.around(parentId, radiusX, radiusY, speed)`: Elliptical orbit
  around another registered planet id.

`speed` is radians per tick-time unit. Built-in values use expressions such as
`Math.PI / 100000`.

## Registry Queries

Systems should query `PlanetRegistry` instead of hardcoding dimension checks:

- `PlanetRegistry.byId(id)`
- `PlanetRegistry.byDimension(dimension)`
- `PlanetRegistry.all()`
- `PlanetRegistry.position(id)`, `x(id)`, `y(id)`

Dimension lookup returns the first registered primary definition for a dimension.
This preserves legacy aliases such as `earth_moon`: the moon dimension remains
owned by `moon`, while `earth_moon` can still exist as an observable legacy id.

## Save Compatibility

The `id` is persisted by old star maps, readings, return tickets, and other item
data. Keep existing ids stable:

- `earth`
- `moon`
- `earth_moon`
- `mars`
- `venus`
- `mercury`
- `earth_orbit`
- observable bodies such as `jupiter`, `saturn`, `uranus`, `neptune`, `pluto`,
  `eris`, `ceres`, and `phobos_deimos`

New Java mods should use namespaced-looking unique ids in practice, even though
the current API stores plain strings, for example `examplemod_test_planet`.

## Reachable And Observable Bodies

Use a normal reachable planet for a rocket destination:

```java
.dimension(TEST_DIMENSION)
.observable(true)
.reachableByRocket(true)
```

Use a telescope-only body when there is no destination dimension:

```java
PlanetDefinition.builder("example_comet")
        .dimension(null)
        .orbit(OrbitDefinition.aroundOrigin(0, 0, 620, 180, Math.PI / 240000))
        .oxygen(false)
        .atmosphere(false)
        .telescopeTexture(ResourceLocation.fromNamespaceAndPath(
                "examplemod",
                "textures/environment/example_comet_far.png"
        ))
        .reachableByRocket(false)
        .build();
```

## Extension Rules

- Do not import or patch `NorthstarPlanets`, `TelescopeScreen`,
  `RocketStationBlockEntity`, mixins, or renderer internals from third-party
  mods.
- Prefer `register(...)` for new Java mod ids. `registerOrReplace(...)` exists
  mainly for KubeJS/script-style override layers.
- Keep behavior representable in `PlanetDefinition` before adding a new
  system-specific branch.
- If a value cannot be represented yet, document the missing field instead of
  hiding behavior in a one-off dimension check.
