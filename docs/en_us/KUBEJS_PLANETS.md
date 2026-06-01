# KubeJS Planets

Northstar exposes a startup-script event for planet registration:

```js
NorthstarEvents.planets(event => {
  event.createPlanet("example_test")
    .dimension("examplemod:test_planet")
    .displayNameLangKey("planets.example_test.name")
    .orbit(OrbitDefinition.aroundOrigin(0, 0, 320, 300, Math.PI / 90000))
    .gravityMultiplier(0.52)
    .temperature(-45)
    .oxygen(false)
    .atmosphere(true)
    .atmosphereCost(300)
    .computingCost(500)
    .engineConstant(4)
    .sunMultiplier(0.8)
    .wind(new WindDefinition(0.2, 0.9))
    .skyProfile(SkyProfile.MARS_LIKE)
    .telescopeTexture("examplemod:textures/environment/test_planet_far.png")
    .reachableByRocket(true)
    .requiresInterplanetaryNavigator(true)
    .hasSky(true)
    .hasWeather(true)
    .customDimension(true)
    .heat(0.08, 70)
})
```

Place this in `kubejs/startup_scripts/`. A game restart is expected after
changing startup scripts.

Registering a planet does not create a dimension or texture. The dimension id,
language entry, and texture asset must already be provided by a mod, KubeJS pack,
datapack, or resource pack.

## Exposed Bindings

The following Java API classes are available to startup scripts:

- `PlanetDefinition`
- `PlanetRegistry`
- `OrbitDefinition`
- `WindDefinition`
- `SkyProfile`

`NorthstarEvents.planets` is an adapter over the Java API. Scripted planets,
Java mod planets, and built-in planets all resolve to `PlanetDefinition` entries.

## ProbeJS Support

Northstar exposes a KubeJS-specific `KubePlanetBuilder` wrapper for
`event.createPlanet(id)`. It delegates to the Java `PlanetDefinition.Builder`
but carries KubeJS `@Info`/`@Param` metadata so ProbeJS can generate cleaner
chain-method completions.

Use ProbeJS' normal dump flow after starting with KubeJS and ProbeJS installed.
Northstar only exposes the annotated Java/KubeJS API; it does not patch
ProbeJS-generated `.d.ts` files.
Completions should include:

- `NorthstarEvents.planets(...)`
- `event.createPlanet(id)`
- chain methods such as `.dimension(...)`, `.orbit(...)`,
  `.gravityMultiplier(...)`, `.reachableByRocket(...)`, `.heat(...)`
- globals such as `OrbitDefinition`, `WindDefinition`, and `SkyProfile`

If completions look stale, restart the game and regenerate ProbeJS typings.
Startup scripts are not hot-reloaded.

For the current 1.21.1 NeoForge development runtime, ProbeJS `7.7.2` can emit
some invalid TypeScript declaration syntax from unrelated Java/KubeJS/Create
classes. That is tracked as a ProbeJS/KubeJS tooling limitation, not a
Northstar runtime behavior. Northstar's supported part is keeping method
documentation sourced from Java `@Info`/`@Param` annotations.

## Builder Methods

Common identity and destination methods:

- `.dimension("namespace:path")`
- `.displayNameLangKey("planets.example.name")`
- `.observable(true | false)`
- `.reachableByRocket(true | false)`
- `.requiresInterplanetaryNavigator(true | false)`

Orbit and telescope:

- `.orbit(OrbitDefinition.fixed(x, y))`
- `.orbit(OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed))`
- `.orbit(OrbitDefinition.around("parent_id", radiusX, radiusY, speed))`
- `.telescopeTexture("namespace:textures/environment/body_far.png")`
- `.telescopeTooltipId("other_id")`
- `.telescopeHitbox(radius, offset)`
- `.telescopeMoonPhase(true | false)`

Telescope coordinates are direct UI-space positions, not automatically scaled
solar-system units or Minecraft world coordinates. The telescope background is a
900x900 scrollable canvas and the screen only shows a smaller window into that
canvas. For quick smoke tests, start with fixed positions near the center such
as `OrbitDefinition.fixed(-80, 70)`. Large orbit radii can put a body outside
the current telescope viewport until the player drags the view.

Orbit definitions are evaluated every world tick:

- `OrbitDefinition.fixed(x, y)` keeps the body at one telescope position.
- `OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed)`
  moves the body around a fixed telescope-space center.
- `OrbitDefinition.around("parent_id", radiusX, radiusY, speed)` moves the body
  around another registered body's current telescope-space position.

The moving orbit formula is approximately:

```text
x = centerX + cos(worldTime * speed) * radiusX
y = centerY + sin(worldTime * speed) * radiusY
```

Different `radiusX` and `radiusY` values create an ellipse. Higher `speed`
values rotate faster. Northstar currently also uses these telescope/orbit
positions for travel-distance fuel estimates, so fuel cost can change as bodies
move.

Environment:

- `.gravityMultiplier(value)`
- `.temperature(celsius)`
- `.oxygen(true | false)`
- `.atmosphere(true | false)`
- `.sunMultiplier(value)`
- `.wind(new WindDefinition(clearMultiplier, weatherMultiplier))`
- `.skyProfile(SkyProfile.DEFAULT)`
- `.skyProfile(SkyProfile.SPACE)`
- `.skyProfile(SkyProfile.MOON_LIKE)`
- `.skyProfile(SkyProfile.MARS_LIKE)`
- `.skyProfile(SkyProfile.VENUS_LIKE)`
- `.skyProfile(SkyProfile.MERCURY_LIKE)`
- `.hasSky(true | false)`
- `.canSeeSkyAtDay(true | false)`
- `.hasWeather(true | false)`
- `.customDimension(true | false)`
- `.orbitDimension(true | false)`

Rocket:

- `.atmosphereCost(value)`
- `.computingCost(value)`
- `.engineConstant(value)`
- `.heat(heatRating, heatConstant)`

Compatibility:

- `.seedOffset(value)`

Some Java vararg methods currently expect `ResourceKey<Level>` values and are
not ergonomic in scripts yet, including `.telescopeHiddenIn(...)` and
`.telescopeVisibleOnlyIn(...)`. They are available to Java callers; KubeJS
string overloads can be added later if pack scripts need them.

## Reachable Planet Example

Use this shape when the body has an actual dimension and should be a rocket
target:

```js
NorthstarEvents.planets(event => {
  event.createPlanet("pack_asteroid")
    .dimension("pack:asteroid")
    .orbit(OrbitDefinition.around("earth", 80, 55, Math.PI / 60000))
    .gravityMultiplier(0.08)
    .temperature(-90)
    .oxygen(false)
    .atmosphere(false)
    .atmosphereCost(0)
    .computingCost(300)
    .engineConstant(2)
    .sunMultiplier(1.4)
    .wind(WindDefinition.NONE)
    .skyProfile(SkyProfile.SPACE)
    .telescopeTexture("pack:textures/environment/asteroid_far.png")
    .reachableByRocket(true)
    .hasSky(true)
    .canSeeSkyAtDay(true)
    .hasWeather(false)
    .customDimension(true)
})
```

## Telescope-Only Example

Use this shape for comets, distant planets, moons without dimensions, or flavor
bodies that should be visible but not travel targets:

```js
NorthstarEvents.planets(event => {
  event.createPlanet("pack_comet")
    .orbit(OrbitDefinition.fixed(-80, 70))
    .gravityMultiplier(0.01)
    .temperature(-210)
    .oxygen(false)
    .atmosphere(false)
    .wind(WindDefinition.NONE)
    .telescopeTexture("pack:textures/environment/comet_far.png")
    .telescopeHitbox(5, 5)
    .reachableByRocket(false)
    .hasWeather(false)
})
```

## Overriding Existing Planets

KubeJS uses `registerOrReplace(...)` internally. That means a startup script can
replace a built-in id if needed for a pack.

Do this deliberately. Existing item data stores planet ids as strings, so
renaming or removing ids such as `mars`, `moon`, or `venus` can break old star
maps, readings, and return tickets.

## Current Limits

- KubeJS planet registration is startup-time only.
- JSON/datapack planet loading is not a current target; use KubeJS for scripted
  pack configuration.
- Sky and fog are selected through `SkyProfile` presets, not fully scripted
  color or renderer data.
- Registering a planet does not generate a dimension.
