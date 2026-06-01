// Create: Northstar - Redux Unhardcoded 的 KubeJS startup script 示例。
// 放到 kubejs/startup_scripts/，然后重启游戏。
//
// 这只注册 Northstar 星球行为。维度、语言 key 和贴图必须由 mod、KubeJS pack、
// datapack 或 resource pack 提供。

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
