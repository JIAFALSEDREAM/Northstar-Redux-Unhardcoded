# 星球去硬编码开发说明

本文是 `Create: Northstar - Redux Unhardcoded` 的中文说明，面向之后维护这个
fork、写 Java 扩展 mod、写 KubeJS 脚本、或者整理 modpack 文档的人。

当前目标是 Minecraft 1.21.1 + NeoForge。第一阶段只做结构重构：先把现有
Northstar Redux 的星球行为抽成统一数据模型，不急着添加新星球。

## 开发基调

这个 fork 不是长期和上游竞争的分叉。它的定位是在上游正式支持数据驱动星球
之前，先提供一个临时的去硬编码版本，让 Java mod、KubeJS pack 和 modpack
作者能配置星球、轨道、火箭目标和环境参数。这个 fork 也可以携带从 upstream
Redux 继承来的、已经确认的玩法/内容 bug 修复，只要这些修复在本 fork 中可维护。

当前基调：

- Java API 是主入口。
- KubeJS 是 Java API 上的一层适配，不另起一套脚本专用星球模型。
- datapack/JSON 星球加载不作为当前目标；pack 侧配置优先走 KubeJS。
- 旧存档、旧星图、返程票里的 `planet` 字符串必须兼容。
- 新星球不应该要求改 `TelescopeScreen`、`RocketStationBlockEntity` 或
  `NorthstarPlanets` 里的星球专用 `if/switch`。
- 可以修复从 upstream 继承来的玩法/内容 bug，但修复应保持聚焦，不偏离去硬编码
  方向。

## 开发阶段计划

### 第一阶段：结构去硬编码

第一阶段只做结构重构，不急着新增官方星球内容。目标是把现有 Redux 行为抽成
统一的 `PlanetDefinition` 和 `PlanetRegistry`：

- 内置地球、月球、火星、水星、金星、地球轨道和望远镜天体都注册成共享定义。
- 望远镜、星图、火箭站、氧气、温度、重力、太阳能、风力、天空 profile 等
  系统通过 registry 查询数据。
- Java mod 通过 `PlanetRegistry.registerCallback(...)` 注册星球。
- KubeJS startup script 通过 `NorthstarEvents.planets(...)` 走同一套 builder 和
  registry。
- 保留旧 id、旧星图、返程票和存档兼容。

第一阶段的验收标准不是“能加很多新星球”，而是“旧行为不坏，并且测试星球不再
要求修改望远镜或火箭站里的星球专用分支”。

### 第二阶段：把兼容逻辑变成显式数据

第二阶段是在第一阶段稳定之后继续收口剩余硬编码，重点不是做 JSON loader，也
不是新增星球内容，而是把仍藏在 helper 或 renderer profile 里的行为变成更明确
的 API 数据。

优先方向：

- 分类重力数据化。当前 `gravityMultiplier` 是主重力值，但船、矿车、箭、掉落物、
  投掷物、生物实体等分类仍可能为了兼容旧 Redux 行为走不同 helper。后续可以把
  这些差异变成 `PlanetDefinition` 的显式字段或子模型，让第三方星球也能精细
  配置。
- 天空、雾和天气 profile 数据化。当前新星球只能复用 `SPACE`、`MOON_LIKE`、
  `MARS_LIKE`、`VENUS_LIKE`、`MERCURY_LIKE` 等 `SkyProfile`。后续可以把颜色、
  雾密度、云、天气粒子、地平线、天空物体等拆成 reusable profile descriptor，
  而不是继续堆渲染分支。
- 继续打磨 Java API 和 KubeJS 适配，补足脚本友好的 overload，并保持两边共享
  同一个 `PlanetDefinition` 模型。
- 用 registry 注册的测试星球持续验证望远镜、星图、火箭目标、燃料/引擎、
  氧气、温度、重力、太阳能、风力和 sky profile 查询。

明确不作为第二阶段默认目标：

- 不新增官方星球内容，除非先完成内置行为共享定义。
- 不做 `data/<namespace>/northstar/planets/*.json` 加载。
- 不做 planet JSON schema 或 KubeJS planet JSON 生成。
- 不自动创建维度、biome、chunk generator 或 worldgen。
- 不和上游长期竞争；如果 upstream 提供等价数据驱动星球支持，应优先减少重复
  维护。
- 不把泛化 upstream 维护变成本 fork 的主要目标；继承来的玩法/内容 bug 修复应
  保持小范围和可维护。

## 核心模型

核心 API 在：

```text
com.lightning.northstar.api.planet
```

主要类型：

- `PlanetDefinition`：一个星球或可观测天体的数据定义。
- `PlanetRegistry`：集中注册和查询入口。
- `PlanetRegistrar`：Java mod 注册回调拿到的注册器。
- `OrbitDefinition`：望远镜/轨道用的二维轨道定义。
- `PlanetPosition`：计算后的二维坐标。
- `SkyProfile`：第一阶段的天空/雾渲染预设。
- `WindDefinition`：晴天和雨天/风暴时的风力倍率。

`PlanetDefinition` 至少承载这些行为：

- 稳定星球 id。
- 火箭目标维度。
- UI 显示名/lang key。
- 轨道父级、半径、速度、位置。
- 重力倍率。
- 温度。
- 是否有氧气。
- 是否有大气。
- 大气成本。
- 计算成本。
- 引擎常数。
- 太阳能倍率。
- 风力倍率。
- 天空 profile。
- 望远镜贴图、提示和点击范围。
- 是否可被火箭抵达。
- 是否需要星际导航仪。

注册星球不等于创建维度。维度、世界生成、生物群系、语言文件、贴图资源仍然
需要由对应 mod、KubeJS pack、datapack 或 resource pack 提供。

## Java Mod 注册方式

第三方 Java mod 不应该 import 或修改 Northstar 内部类，比如
`NorthstarPlanets`、`TelescopeScreen`、火箭方块实体或 mixin。正确方式是使用
公共 API：

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

推荐使用 `PlanetRegistry.registerCallback(...)`，这样注册顺序不依赖 mod
构造器谁先谁后。内置星球注册完成后，Northstar 会统一应用 Java mod 回调和
KubeJS 启动脚本注册。

## KubeJS 注册方式

KubeJS 脚本放在：

```text
kubejs/startup_scripts/
```

示例：

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

KubeJS 当前暴露：

- `PlanetDefinition`
- `PlanetRegistry`
- `KubePlanetBuilder`
- `OrbitDefinition`
- `WindDefinition`
- `SkyProfile`

注意：KubeJS 使用 startup event，改完脚本需要重启游戏。KubeJS 目前内部使用
`registerOrReplace(...)`，所以可以覆盖内置 id，但这会影响旧星图、返程票和
旧存档数据，除非明确知道自己在做什么，否则不要随便替换 `moon`、`mars`、
`venus` 这类旧 id。

`event.createPlanet(id)` 返回的是 KubeJS 专用的 `KubePlanetBuilder`。它内部
仍然转发到 Java 的 `PlanetDefinition.Builder`，但额外带了 KubeJS
`@Info`/`@Param` 注解，方便 ProbeJS 生成更清楚的链式补全。装了 ProbeJS 后，
重启游戏并重新生成 typings，应该能看到 `NorthstarEvents.planets(...)`、
`event.createPlanet(...)`、`.dimension(...)`、`.orbit(...)`、
`.reachableByRocket(...)` 等补全。startup 脚本不会热重载，改脚本后需要重启。

当前 1.21.1 NeoForge 开发环境使用的是 KubeJS `2101.7.2-build.285`，本地测试
匹配的 ProbeJS 是 `7.7.2`。这版 ProbeJS 有时会从其他 Java/KubeJS/Create
类生成 TypeScript 语法不合法的 `.d.ts`，导致 VS Code 看不到补全。这属于
ProbeJS/KubeJS 工具链问题，不应该由 Northstar 客户端后台修改生成文件。
Northstar 负责的是在 Java/KubeJS API 上保留 `@Info`/`@Param` 注解，让
ProbeJS 能按正常机制读取方法说明。ProbeJS `8.0.2`
需要更新的 KubeJS build，不能直接塞进当前 dev runtime。

## 可达星球和仅观测天体

火箭可达星球通常需要：

```java
.dimension(TEST_DIMENSION)
.observable(true)
.reachableByRocket(true)
```

仅望远镜可见、不能旅行的天体可以不设置 dimension：

```java
PlanetDefinition.builder("example_comet")
        .dimension(null)
        .orbit(OrbitDefinition.fixed(-80, 70))
        .oxygen(false)
        .atmosphere(false)
        .reachableByRocket(false)
        .build();
```

这类天体适合彗星、远景行星、没有维度的卫星，或者纯观测用天体。

望远镜坐标是直接用于 UI 的二维坐标，不是 Minecraft 世界坐标，也不会自动
缩放成完整太阳系视图。望远镜背景可以理解成一张 900x900 的可滚动画布，屏幕
只显示其中一块区域。写测试脚本时建议先用
`OrbitDefinition.fixed(-80, 70)` 这类靠近初始视野的位置；大半径轨道可能需
要玩家拖动画面才能找到，甚至在某些时间点跑到当前可视范围之外。

`OrbitDefinition` 当前主要有三种写法：

- `OrbitDefinition.fixed(x, y)`：固定在一个望远镜坐标，不会移动。
- `OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed)`：
  绕一个固定的望远镜坐标中心旋转。
- `OrbitDefinition.around("parent_id", radiusX, radiusY, speed)`：绕另一个
  已注册天体的当前望远镜坐标旋转，比如月球绕地球。

移动轨道的近似公式是：

```text
x = centerX + cos(worldTime * speed) * radiusX
y = centerY + sin(worldTime * speed) * radiusY
```

`radiusX` 和 `radiusY` 不同时会形成椭圆轨道；`speed` 越大转得越快。当前
Northstar 也会复用这些望远镜/轨道坐标计算旅行距离，所以燃料需求会随着天体
位置变化。

## 旧内容兼容

这些旧 id 必须谨慎处理：

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

其中 `earth_moon` 是为了望远镜和旧星图行为保留的 legacy id；月球维度的主
定义仍然是 `moon`。`earth_orbit` 有维度定义，但不是普通火箭目标 id。

## 天空和天气

第一阶段不做完全数据驱动天空。新星球先复用 `SkyProfile`：

- `DEFAULT`
- `SPACE`
- `MOON_LIKE`
- `MARS_LIKE`
- `VENUS_LIKE`
- `MERCURY_LIKE`

这些 profile 只是选择已有 Northstar 天空、雾、天气和部分温度逻辑。以后再把
颜色、雾密度、云、天气粒子、天空物体等拆成真正的数据字段。

## 当前限制

当前不做或还没做：

- `data/<namespace>/northstar/planets/*.json` 加载。这个方向复杂度较高，
  当前 fork 不优先做。
- planet JSON schema。它和上面的 JSON loader 绑定，当前不优先做。
- KubeJS 生成 planet JSON。当前更推荐直接写 startup script 注册。
- 自动创建维度、世界生成和生物群系。
- 完全数据驱动 sky/fog/weather renderer。
- 把所有 entity 类型的重力差异都做成 `PlanetDefinition` 字段。

当前重力已经集中到 helper，但为了兼容旧行为，船、矿车、箭、物品、投掷物、
生物实体等分类仍然可能走不同兼容值。之后如果要做得更正式，可以把这些分类
重力做成显式数据。

## 验收方向

第一阶段验收重点：

- 现有五个主要维度/星球行为不变。
- 旧星图、返程票、望远镜、火箭站仍可用。
- 新增测试星球时，不需要修改望远镜 UI 和火箭站里的星球专用分支。
- registry 能提供星图打印、火箭目标、燃料/引擎计算、氧气、温度、重力、
  太阳能、风力、天气和 sky profile 数据。
- 不破坏旧存档里的 `planet` 字符串数据。

更细的文档见：

- `docs/zh_cn/PLANET_API.md`
- `docs/zh_cn/KUBEJS_PLANETS.md`
- `docs/zh_cn/KNOWN_LIMITATIONS.md`
- `docs/en_us/PLANET_API.md`
- `docs/en_us/KUBEJS_PLANETS.md`
- `docs/en_us/KNOWN_LIMITATIONS.md`
