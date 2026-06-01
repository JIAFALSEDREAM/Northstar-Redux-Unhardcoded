# KubeJS 星球

Northstar 暴露了一个 startup-script 事件用于注册星球：

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

放在 `kubejs/startup_scripts/`。修改 startup script 后需要重启游戏。

注册星球不会创建维度或贴图。维度 id、语言项和贴图资源必须已经由 mod、KubeJS
pack、datapack 或 resource pack 提供。

## 暴露绑定

startup script 可以使用这些 Java API 类型：

- `PlanetDefinition`
- `PlanetRegistry`
- `OrbitDefinition`
- `WindDefinition`
- `SkyProfile`

`NorthstarEvents.planets` 是 Java API 上的一层适配。脚本星球、Java mod 星球和
内置星球最后都会变成 `PlanetDefinition` 条目。

## ProbeJS 支持

Northstar 给 `event.createPlanet(id)` 暴露了 KubeJS 专用的
`KubePlanetBuilder` wrapper。它会委托给 Java 的 `PlanetDefinition.Builder`，
但带有 KubeJS `@Info`/`@Param` 元数据，方便 ProbeJS 生成更清晰的链式方法补全。

安装 KubeJS 和 ProbeJS 后，按 ProbeJS 正常流程启动并 dump。Northstar 只暴露带
注解的 Java/KubeJS API，不会 patch ProbeJS 生成的 `.d.ts` 文件。补全应该包含：

- `NorthstarEvents.planets(...)`
- `event.createPlanet(id)`
- `.dimension(...)`、`.orbit(...)`、`.gravityMultiplier(...)`、
  `.reachableByRocket(...)`、`.heat(...)` 等链式方法
- `OrbitDefinition`、`WindDefinition`、`SkyProfile` 等全局类型

如果补全看起来过期，重启游戏并重新生成 ProbeJS typings。startup script 不会
热重载。

当前 1.21.1 NeoForge 开发运行时里，ProbeJS `7.7.2` 可能会从无关的
Java/KubeJS/Create 类生成一些无效 TypeScript declaration 语法。这属于
ProbeJS/KubeJS 工具链限制，不是 Northstar 运行时行为。Northstar 支持的部分是
把方法文档保留在 Java `@Info`/`@Param` 注解里。

## Builder 方法

常用身份和目的地方法：

- `.dimension("namespace:path")`
- `.displayNameLangKey("planets.example.name")`
- `.observable(true | false)`
- `.reachableByRocket(true | false)`
- `.requiresInterplanetaryNavigator(true | false)`

轨道和望远镜：

- `.orbit(OrbitDefinition.fixed(x, y))`
- `.orbit(OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed))`
- `.orbit(OrbitDefinition.around("parent_id", radiusX, radiusY, speed))`
- `.telescopeTexture("namespace:textures/environment/body_far.png")`
- `.telescopeTooltipId("other_id")`
- `.telescopeHitbox(radius, offset)`
- `.telescopeMoonPhase(true | false)`

望远镜坐标是直接 UI 空间坐标，不会自动缩放成太阳系单位，也不是 Minecraft
世界坐标。望远镜背景是 900x900 的可滚动画布，屏幕只显示其中一小块区域。
快速 smoke test 建议先用靠近中心的固定坐标，例如
`OrbitDefinition.fixed(-80, 70)`。大轨道半径可能让天体跑到当前视野外，需要玩家
拖动画面。

轨道定义每个 world tick 都会计算：

- `OrbitDefinition.fixed(x, y)` 保持在固定望远镜坐标。
- `OrbitDefinition.aroundOrigin(originX, originY, radiusX, radiusY, speed)`
  绕固定望远镜空间中心移动。
- `OrbitDefinition.around("parent_id", radiusX, radiusY, speed)` 绕另一个已注册
  天体的当前望远镜空间坐标移动。

移动轨道近似公式：

```text
x = centerX + cos(worldTime * speed) * radiusX
y = centerY + sin(worldTime * speed) * radiusY
```

`radiusX` 和 `radiusY` 不同会形成椭圆。`speed` 越大转得越快。Northstar 当前也
用这些望远镜/轨道位置估算旅行距离，所以天体移动时燃料成本可能变化。

环境：

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

火箭：

- `.atmosphereCost(value)`
- `.computingCost(value)`
- `.engineConstant(value)`
- `.heat(heatRating, heatConstant)`

兼容：

- `.seedOffset(value)`

一些 Java vararg 方法当前仍要求 `ResourceKey<Level>`，脚本里不够顺手，例如
`.telescopeHiddenIn(...)` 和 `.telescopeVisibleOnlyIn(...)`。Java 调用方可以使用；
如果 pack 脚本需要，后续可以继续补字符串 overload。

## 可达星球示例

当天体有真实维度并且应该成为火箭目标时，用这个形状：

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

## 仅望远镜天体示例

彗星、远景行星、没有维度的卫星或只用于氛围的天体，可以可见但不可旅行：

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

## 覆盖现有星球

KubeJS 内部使用 `registerOrReplace(...)`。这意味着 startup script 可以在需要时
替换内置 id。

这件事必须有意为之。现有物品数据会用字符串保存 planet id，所以重命名或移除
`mars`、`moon`、`venus` 这类 id 可能破坏旧星图、读数和返程票。

## 当前限制

- KubeJS 星球注册只在启动阶段执行。
- JSON/datapack 星球加载不是当前目标；pack 配置请使用 KubeJS。
- 天空和雾通过 `SkyProfile` 预设选择，不是完全脚本化的颜色或 renderer 数据。
- 注册星球不会生成维度。
