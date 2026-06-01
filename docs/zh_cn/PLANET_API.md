# 星球 Java API

本文说明 `Create: Northstar - Redux Unhardcoded` fork 面向 Java mod 的星球
API。

目标环境是 Minecraft 1.21.1 + NeoForge。API 设计以 Java 为主：第三方 mod
应通过 `com.lightning.northstar.api.planet` 下的公共类型注册星球定义；
KubeJS 只是适配同一个模型，不另起一套脚本专用行为。

## 注册生命周期

Northstar 会先注册内置星球定义，然后应用 Java mod 注册回调，最后应用 KubeJS
startup script 注册。

在 mod setup 代码里使用 `PlanetRegistry.registerCallback(...)`。这样不会依赖
Northstar 和第三方 mod 构造器的加载顺序。

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

注册星球不会创建维度、生物群系源、区块生成器、贴图、语言项或世界生成数据。
目标维度和资源必须已经由注册方 mod 或 pack 提供。

## PlanetDefinition 字段

`PlanetDefinition` 是火箭、望远镜、环境和第一阶段渲染行为的共享数据源。

核心身份：

- `id`：稳定字符串 id，会存进星图、返程票、读数和存档。不要随便改名。
- `dimension`：火箭抵达的维度。仅望远镜天体可以为 `null`。
- `displayNameLangKey`：UI 翻译 key，默认是 `planets.<id>.name`。

轨道和望远镜：

- `orbit`：二维望远镜/轨道位置，也用于燃料距离估算。
- `telescopeTexture`：望远镜 UI 使用的贴图。
- `telescopeTooltipId`：tooltip 翻译 key 的可选别名，例如 `planets.<id>.grav`。
- `telescopeHitbox(radius, offset)`：望远镜界面的悬停/点击范围，单位是屏幕像素。
- `telescopeMoonPhase`：让该天体使用原版月相贴图路径。
- `telescopeHiddenIn(...)`：在指定维度隐藏该天体。
- `telescopeVisibleOnlyIn(...)`：只在指定维度显示该天体。
- `observable`：控制是否出现在望远镜列表里。

环境：

- `gravityMultiplier`：基础实体重力倍率，主世界为 `1.0`。
- `temperature`：环境温度，单位摄氏度；部分 sky profile 可能保留旧特殊逻辑。
- `oxygen`：实体是否可以不借助氧气系统呼吸。
- `atmosphere`：大气机器是否把该天体视为有大气。
- `sunMultiplier`：太阳能板输出倍率。
- `wind`：晴天和雨天/风暴时的风车倍率。
- `hasSky`、`canSeeSkyAtDay`、`hasWeather`：天空/天气行为标记。
- `customDimension`：标记 Northstar 风格自定义维度，用于兼容检查。
- `orbitDimension`：标记需要轨道式重力处理的太空/轨道维度。

火箭：

- `reachableByRocket`：星图和火箭是否能以该天体为目标。
- `requiresInterplanetaryNavigator`：旅行是否需要星际导航仪。
- `atmosphereCost`：大气发射带来的燃料/引擎需求贡献。
- `computingCost`：目标计算难度。
- `engineConstant`：额外引擎需求常数。
- `heat(heatRating, heatConstant)`：火箭检查用的隔热值。

生成兼容：

- `seedOffset`：现有 Northstar 维度种子偏移兼容值。

## OrbitDefinition

`OrbitDefinition` 当前提供一个简单二维模型：

- `OrbitDefinition.fixed(x, y)`：望远镜空间里的固定天体。
- `OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed)`：
  绕绝对原点做椭圆轨道。
- `OrbitDefinition.around(parentId, radiusX, radiusY, speed)`：绕另一个已注册
  planet id 做椭圆轨道。

`speed` 是每 tick 时间单位的弧度值。内置值使用类似 `Math.PI / 100000` 的表达式。

## Registry 查询

系统应该查询 `PlanetRegistry`，而不是写死维度判断：

- `PlanetRegistry.byId(id)`
- `PlanetRegistry.byDimension(dimension)`
- `PlanetRegistry.all()`
- `PlanetRegistry.position(id)`、`x(id)`、`y(id)`

维度查询会返回该维度的第一个主定义。这保留了 `earth_moon` 这类 legacy alias：
月球维度仍属于 `moon`，而 `earth_moon` 仍可作为望远镜/旧星图 id 存在。

## 存档兼容

旧星图、读数、返程票和其他物品数据会持久化 `id`。现有 id 必须保持稳定：

- `earth`
- `moon`
- `earth_moon`
- `mars`
- `venus`
- `mercury`
- `earth_orbit`
- 仅观测天体，例如 `jupiter`、`saturn`、`uranus`、`neptune`、`pluto`、
  `eris`、`ceres` 和 `phobos_deimos`

新的 Java mod 实践上应使用类似命名空间的唯一 id，例如
`examplemod_test_planet`，即使当前 API 内部存的是普通字符串。

## 可达天体和可观测天体

火箭目的地使用普通可达星球：

```java
.dimension(TEST_DIMENSION)
.observable(true)
.reachableByRocket(true)
```

没有目标维度时，使用仅望远镜可见的天体：

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

## 扩展规则

- 第三方 mod 不要 import 或 patch `NorthstarPlanets`、`TelescopeScreen`、
  `RocketStationBlockEntity`、mixin 或 renderer 内部类。
- 新 Java mod id 优先使用 `register(...)`。`registerOrReplace(...)` 主要给
  KubeJS/脚本覆盖层使用。
- 添加新的系统专用分支前，先让行为能用 `PlanetDefinition` 表示。
- 如果某个值当前还不能表示，应该记录缺失字段，而不是把行为藏进一次性维度判断。
