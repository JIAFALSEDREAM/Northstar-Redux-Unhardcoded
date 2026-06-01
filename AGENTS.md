# Agent Notes

This repository is the temporary `Create: Northstar - Redux Unhardcoded` fork of
`Astronauts-of-Create/Northstar-Redux`.

## Project direction

- The fork exists to remove hardcoded planet behavior and make custom planets
  possible for Java mods, KubeJS packs, and modpacks.
- The fork may also carry confirmed gameplay/content bug fixes inherited from
  upstream Redux, as long as they stay scoped and maintainable.
- Do not compete with upstream long term. If upstream ships equivalent
  data-driven planet support, this fork should stop carrying duplicate work.
- Phase 1 is a structure refactor. Do not add new planet content before the
  existing built-in behavior is represented by shared planet definitions.

## Current target architecture

- Maintain and extend the `PlanetDefinition` model covering identity, dimension,
  display/lang key, orbit, gravity, temperature, oxygen/atmosphere, atmosphere
  cost, computing cost, engine constant, sun/wind multipliers, sky profile,
  telescope icon/texture, rocket reachability, and interplanetary navigator
  requirement.
- Maintain `PlanetRegistry` as the central query and registration API.
- Keep static Java registration acceptable for built-ins. Public Java mod API
  registration and KubeJS startup registration are the supported extension
  paths. Datapack/JSON planet loading is not a current target.
- Put public extension types under `com.lightning.northstar.api`, preferably in
  an `api.planet` package. Third-party Java mods should not need to import
  internal world, screen, rocket, or mixin classes to add a planet.
- Third-party Java mods should use `PlanetRegistry.registerCallback(...)` so
  registration is not sensitive to mod constructor ordering.
- KubeJS must be an adapter over the same builder/registry used by Java mods.
  The intended pack-facing shape is a startup-script event such as
  `NorthstarEvents.planets(event => event.createPlanet(...))`.
- Keep string planet ids compatible with existing saves, star maps, and return
  tickets.
- Model telescope-only bodies explicitly, either as observable celestial bodies
  or as unreachable planet definitions.

## Development baseline

- Java API is the source of truth. KubeJS should expose that same model for
  pack authors.
- Keep public extension docs current when changing planet API behavior:
  `docs/en_us/PLANET_API.md`, `docs/en_us/KUBEJS_PLANETS.md`, and
  `docs/en_us/KNOWN_LIMITATIONS.md`, plus the matching `docs/zh_cn/`
  counterparts.
- Keep project-direction and release-facing docs current when changing scope,
  release positioning, or documented limitations:
  `docs/en_us/PLANET_UNHARDCODED.md`, `docs/zh_cn/PLANET_UNHARDCODED.md`,
  `docs/en_us/CHANGELOG_UNHARDCODED.md`, `docs/zh_cn/CHANGELOG_UNHARDCODED.md`,
  and `README.md`.
- Prefer a sealed registration phase: built-in planets, Java mod event
  registrations, and KubeJS startup registrations should complete before systems
  such as rocket stations, telescope UI, gravity, oxygen, temperature, solar, and
  wind query the registry.
- Avoid runtime-only KubeJS behavior that cannot be represented by
  `PlanetDefinition`; it will make Java mod and KubeJS behavior diverge.
- Do not name the KubeJS method `createPlant`; use `createPlanet` unless there
  is a deliberate separate API for flora.
- Do not spend current fork effort on planet JSON generation/loading unless the
  project direction changes again.

## Hardcoded areas to inspect

- `NorthstarDimensions`
- `NorthstarPlanets`
- `TelescopeScreen`
- `RocketStationBlockEntity`
- `RocketStationMenu`
- `NorthstarCreativeModeTab`
- Gravity mixins under `mixin/gravitystuff` and related entity gravity code
- Oxygen, temperature, solar panel, windmill, weather, sky, and fog logic
- `LevelRendererMixin` and `FogRendererMixin`

## Refactor constraints

- Preserve behavior for earth, moon, mars, mercury, venus, and earth orbit.
- Preserve telescope behavior for jupiter, saturn, uranus, neptune, pluto, eris,
  ceres, and other existing observable bodies.
- Do not break persisted `planet` string data.
- Avoid adding planet-specific branches to UI and rocket code. New planet support
  should flow through registry data.
- For the first sky pass, prefer reusable sky profiles: `space`, `moon_like`,
  `mars_like`, `venus_like`, `mercury_like`, and `default`.

## Verification expectations

- Existing star maps, return tickets, telescope UI, and rocket station flows
  should still work.
- A registry-registered test planet should be usable for telescope rendering,
  star map printing, rocket targeting, fuel/engine calculations, oxygen,
  temperature, gravity, sun, and wind lookups without editing per-planet
  branches.
- Run the narrowest useful Gradle checks after code changes. If a check cannot
  run locally, document the reason.
