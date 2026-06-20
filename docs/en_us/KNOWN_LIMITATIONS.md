# Known Limitations

Create: Northstar - Redux Unhardcoded is currently focused on removing
hardcoded behavior and exposing extension hooks. The registry removes many
hardcoded planet branches, but this is not a complete data-driven solar-system
framework yet.

## Not Implemented Yet

- JSON/datapack planet loading. This is intentionally not a current target;
  KubeJS is the supported pack-facing configuration path.
- A published JSON schema for planet files. This is tied to the non-goal above.
- KubeJS data generation for planet JSON. Startup-script registration is the
  preferred KubeJS path.
- Automatic dimension, biome, chunk-generator, or worldgen creation.
- Fully data-driven sky, fog, cloud, weather particle, and horizon rendering.
- Per-planet renderer colors beyond the current `SkyProfile` presets.
- Per-planet rocket fuel requirements. Fuel fluids can already be defined
  through the `FuelType` data registry, but planets cannot yet require a
  specific fuel type, fuel tag, or fuel category such as fantasy fuels.

## Current Compatibility Choices

Existing ids are kept for old item and save compatibility. Some of these are not
physically clean API concepts, but they preserve Redux behavior:

- `earth_orbit` exists as a dimension definition but is not a rocket target id.
- `earth_moon` remains an observable legacy id while `moon` remains the primary
  definition for the moon dimension.
- Observable bodies such as `jupiter`, `saturn`, `uranus`, `neptune`, `pluto`,
  `eris`, `ceres`, and `phobos_deimos` are registry entries even when they are
  not reachable by rocket.

## Gravity

`gravityMultiplier` is the primary planet value, but some entity categories keep
legacy compatibility helpers. Boats, minecarts, arrows, items, throwable
projectiles, and living entities did not all use identical hardcoded values in
Redux.

For compatibility, these category-specific differences are centralized behind
`NorthstarPlanets` helper methods. They are not yet explicit
`PlanetDefinition` fields.

## Sky Profiles

The first renderer pass supports reusable profiles:

- `DEFAULT`
- `SPACE`
- `MOON_LIKE`
- `MARS_LIKE`
- `VENUS_LIKE`
- `MERCURY_LIKE`

These profiles choose existing Northstar sky/fog/weather paths. They are meant
to let new planets reuse known behavior until the renderer can be split into
actual data fields.

## KubeJS Ergonomics

The common path works from startup scripts, including string overloads for
dimension ids and telescope textures. Some Java-only methods still need better
script-facing overloads, especially telescope visibility restrictions that take
`ResourceKey<Level>` varargs.

## Remaining Hardcoded Areas

The current work focuses on planet selection, rocket targeting, telescope
rendering, environment lookups, and compatibility helpers. Other content can
still be hardcoded in normal mod systems:

- dimension JSON/worldgen and biome placement
- mobs and spawning rules
- blocks, fluids, and item progression
- planet-specific recipes or loot
- renderer constants inside individual sky profiles

Before adding new planet content, prefer adding missing fields to
`PlanetDefinition` and routing systems through the registry.
