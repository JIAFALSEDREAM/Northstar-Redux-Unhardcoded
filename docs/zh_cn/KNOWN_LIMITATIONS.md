# 已知限制

`Create: Northstar - Redux Unhardcoded` 当前专注于移除硬编码行为并暴露扩展钩子。
registry 已经移除了许多星球专用分支，但这还不是完整的数据驱动太阳系框架。

## 尚未实现

- JSON/datapack 星球加载。这是有意不做的当前目标；KubeJS 是支持的 pack 侧配置路径。
- planet 文件的公开 JSON schema。它和上面的非目标绑定。
- KubeJS 生成 planet JSON。推荐路径是 startup-script 注册。
- 自动创建维度、生物群系、区块生成器或世界生成。
- 完全数据驱动的天空、雾、云、天气粒子和地平线渲染。
- 超出当前 `SkyProfile` 预设的每星球 renderer 颜色配置。

## 当前兼容选择

现有 id 会保留，以兼容旧物品和存档。有些并不是物理上最干净的 API 概念，但能
保持 Redux 行为：

- `earth_orbit` 作为维度定义存在，但不是火箭目标 id。
- `earth_moon` 作为可观测 legacy id 保留，同时 `moon` 仍是月球维度的主定义。
- `jupiter`、`saturn`、`uranus`、`neptune`、`pluto`、`eris`、`ceres` 和
  `phobos_deimos` 等可观测天体即使不能被火箭抵达，也仍是 registry 条目。

## 重力

`gravityMultiplier` 是主要星球值，但部分实体分类仍保留 legacy 兼容 helper。
船、矿车、箭、物品、投掷物和生物实体在 Redux 里不一定使用完全相同的硬编码值。

为了兼容，这些分类差异集中在 `NorthstarPlanets` helper 方法后面。它们还没有
成为显式的 `PlanetDefinition` 字段。

## Sky Profile

第一轮 renderer 支持这些可复用 profile：

- `DEFAULT`
- `SPACE`
- `MOON_LIKE`
- `MARS_LIKE`
- `VENUS_LIKE`
- `MERCURY_LIKE`

这些 profile 会选择现有 Northstar 天空/雾/天气路径。它们用于让新星球先复用
已知行为，直到 renderer 能拆成真正的数据字段。

## KubeJS 易用性

常见路径已经能从 startup script 工作，包括维度 id 和望远镜贴图的字符串 overload。
一些 Java-only 方法仍需要更适合脚本的 overload，尤其是接收 `ResourceKey<Level>`
varargs 的望远镜可见性限制。

## 仍可能硬编码的区域

当前工作重点是星球选择、火箭目标、望远镜渲染、环境查询和兼容 helper。其他内容
仍可能在普通 mod 系统里硬编码：

- 维度 JSON/worldgen 和生物群系放置
- mob 和生成规则
- 方块、流体和物品 progression
- 星球专用 recipe 或 loot
- 单个 sky profile 内部的 renderer 常量

添加新星球内容前，优先给 `PlanetDefinition` 补缺失字段，并让系统通过 registry
取数。
