# Unhardcoded Fork Change Log

This file records local fork work that is not yet upstream Redux behavior.

## 2026-05-28

- Defined the fork direction in `README.md`: temporary unhardcoded fork, Java API
  first, KubeJS as an adapter.
- Added `AGENTS.md` with local development rules, target architecture, hardcoded
  areas to inspect, and verification expectations.
- Added public planet API foundation under `com.lightning.northstar.api.planet`:
  - `PlanetDefinition`
  - `PlanetRegistry`
  - `PlanetRegistrar`
  - `OrbitDefinition`
  - `PlanetPosition`
  - `SkyProfile`
  - `WindDefinition`
- Reworked `NorthstarPlanets` into a compatibility facade:
  - Keeps existing public static fields and methods for current callers.
  - Registers built-in bodies into `PlanetRegistry`.
  - Keeps built-in ids and persisted string compatibility such as `earth`,
    `earth_moon`, `moon`, `mars`, `venus`, `mercury`, and observable bodies.
  - Preserves current `earth_orbit` oxygen fallback behavior by registering it
    with `oxygen(true)`.
- Added `PlanetRegistry.registerCallback(...)` so third-party Java mods can
  register planets without depending on constructor ordering.
- Wired `NorthstarTemperature.getHeatRating` and `getHeatConstant` to
  `PlanetDefinition` data.
- Wired `WeatherStuff.hasWeather` through `NorthstarPlanets.hasWeather`.
- Exposed early KubeJS bindings for `PlanetRegistry`, `PlanetDefinition`,
  `OrbitDefinition`, `WindDefinition`, and `SkyProfile`.
- Added KubeJS startup event `NorthstarEvents.planets`.
  - `event.createPlanet(id)` returns a `PlanetDefinition.Builder`.
  - `event.planet(builder)` and `event.planet(definition)` are also supported.
  - Scripted planets use `PlanetRegistry.registerOrReplace(...)` through the
    same registry path as Java-defined planets.
- Centralized rocket station travel fuel, return fuel, required engine count,
  and interplanetary navigator checks behind `NorthstarPlanets` registry-backed
  helper methods.
- Reworked `RocketStationBlockEntity` and `RocketStationMenu` to call those
  shared helpers instead of each carrying the fuel formula.
- Reworked `TelescopeScreen` planet rendering, tooltip construction, click
  selection, and selected-body sprite lookup to iterate `PlanetRegistry.all()`
  instead of keeping one draw/tooltip/click block per body.
  - Preserved the special overworld moon-phase render path for `earth_moon`.
  - Preserved current-dimension hiding behavior for reachable bodies and the
    existing `earth_moon` string id for printed readings/star maps.
- Fixed duplicate dimension indexing in `PlanetRegistry` so `moon` remains the
  primary definition for the moon dimension while `earth_moon` can still exist
  as an observable/legacy star-map id.
- Centralized gravity lookups behind `NorthstarPlanets` helper methods:
  - Living entities, items, throwable projectiles, boats, minecarts, arrows, and
    client walk-animation scaling now query shared helpers.
  - Existing category-specific legacy values are preserved for built-in planets
    where boats, minecarts, arrows, and item/projectile Venus gravity differed
    from the planet gravity multiplier.
  - Mars dust push checks now use a registry/profile-backed predicate.
- Moved atmospheric concentrator planet behavior to registry data:
  - No atmosphere produces nothing.
  - Oxygen atmospheres produce oxygen.
  - Non-oxygen atmospheres produce carbon, preserving Mars/Venus behavior.
- Moved Mercury base temperature handling behind `NorthstarPlanets` and allowed
  `SkyProfile.MERCURY_LIKE` definitions to reuse the day/night behavior.
- Added `NorthstarPlanets.getSkyProfile(...)` and changed fog/weather/sky
  rendering branches to select existing rendering paths by `SkyProfile` instead
  of direct dimension checks.
- Reworked creative-tab star-map generation to iterate reachable registry
  planets and emit one primary map per dimension, instead of hardcoding
  earth/moon/mars/mercury/venus.
- Rocket in-flight display now reports required engine count through the same
  registry-backed helper used by rocket station assembly.
- Added a local Copycats development fallback:
  - GitHub Packages credentials are now optional at Gradle configuration time.
  - Passing `-Pnorthstar.localCopycatsJar=<path-to-copycats.jar>` uses a local
    jar for compile classpath verification when GitHub Packages is unavailable.
  - A `copycats-*.jar` placed in `.gradle/local-libs/` is now also detected
    automatically for local compile classpath without forcing Copycats to load
    in the dev runtime.
  - The Copycats GitHub Packages repository is restricted to the
    `com.copycatsplus` group so Gradle does not probe unrelated dependencies
    against that private repository.
- Added API-oriented Javadoc/comments for planet definitions, registry
  lifecycle, Java mod callbacks, KubeJS planet registration, orbit/wind/sky
  helpers, and compatibility gravity helpers so later generated docs have
  stable source text.
- Moved remaining telescope hitbox, tooltip alias, visibility, and moon-phase
  render flags into `PlanetDefinition` data. `TelescopeScreen` no longer keeps
  planet-id switches for hit radius/offset or `earth_moon`/`phobos_deimos`
  visibility.
- Moved return-ticket target detection out of `RocketContraption.capture()` and
  into rocket station assembly after the registry-backed destination has been
  resolved.
- Added KubeJS-friendly builder overloads for `dimension("namespace:path")` and
  `telescopeTexture("namespace:path")`.
- Added public extension documentation:
  - `docs/en_us/PLANET_API.md`
  - `docs/en_us/KUBEJS_PLANETS.md`
  - `docs/en_us/KNOWN_LIMITATIONS.md`
  - `docs/en_us/PLANET_UNHARDCODED.md`
  - `docs/en_us/CHANGELOG_UNHARDCODED.md`
  - `docs/en_us/examples/test_planet_startup.js`
  - `docs/zh_cn/PLANET_API.md`
  - `docs/zh_cn/KUBEJS_PLANETS.md`
  - `docs/zh_cn/KNOWN_LIMITATIONS.md`
  - `docs/zh_cn/PLANET_UNHARDCODED.md`
  - `docs/zh_cn/CHANGELOG_UNHARDCODED.md`
  - `docs/zh_cn/examples/test_planet_startup.js`
- Linked the new extension documentation from `README.md` and recorded the docs
  maintenance rule in `AGENTS.md`.
- Updated project direction to make KubeJS the pack-facing configuration path and
  remove datapack/JSON planet loading from the current target scope.
- Investigated the first client playtest crash after teleporting to Mars.
  - Crash cause was a missing dynamic registry entry for
    `northstar:suffocation`, triggered by Mars worm breathing ticks through
    `NorthstarOxygen.onBreathe`.
  - Added the runtime damage type JSON under `src/main/resources` and preserved
    the intended bypass-armor/bypass-enchantments damage tags.
  - Ran datagen locally to restore the ignored `src/generated` resource tree,
    which provides generated Northstar item/block models and removes the
    Northstar-side missing model spam that caused purple-black placeholders.
  - Removed the impossible `facing=down` telescope blockstate variant; the block
    only exposes horizontal facings.
- Investigated the follow-up client startup/resource pass after the texture and
  teleport crash report.
  - Split optional Create addon/KubeJS development runtimes behind Gradle flags:
    `northstar.fullDevRuntime`, `northstar.devRuntime.kubejs`,
    `northstar.devRuntime.cca`, `northstar.devRuntime.cdg`, and
    `northstar.devRuntime.tfmg`.
  - Kept those integrations on the compile classpath while making the default
    `runClient` use a smaller runtime set.
  - Removed the temporary `REGISTRIES`/debug console logging that was added
    while chasing the Registrate unused-callback failure.
  - Re-ran short client starts for individual optional runtimes and the full
    runtime; all reached client resource loading without the previous
    Registrate unused-callback crash.
  - Fixed Northstar-side sound resource warnings by defining
    `mars_cobra_death`, adding its subtitle translations, and pointing
    `venus_mimic_die` at the existing `northstar.subtitle.mimic_death` key.
  - Guarded the Create Crafts & Additions liquid-burning datagen provider so
    it is only loaded when `createaddition` is present, avoiding a default
    datagen `NoClassDefFoundError`.
  - Confirmed the remaining model/texture errors in full-runtime logs are from
    external addon resources such as `tfmg` and `createaddition`, not Northstar
    generated models or planet registry work.
- Investigated a manual rocket assembly failure from `run/logs/latest.log`.
  - The test rocket met fuel and engine requirements, but failed cockpit
    sealing, heat shielding, and target item checks.
  - Cleared rocket station target state when its slot no longer contains a
    star map or return ticket with planet data, preventing stale target costs
    from being displayed after removing the travel item.
- Investigated a client crash after wrenching/cranking a generated
  `northstar:lunar_base` Create structure on Mars.
  - The crash stack is in Flywheel's indirect rendering backend while collecting
    sky light data, not in Northstar planet registry code.
  - Aligned the declared Flywheel API/runtime version with the Create-resolved
    runtime (`1.0.6-41`) and disabled the local dev Flywheel backend in
    `run/config/flywheel-client.toml` so manual gameplay testing can continue
    without hitting the Flywheel indirect-backend mixin crash.
- Replaced remaining rocket/telescope player-facing hardcoded strings with
  translation keys:
  - Rocket station assembly blockers, status readouts, missing-requirement
    errors, and UI target/fuel text now use `northstar.gui.rocket_station.*`.
  - In-flight rocket status messages now use `northstar.contraption.rocket.*`.
  - Telescope selected-body coordinates and printed-reading lore now use
    `northstar.gui.telescope.coordinate_*`.
  - Added matching entries for default English, Chinese, Japanese, and Russian
    language files.
- Continued the hardcoded-text sweep:
  - Removed stale debug log strings from the unfinished oxygen bubble generator.
  - Realigned rocket Ponder tutorial lang entries with the 1.21.1 Ponder key
    scheme (`header`, `text_1` through `text_8`) in default, generated English,
    Chinese, Japanese, and Russian resources.
- Restored runtime block tag data for rocket heat shielding:
  - Added `northstar:tier_1_heat_resistance`,
    `northstar:tier_2_heat_resistance`, and
    `northstar:tier_3_heat_resistance` block tag JSON files.
  - This fixes rockets reporting `0` current heat shielding even when built
    with iron/titanium, martian steel, or tungsten heat-resistant blocks.
- Fixed the datagen pass so generated runtime data can be refreshed cleanly:
  - Removed direct datagen references to optional compat classes from Copycats,
    Create Diesel Generators, and TFMG. Optional compat tag/fuel data now uses
    resource ids instead of loading those mods on the datagen runtime classpath.
  - Kept Copycats as a local `compileOnly` jar override only; it is not forced
    onto `runData`/runtime because the local jar's access widener is not
    consumable by the current Loom setup.
  - Let datagen own generated damage-type tags, the `northstar:suffocation`
    damage type JSON, and rocket heat-resistance block tags. The duplicate
    hand-written copies under `src/main/resources` were removed so
    `processResources` no longer fails on duplicate paths after `runData`.
  - Preserved the original `northstar:suffocation` behavior by setting the
    generated damage type exhaustion to `0.1`.
  - Regenerated missing recipe/runtime data, including rocket station, rocket
    controls, interplanetary navigator, telescope, astronomy table, and jet
    engine recipes.
- Fixed an interplanetary navigator playtest regression:
  - The interplanetary navigator no longer deletes itself during Create
    contraption disassembly. Its two-block validation is now handled explicitly
    on player breaking, instead of using neighbor updates that can fire while a
    moved rocket is only partially restored.
  - Rocket station fuel estimates intentionally continue to use live
    telescope/orbit coordinates, so launch costs can fluctuate with the current
    planet positions.
- Expanded `README.md` with a clear "What changed from upstream Redux" section
  covering registry-backed planet behavior, rocket/telescope/environment
  routing, Java/KubeJS extension points, dev-environment fixes, localization
  cleanup, and added documentation.
- Improved KubeJS planet smoke testing:
  - Updated the local `run/kubejs/startup_scripts/main.js` example to include a
    telescope-only body and a reachable `test_orbit` target using the existing
    `northstar:earth_orbit` dimension.
  - Added KubeJS planet registration log lines, including a warning when a
    script marks a planet reachable but does not provide a dimension, because
    those entries cannot produce creative-tab star maps.
- Adjusted the local telescope-only KubeJS smoke-test body to use a fixed
  near-center telescope position, and documented that telescope coordinates are
  direct UI-space coordinates rather than automatically scaled solar-system
  units.
- Documented the current orbit coordinate model for KubeJS/Java planet authors:
  fixed positions, fixed-center orbits, parent-relative orbits, the approximate
  sine/cosine position formula, and the fact that travel fuel estimates reuse
  these moving telescope positions.
- Added `KubePlanetBuilder`, a KubeJS-facing wrapper around
  `PlanetDefinition.Builder`, so ProbeJS can generate cleaner method
  completions without making the public Java planet API depend on optional
  KubeJS annotation classes.
- Investigated missing ProbeJS completions after a dump.
  - Confirmed Northstar declarations were generated, including
    `NorthstarEvents.planets`, `KubePlanetBuilder`, `OrbitDefinition`,
    `WindDefinition`, and `SkyProfile`.
  - Confirmed the TypeScript project failed because ProbeJS `7.7.2` emitted
    invalid `.d.ts` syntax for unrelated generated Java/KubeJS/Create classes.
  - Kept method documentation on the Java/KubeJS API itself via KubeJS
    `@Info`/`@Param` annotations, which is the normal ProbeJS documentation
    path. The generated typings are only an editor output of those annotations.
  - Verified ProbeJS `8.0.2` is not compatible with the current dev runtime
    because it requires `kubejs >= 2101.7.2-build.365` while this runtime uses
    `2101.7.2-build.285`.
  - Deliberately did not patch ProbeJS-generated `.d.ts` files from Northstar.
    Invalid generated TypeScript is a ProbeJS/KubeJS tooling issue; Northstar's
    responsibility is to expose a clean annotated Java/KubeJS API.
  - Tested upgrading to ProbeJS `8.0.2` by raising KubeJS to
    `2101.7.2-build.365`.
    - ProbeJS `8.0.2` requires KubeJS `>= 2101.7.2-build.365`.
    - KubeJS `2101.7.2-build.369` crashes this dev runtime during attribute
      registry setup around `neoforge:swim_speed`.
    - KubeJS `2101.7.2-build.365` needs `tiny-java-server` `1.0.0-build.33`
      for the web-server ABI, but still triggers Create/Registrate unused
      register callbacks before ProbeJS can dump.
    - Reverted the dev runtime to the previously working KubeJS
      `2101.7.2-build.285` and ProbeJS `7.7.2`.
  - `NorthstarEvents.planets(event => event.createPlanet(id))` now returns the
    wrapper while preserving the existing chain-style script API.
  - Common script methods now carry `@Info`/`@Param` metadata.
  - Added string-based `telescopeHiddenIn(...)` and
    `telescopeVisibleOnlyIn(...)` helpers for scripts.
  - Exposed `KubePlanetBuilder` as a KubeJS binding and documented ProbeJS
    regeneration expectations.
- Updated `README.md` to summarize the new KubeJS/ProbeJS wrapper,
  script-friendly telescope visibility helpers, and telescope orbit coordinate
  model.
- Added a JEI item subtype interpreter for `northstar:star_map` that uses the
  `NorthstarDataComponents.PLANET` value as subtype data. JEI should now keep
  registry-generated star maps for different planet ids separate instead of
  collapsing them into duplicate plain star-map entries.

## Verification Notes

- `compileJava` must be run with JDK 21. The local `JAVA_HOME` pointed at JDK 17,
  so verification used `JAVA_HOME=C:\jdk\21\graalvm-jdk-21.0.6+8.1` for the
  command only.
- Without credentials or a local jar override, `compileJava` cannot resolve
  `com.copycatsplus:copycats:3.0.4+mc.1.21.1-neoforge` from GitHub Packages and
  returns HTTP 401.
- `compileJava` passes when run with the local Copycats jar from the provided
  mods folder:
  `-Pnorthstar.localCopycatsJar=C:\path\to\copycats-3.0.4+mc.1.21.1-neoforge.jar`.
  The build currently reports 9 upstream deprecation warnings and no errors.
- `git diff --check` passed after the current refactor set.
- The client was started with `runClient` after restoring generated resources.
  Current remaining model/texture errors in `latest.log` are from external mods
  such as `tfmg`/`createaddition`, not from Northstar generated item models.
- `compileJava processResources` passed after the dev-runtime split and sound
  resource fixes.
- `compileJava processResources` passed again after the hardcoded-text/Ponder
  lang-key sweep, using JDK 21 and the local Copycats jar override.
- JSON parsing passed for all Northstar language files under both
  `src/main/resources/assets/northstar/lang` and
  `src/generated/assets/northstar/lang`.
- A short `runClient -Pnorthstar.fullDevRuntime=true` verification reached
  client resource loading and no longer reported Northstar missing sound/subtitle
  warnings.
- `runData` completed all providers successfully after the datagen compat and
  duplicate-resource fixes. It still prints an Architectury transformer
  `StringIndexOutOfBoundsException` during post-run duplicate cleanup, after
  Gradle has already reported `BUILD SUCCESSFUL`; treat that as tooling noise
  unless it starts changing the Gradle result.
- `git diff --check` passed again after adding the public API/KubeJS/limitations
  docs.
- `compileJava processResources` passed again after the `runData` fix, using JDK
  21 and the local Copycats jar override.
- `compileJava` passed after fixing interplanetary navigator disassembly
  behavior.
- `compileJava` passed after adding the KubeJS `KubePlanetBuilder` ProbeJS
  wrapper.
- A short KubeJS-enabled `runClient` reached startup script loading after the
  wrapper change. The local smoke script still registered `test_comet` and
  `test_orbit` with 0 KubeJS startup errors/warnings.
- `compileJava` passed after adding the JEI star-map subtype interpreter.
- Gradle daemons were stopped after the attempted build.

## Next Work

- Add script-friendly overloads for telescope visibility restrictions if KubeJS
  packs need to hide/show bodies by dimension string.
- Decide whether entity-category gravity should become explicit
  `PlanetDefinition` data for Java/KubeJS definitions.
- Move remaining sky/fog rendering constants into reusable profile descriptors
  once the first profile-backed behavior is stable.
- Keep improving the Java API and KubeJS path; do not start a planet JSON loader
  unless the fork direction changes again.
