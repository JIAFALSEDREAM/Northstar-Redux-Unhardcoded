# 去硬编码 Fork 变更日志

本文记录尚未成为 upstream Redux 行为的本地 fork 工作。

## 2026-05-28

- 在 `README.md` 中明确 fork 方向：这是临时去硬编码 fork，Java API 优先，
  KubeJS 作为适配层。
- 添加 `AGENTS.md`，记录本地开发规则、目标架构、需要检查的硬编码区域和验证期望。
- 在 `com.lightning.northstar.api.planet` 下添加公开星球 API 基础：
  - `PlanetDefinition`
  - `PlanetRegistry`
  - `PlanetRegistrar`
  - `OrbitDefinition`
  - `PlanetPosition`
  - `SkyProfile`
  - `WindDefinition`
- 将 `NorthstarPlanets` 改成兼容 facade：
  - 保留现有 public static 字段和方法，避免破坏当前调用方。
  - 把内置天体注册进 `PlanetRegistry`。
  - 保留 `earth`、`earth_moon`、`moon`、`mars`、`venus`、`mercury` 和仅观测天体等
    旧 id 与持久化字符串兼容。
  - 通过注册 `earth_orbit` 且设置 `oxygen(true)` 保留当前氧气 fallback 行为。
- 添加 `PlanetRegistry.registerCallback(...)`，让第三方 Java mod 注册星球时不依赖
  构造器顺序。
- 将温度、天气、氧气、大气机器、太阳能、风力、重力 helper、雾和天空 profile
  等查询逐步接到 `PlanetDefinition` / `NorthstarPlanets` registry-backed helper。
- 添加 KubeJS 绑定和 startup event `NorthstarEvents.planets`。
  - `event.createPlanet(id)` 支持链式 builder。
  - 脚本注册通过和 Java mod 相同的 registry 路径进入 `PlanetDefinition`。
  - 后续加入 `KubePlanetBuilder` wrapper，用 KubeJS `@Info`/`@Param` 元数据改善
    ProbeJS 链式补全。
- 将火箭站旅行燃料、返程燃料、引擎数量和星际导航仪检查集中到共享 helper。
  `RocketStationBlockEntity`、`RocketStationMenu` 和飞行中火箭显示不再各自保存一套
  燃料/引擎公式。
- 将 `TelescopeScreen` 的渲染、tooltip、点击选择、命中范围、可见性和月相显示改为
  遍历 `PlanetRegistry.all()` 和读取 `PlanetDefinition` 数据，而不是每个天体一段
  专用分支。
- 修正重复维度索引：`moon` 仍是月球维度的主定义，`earth_moon` 作为望远镜和旧星图
  legacy id 保留。
- 集中重力查询：
  - 生物、物品、投掷物、船、矿车、箭和客户端行走动画缩放改为查询共享 helper。
  - 为内置星球保留旧分类差异，避免破坏 Redux 行为。
  - Mars dust push 检查改为 registry/profile-backed predicate。
- 将 Mercury 昼夜温度、sky/fog/weather renderer 分支和 creative tab 星图生成改为
  通过 registry/profile 数据驱动。
- 添加本地 Copycats 开发 fallback：
  - Gradle 配置阶段不再强制需要 GitHub Packages 凭据。
  - 可用 `-Pnorthstar.localCopycatsJar=<path-to-copycats.jar>` 指向本地 jar。
  - `.gradle/local-libs/copycats-*.jar` 也会被自动识别为 compile classpath。
  - 限制 Copycats GitHub Packages 仓库只探测 `com.copycatsplus` group。
- 添加面向 API 的 Javadoc/注释，覆盖星球定义、registry 生命周期、Java mod 回调、
  KubeJS 注册、轨道/风/天空 helper 和兼容重力 helper。
- 将返程票目标检测从 `RocketContraption.capture()` 移到火箭站组装阶段，在
  registry-backed 目的地解析后处理。
- 添加 KubeJS 友好的 builder overload，例如 `dimension("namespace:path")` 和
  `telescopeTexture("namespace:path")`，后续又补充了
  `telescopeHiddenIn(...)` / `telescopeVisibleOnlyIn(...)` 的字符串 helper。
- 添加并整理公开扩展文档：
  - `docs/en_us/PLANET_API.md`
  - `docs/en_us/KUBEJS_PLANETS.md`
  - `docs/en_us/KNOWN_LIMITATIONS.md`
  - `docs/en_us/PLANET_UNHARDCODED.md`
  - `docs/zh_cn/PLANET_API.md`
  - `docs/zh_cn/KUBEJS_PLANETS.md`
  - `docs/zh_cn/KNOWN_LIMITATIONS.md`
  - `docs/zh_cn/PLANET_UNHARDCODED.md`
  - `docs/*/examples/test_planet_startup.js`
- 调整项目方向：KubeJS 是 pack-facing 配置路径，datapack/JSON planet loading 不属于
  当前目标。
- 排查首次 Mars teleport 后的客户端崩溃：
  - 崩溃来自缺少 `northstar:suffocation` dynamic registry entry。
  - 添加运行时 damage type JSON，并保留 bypass armor/enchantment 标签行为。
  - 通过 datagen 恢复被 ignore 的 `src/generated` 资源树，消除 Northstar 侧缺失模型
    造成的紫黑占位。
  - 移除不可能出现的 `facing=down` telescope blockstate variant。
- 拆分可选 Create addon/KubeJS 开发运行时：
  - `northstar.fullDevRuntime`
  - `northstar.devRuntime.kubejs`
  - `northstar.devRuntime.cca`
  - `northstar.devRuntime.cdg`
  - `northstar.devRuntime.tfmg`
- 修复 Northstar 声音资源警告、Create Crafts & Additions datagen 可选依赖加载问题，
  并确认 full-runtime 剩余模型/贴图错误来自外部 addon 资源而非 Northstar 生成模型。
- 排查火箭组装失败日志：
  - 测试火箭满足燃料和引擎需求，但未满足驾驶舱密封、隔热和目标物品检查。
  - 火箭站 slot 不再包含带 planet 数据的星图/返程票时，会清空目标状态，避免显示
    过期成本。
- 排查 Mars 上 wrench/crank 生成结构导致的 Flywheel 间接渲染崩溃：
  - 崩溃栈在 Flywheel backend 收集 skylight 数据，不在 planet registry。
  - 对齐 Flywheel API/runtime 版本，并在本地 dev 配置中禁用 Flywheel backend，方便
    继续手动测试。
- 将火箭、望远镜、Ponder 和 UI 的面向玩家硬编码文本迁移到翻译 key，并补充默认
  英文、中文、日文和俄文资源。
- 恢复火箭隔热运行时 block tag 数据，并让 datagen 管理 damage type tag、
  `northstar:suffocation` JSON 和隔热 tag，避免 `processResources` 重复路径失败。
- 修复 datagen：
  - 移除对 Copycats、Create Diesel Generators 和 TFMG 可选 compat 类的直接 datagen
    加载。
  - 可选 compat tag/fuel 数据改为使用资源 id。
  - 重新生成缺失 recipe/runtime 数据，包括 rocket station、rocket controls、
    interplanetary navigator、telescope、astronomy table 和 jet engine recipes。
- 修复星际导航仪回归：
  - 星际导航仪不再在 Create contraption disassembly 时删除自身。
  - 双方块校验改为在玩家破坏时显式处理，避免移动火箭部分恢复时被 neighbor update
    误触发。
- 改进 KubeJS planet smoke test：
  - 本地 `run/kubejs/startup_scripts/main.js` 示例包含一个仅望远镜天体和一个可达的
    `test_orbit` 目标。
  - 添加 KubeJS 星球注册日志；脚本把星球标成可达但没有维度时会警告，因为无法生成
    creative tab 星图。
- 记录望远镜/轨道坐标模型：固定位置、固定中心轨道、父天体相对轨道、近似
  sin/cos 公式，以及旅行燃料估算复用这些移动坐标的事实。
- 排查 ProbeJS 补全：
  - 确认 Northstar declarations 已生成，包括 `NorthstarEvents.planets`、
    `KubePlanetBuilder`、`OrbitDefinition`、`WindDefinition` 和 `SkyProfile`。
  - 确认 ProbeJS `7.7.2` 会从无关 Java/KubeJS/Create 类生成无效 `.d.ts` 语法。
  - 保持文档在 Java/KubeJS API 的 `@Info`/`@Param` 注解上，不从 Northstar patch
    ProbeJS 生成文件。
  - 尝试 ProbeJS `8.0.2` 需要更新 KubeJS；`2101.7.2-build.365/369` 在当前 dev
    runtime 中仍有 ABI 或 registry setup 问题，所以回退到 KubeJS
    `2101.7.2-build.285` 和 ProbeJS `7.7.2`。
- 添加 JEI item subtype interpreter，让 `northstar:star_map` 使用
  `NorthstarDataComponents.PLANET` 作为 subtype 数据。JEI 应该不再把不同 planet id 的
  registry-generated star map 折叠成重复的普通星图条目。

## 验证记录

- `compileJava` 必须用 JDK 21。本地 `JAVA_HOME` 指向 JDK 17 时，验证命令临时使用
  `JAVA_HOME=C:\jdk\21\graalvm-jdk-21.0.6+8.1`。
- 没有凭据或本地 jar override 时，`compileJava` 无法从 GitHub Packages 解析
  `com.copycatsplus:copycats:3.0.4+mc.1.21.1-neoforge`，会返回 HTTP 401。
- 使用提供的本地 Copycats jar 时，`compileJava` 通过：
  `-Pnorthstar.localCopycatsJar=C:\path\to\copycats-3.0.4+mc.1.21.1-neoforge.jar`。
- `git diff --check` 在当前 refactor 集合和文档添加后通过。
- `compileJava processResources` 在 dev runtime 拆分、声音资源修复、Ponder/lang key
  清理和 `runData` 修复后均通过。
- Northstar 语言文件 JSON 解析通过，覆盖 `src/main/resources/assets/northstar/lang`
  和 `src/generated/assets/northstar/lang`。
- `runClient -Pnorthstar.fullDevRuntime=true` 的短启动到达客户端资源加载，不再报告
  Northstar 缺失声音/subtitle 警告。
- `runData` provider 成功完成。后续 Architectury transformer 的
  `StringIndexOutOfBoundsException` 出现在 Gradle 已报告 `BUILD SUCCESSFUL` 之后；
  除非它开始改变 Gradle 结果，否则按工具噪音处理。
- 添加 `KubePlanetBuilder` 后，KubeJS-enabled `runClient` 短启动到达 startup script
  loading，本地 smoke script 注册 `test_comet` 和 `test_orbit` 时没有 KubeJS
  startup errors/warnings。
- 添加 JEI star-map subtype interpreter 后，`compileJava` 通过。
- 尝试构建后已停止 Gradle daemons。

## 后续工作

- 如果 KubeJS pack 需要按维度字符串隐藏/显示天体，继续补 telescope visibility 的
  脚本友好 overload。
- 决定 entity 分类重力是否应该成为 Java/KubeJS `PlanetDefinition` 的显式数据。
- 第一版 profile-backed 行为稳定后，把剩余 sky/fog renderer 常量移动到可复用
  profile descriptor。
- 继续打磨 Java API 和 KubeJS 路径；除非 fork 方向再次改变，否则不要启动 planet
  JSON loader。
