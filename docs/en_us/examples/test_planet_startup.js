// Example KubeJS startup script for Create: Northstar - Redux Unhardcoded.
// Place in kubejs/startup_scripts/ and restart the game.
//
// This only registers Northstar planet behavior. The dimension, language key,
// and texture must be supplied by a mod, KubeJS pack, datapack, or resource pack.

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
