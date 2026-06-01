# Planet Unhardcoding Development Notes

This document is the English maintainer overview for
`Create: Northstar - Redux Unhardcoded`. It is meant for future maintainers,
Java extension mod authors, KubeJS pack authors, and modpack documentation.

The current target is Minecraft 1.21.1 on NeoForge. Phase 1 is a structure
refactor: represent existing Northstar Redux planet behavior through a shared
data model before adding new planet content.

## Development Direction

This fork is not meant to compete with upstream long term. It exists as a
temporary hardcode-removal bridge until upstream provides official
data-driven planet support. It may also carry confirmed gameplay/content bug
fixes inherited from upstream Redux when those fixes are practical to maintain
in this fork.

Current direction:

- Java API is the primary entry point.
- KubeJS adapts to the Java API and must not introduce a separate planet model.
- Datapack/JSON planet loading is not a current target; pack-facing
  configuration uses KubeJS.
- Existing `planet` strings in saves, star maps, and return tickets must remain
  compatible.
- New planets should not require new planet-specific `if`/`switch` branches in
  `TelescopeScreen`, `RocketStationBlockEntity`, or `NorthstarPlanets`.
- Gameplay/content bug fixes inherited from upstream are acceptable when they
  are scoped and do not distract from the fork's hardcode-removal direction.

## Development Phases

### Phase 1: Structural Unhardcoding

Phase 1 is a structure refactor, not a content pass. The goal is to represent
existing Redux behavior through `PlanetDefinition` and `PlanetRegistry`:

- Built-in earth, moon, mars, mercury, venus, earth orbit, and telescope bodies
  are registered as shared definitions.
- Telescope, star maps, rocket station, oxygen, temperature, gravity, solar,
  wind, and sky-profile systems query the registry.
- Java mods register planets through `PlanetRegistry.registerCallback(...)`.
- KubeJS startup scripts use `NorthstarEvents.planets(...)` over the same
  builder and registry.
- Existing ids, star maps, return tickets, and save compatibility are preserved.

The acceptance target is not "many new planets"; it is "old behavior still
works, and a test planet does not require editing telescope or rocket-station
planet branches."

### Phase 2: Make Compatibility Logic Explicit Data

Phase 2 continues after Phase 1 is stable. The focus is not JSON loading or new
planet content; it is turning behavior still hidden behind helpers or renderer
profiles into explicit API data.

Priority areas:

- Data-driven entity-category gravity. `gravityMultiplier` is the primary value
  today, but boats, minecarts, arrows, item entities, throwable projectiles, and
  living entities may still use helper-backed legacy values. Those differences
  can become explicit `PlanetDefinition` fields or a sub-model.
- Data-driven sky, fog, and weather profiles. New planets currently reuse
  `SPACE`, `MOON_LIKE`, `MARS_LIKE`, `VENUS_LIKE`, or `MERCURY_LIKE`.
  Later work can split colors, fog density, clouds, weather particles, horizon,
  and sky objects into reusable profile descriptors.
- Continue improving the Java API and KubeJS adapter while keeping both backed
  by the same `PlanetDefinition` model.
- Keep validating a registry-registered test planet across telescope, star map,
  rocket target, fuel/engine, oxygen, temperature, gravity, solar, wind, and sky
  profile lookups.

Explicit non-goals for the default Phase 2 path:

- Do not add official planet content before built-in behavior is represented by
  shared definitions.
- Do not implement `data/<namespace>/northstar/planets/*.json` loading.
- Do not add a planet JSON schema or KubeJS planet JSON generation.
- Do not automatically create dimensions, biomes, chunk generators, or
  worldgen.
- Do not compete with upstream long term; if upstream ships equivalent support,
  reduce duplicated fork maintenance.
- Do not turn general upstream maintenance into this fork's main purpose; keep
  inherited gameplay/content bug fixes focused.

## Core Model

The public API lives in:

```text
com.lightning.northstar.api.planet
```

Main types:

- `PlanetDefinition`
- `PlanetRegistry`
- `PlanetRegistrar`
- `OrbitDefinition`
- `PlanetPosition`
- `SkyProfile`
- `WindDefinition`

`PlanetDefinition` carries stable ids, destination dimensions, display keys,
orbits, gravity, temperature, oxygen/atmosphere, cost values, engine constants,
solar/wind multipliers, sky profiles, telescope textures/hitboxes, rocket
reachability, and interplanetary navigator requirements.

Registering a planet does not create the dimension, worldgen, biomes, language
entries, or textures. Those must come from the registering mod, KubeJS pack,
datapack, or resource pack.

## Extension Entry Points

Third-party Java mods should use the public API under
`com.lightning.northstar.api.planet`, especially
`PlanetRegistry.registerCallback(...)`. They should not import or patch
Northstar internals such as `NorthstarPlanets`, `TelescopeScreen`, rocket block
entities, or mixins.

KubeJS scripts belong in:

```text
kubejs/startup_scripts/
```

KubeJS uses `NorthstarEvents.planets(...)` and delegates to the same underlying
builder/registry model as Java mods.

## Compatibility

Handle these legacy ids carefully:

- `earth`
- `moon`
- `earth_moon`
- `mars`
- `venus`
- `mercury`
- `earth_orbit`
- `jupiter`
- `saturn`
- `uranus`
- `neptune`
- `pluto`
- `eris`
- `ceres`
- `phobos_deimos`

`earth_moon` is a legacy telescope/star-map id. The moon dimension's primary
definition remains `moon`. `earth_orbit` has a dimension definition but is not a
normal rocket target id.

## Sky And Weather

Phase 1 does not implement a fully data-driven sky renderer. New planets reuse:

- `DEFAULT`
- `SPACE`
- `MOON_LIKE`
- `MARS_LIKE`
- `VENUS_LIKE`
- `MERCURY_LIKE`

These profiles select existing Northstar sky, fog, weather, and some legacy
temperature behavior. Later work can split colors, fog density, clouds, weather
particles, and sky objects into real data fields.

## Acceptance Direction

Phase 1 should preserve existing main planet behavior, old star maps, return
tickets, telescope UI, and rocket station flows. A registry-registered test
planet should work through telescope rendering, star-map printing, rocket
targeting, fuel/engine calculations, oxygen, temperature, gravity, solar, wind,
weather, and sky profile lookups without per-planet UI or rocket branches.

Detailed docs:

- `docs/en_us/PLANET_API.md`
- `docs/en_us/KUBEJS_PLANETS.md`
- `docs/en_us/KNOWN_LIMITATIONS.md`
