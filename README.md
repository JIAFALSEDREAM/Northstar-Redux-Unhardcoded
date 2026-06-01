# Create: Northstar - Redux Unhardcoded

Languages: [English](#english) | [中文](#中文)

## English

Create: Northstar - Redux Unhardcoded is a transitional hardcode-removal fork of
[Create: Northstar - Redux](https://github.com/Astronauts-of-Create/Northstar-Redux)
for Minecraft 1.21.1 on NeoForge.

This fork keeps the original Northstar Redux gameplay direction, but moves
planet behavior, rocket and telescope lookups, environment rules, and related
hardcoded text paths toward a shared registry-backed `PlanetDefinition` model.
It is focused on compatibility and extension plumbing, not on adding new planet
content. It may also carry confirmed gameplay/content bug fixes from upstream
Redux when they are practical to maintain in this fork.

## Requirements

- Minecraft 1.21.1
- NeoForge
- Java 21
- [Create](https://modrinth.com/mod/create)
- [GeckoLib](https://modrinth.com/mod/geckolib)

## What This Fork Changes

- Built-in planet data is represented by registry-backed `PlanetDefinition`
  entries.
- Rocket station target lookup, travel fuel, return fuel, engine requirements,
  heat checks, interplanetary navigator checks, star maps, and return tickets
  query the planet registry.
- Telescope rendering, selection, tooltips, and printed readings iterate
  registered observable bodies instead of one hardcoded body block per planet.
- Creative-tab star maps are generated from reachable registry planets.
- Oxygen, atmosphere machine behavior, temperature, weather, gravity helpers,
  solar/wind lookup, fog, and sky selection use registry helpers where practical.
- Several rocket, telescope, Ponder, and UI strings were moved to translation
  keys instead of hardcoded text.
- Existing planet string ids are preserved for save compatibility, including
  `earth`, `earth_moon`, `moon`, `mars`, `venus`, and `mercury`.
- Telescope-only bodies such as `jupiter`, `saturn`, `uranus`, `neptune`,
  `pluto`, `eris`, `ceres`, and `phobos_deimos` are kept as observable registry
  entries.

## Extension API

Java mods and KubeJS packs use the same registry-backed `PlanetDefinition`
model. See the dedicated docs for registration lifecycle, supported fields,
script examples, and current extension limits:

- [Java Planet API](docs/en_us/PLANET_API.md)
- [KubeJS Planets](docs/en_us/KUBEJS_PLANETS.md)
- [Known Limitations](docs/en_us/KNOWN_LIMITATIONS.md)

## Current Scope

This release supports Java API registration and KubeJS startup-script
registration. Datapack/JSON planet loading is intentionally not part of the
current scope.

The fork is meant as a compatibility-focused bridge until upstream Redux ships
equivalent official data-driven planet support. If upstream provides equivalent
support, this fork should stop carrying duplicate work.

This fork may also include small gameplay/content bug fixes for issues inherited
from upstream Redux. Those fixes should stay scoped and should not turn the fork
into a broad replacement for upstream.

## Current Limitations

The current fork is still a hardcode-removal pass, not a complete
data-driven solar-system framework. Sky/fog/weather rendering still uses
`SkyProfile` presets, some entity-category gravity differences remain helper
backed, and JSON/datapack planet loading is intentionally out of scope.

See [Known Limitations](docs/en_us/KNOWN_LIMITATIONS.md) for the full list.

## Documentation

English:

- [Planet Unhardcoding Notes](docs/en_us/PLANET_UNHARDCODED.md)
- [Java Planet API](docs/en_us/PLANET_API.md)
- [KubeJS Planets](docs/en_us/KUBEJS_PLANETS.md)
- [Known Limitations](docs/en_us/KNOWN_LIMITATIONS.md)
- [Unhardcoded Fork Changelog](docs/en_us/CHANGELOG_UNHARDCODED.md)
- [Example KubeJS startup script](docs/en_us/examples/test_planet_startup.js)

Chinese:

- [星球去硬编码开发说明](docs/zh_cn/PLANET_UNHARDCODED.md)
- [星球 Java API](docs/zh_cn/PLANET_API.md)
- [KubeJS 星球](docs/zh_cn/KUBEJS_PLANETS.md)
- [已知限制](docs/zh_cn/KNOWN_LIMITATIONS.md)
- [去硬编码 Fork 变更日志](docs/zh_cn/CHANGELOG_UNHARDCODED.md)
- [KubeJS startup script 示例](docs/zh_cn/examples/test_planet_startup.js)

## Local Build Notes

The Copycats dependency is hosted on GitHub Packages. If credentials are not
configured locally, point Gradle at a matching local jar for development builds:

```powershell
$env:JAVA_HOME='C:\jdk\21\graalvm-jdk-21.0.6+8.1'
.\gradlew.bat -P"northstar.localCopycatsJar=C:\path\to\copycats-3.0.4+mc.1.21.1-neoforge.jar" compileJava --console=plain
```

Useful dev runs:

```powershell
.\gradlew.bat runClient --console=plain
.\gradlew.bat -Pnorthstar.fullDevRuntime=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.kubejs=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.cca=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.cdg=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.tfmg=true runClient --console=plain
```

KubeJS/ProbeJS authoring notes live in [KubeJS Planets](docs/en_us/KUBEJS_PLANETS.md).

## Upstream

Original project:
[Astronauts-of-Create/Northstar-Redux](https://github.com/Astronauts-of-Create/Northstar-Redux)

This fork does not try to replace upstream long term.

## 中文

Create: Northstar - Redux Unhardcoded 是
[Create: Northstar - Redux](https://github.com/Astronauts-of-Create/Northstar-Redux)
的临时去硬编码 fork，目标环境是 Minecraft 1.21.1 + NeoForge。

这个 fork 保留原 Northstar Redux 的玩法方向，但把星球行为、火箭和望远镜查询、
环境规则以及相关硬编码文本路径，逐步迁移到 registry-backed
`PlanetDefinition` 模型。当前重点是兼容性和扩展管线，不是添加新星球内容。这个
fork 也可以携带从 upstream Redux 继承来的、已经确认的玩法/内容 bug 修复，只要
这些修复在本 fork 中可维护。

## 需求

- Minecraft 1.21.1
- NeoForge
- Java 21
- [Create](https://modrinth.com/mod/create)
- [GeckoLib](https://modrinth.com/mod/geckolib)

## 这个 Fork 改了什么

- 内置星球数据由 registry-backed `PlanetDefinition` 条目表示。
- 火箭站目标查询、旅行燃料、返程燃料、引擎需求、隔热检查、星际导航仪检查、
  星图和返程票会查询星球 registry。
- 望远镜渲染、选择、tooltip 和打印读数会遍历已注册可观测天体，而不是每个星球
  一段硬编码。
- 创造标签页里的星图会从可达 registry 星球生成。
- 氧气、大气机器行为、温度、天气、重力 helper、太阳能/风力查询、雾和天空选择
  在可行处使用 registry helper。
- 多个火箭、望远镜、Ponder 和 UI 字符串从硬编码文本改为翻译 key。
- 保留现有 planet 字符串 id 以兼容存档，包括 `earth`、`earth_moon`、`moon`、
  `mars`、`venus` 和 `mercury`。
- `jupiter`、`saturn`、`uranus`、`neptune`、`pluto`、`eris`、`ceres` 和
  `phobos_deimos` 等仅望远镜天体作为可观测 registry 条目保留。

## 扩展 API

Java mod 和 KubeJS pack 使用同一个 registry-backed `PlanetDefinition` 模型。注册
生命周期、支持字段、脚本示例和当前扩展限制见：

- [星球 Java API](docs/zh_cn/PLANET_API.md)
- [KubeJS 星球](docs/zh_cn/KUBEJS_PLANETS.md)
- [已知限制](docs/zh_cn/KNOWN_LIMITATIONS.md)

## 当前范围

当前支持 Java API 注册和 KubeJS startup-script 注册。datapack/JSON 星球加载有意
不属于当前范围。

这个 fork 是兼容性桥接方案，直到 upstream Redux 发布等价的官方数据驱动星球支持。
如果 upstream 提供等价支持，这个 fork 应停止维护重复实现。

这个 fork 也可以包含小范围玩法/内容 bug 修复，用来修 upstream Redux 继承下来的
问题。这类修复应保持聚焦，不应把 fork 变成广义 upstream 替代品。

## 当前限制

当前 fork 仍是去硬编码阶段，不是完整的数据驱动太阳系框架。天空/雾/天气渲染仍
使用 `SkyProfile` 预设，部分实体分类重力差异仍由 helper 兼容，JSON/datapack
星球加载仍不在范围内。

完整列表见 [已知限制](docs/zh_cn/KNOWN_LIMITATIONS.md)。

## 文档

English:

- [Planet Unhardcoding Notes](docs/en_us/PLANET_UNHARDCODED.md)
- [Java Planet API](docs/en_us/PLANET_API.md)
- [KubeJS Planets](docs/en_us/KUBEJS_PLANETS.md)
- [Known Limitations](docs/en_us/KNOWN_LIMITATIONS.md)
- [Unhardcoded Fork Changelog](docs/en_us/CHANGELOG_UNHARDCODED.md)
- [Example KubeJS startup script](docs/en_us/examples/test_planet_startup.js)

中文：

- [星球去硬编码开发说明](docs/zh_cn/PLANET_UNHARDCODED.md)
- [星球 Java API](docs/zh_cn/PLANET_API.md)
- [KubeJS 星球](docs/zh_cn/KUBEJS_PLANETS.md)
- [已知限制](docs/zh_cn/KNOWN_LIMITATIONS.md)
- [去硬编码 Fork 变更日志](docs/zh_cn/CHANGELOG_UNHARDCODED.md)
- [KubeJS startup script 示例](docs/zh_cn/examples/test_planet_startup.js)

## 本地构建说明

Copycats 依赖托管在 GitHub Packages。如果本地没有配置凭据，可以在开发构建时
指向匹配的本地 jar：

```powershell
$env:JAVA_HOME='C:\jdk\21\graalvm-jdk-21.0.6+8.1'
.\gradlew.bat -P"northstar.localCopycatsJar=C:\path\to\copycats-3.0.4+mc.1.21.1-neoforge.jar" compileJava --console=plain
```

常用 dev run：

```powershell
.\gradlew.bat runClient --console=plain
.\gradlew.bat -Pnorthstar.fullDevRuntime=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.kubejs=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.cca=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.cdg=true runClient --console=plain
.\gradlew.bat -Pnorthstar.devRuntime.tfmg=true runClient --console=plain
```

KubeJS/ProbeJS 编写说明见 [KubeJS 星球](docs/zh_cn/KUBEJS_PLANETS.md)。

## 上游

原项目：
[Astronauts-of-Create/Northstar-Redux](https://github.com/Astronauts-of-Create/Northstar-Redux)

这个 fork 不试图长期替代 upstream。
