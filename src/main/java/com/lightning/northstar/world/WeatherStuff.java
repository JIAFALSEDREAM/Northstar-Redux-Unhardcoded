package com.lightning.northstar.world;

import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WeatherStuff {

    public enum WeatherCondition {
        CLEAR,
        RAINY,
        STORMY
    }

    public static Map<Pair<Pair<Direction, Direction>, Pair<WeatherCondition, Integer>>, ResourceKey<Level>> managedPlanets = new HashMap<>();

    // @SubscribeEvent
    // public static void onWorldTick(TickEvent.LevelTickEvent event){
    // for(Entry<Pair<Pair<Direction, Direction>, Pair<WeatherCondition, Integer>>, ResourceKey<Level>> entries:    managedPlanets.entrySet()) {
    // for(Pair<Pair<Direction, Direction>, Pair<WeatherCondition, Integer>> entries2 : entries.getKey()) {
    //
    // }
    // }
    // }

    public static void init() {
        managedPlanets.clear();
    }

    public Pair<Direction, Direction> getWindDirection(ResourceKey<Level> lev) {
        if (managedPlanets.containsValue(lev)) {
            for (Entry<Pair<Pair<Direction, Direction>, Pair<WeatherCondition, Integer>>, ResourceKey<Level>> entries : managedPlanets.entrySet()) {
                if (entries.getValue() == lev) {
                    return entries.getKey().getFirst();
                }

            }
        }
        return null;
    }

    public WeatherCondition getWeatherConditions(ResourceKey<Level> lev) {
        if (managedPlanets.containsValue(lev)) {
            for (Entry<Pair<Pair<Direction, Direction>, Pair<WeatherCondition, Integer>>, ResourceKey<Level>> entries : managedPlanets.entrySet()) {
                if (entries.getValue() == lev) {
                    return entries.getKey().getSecond().getFirst();
                }
            }
        }
        return WeatherCondition.CLEAR;
    }

    public static boolean hasWind(ResourceKey<Level> lev) {
        return NorthstarPlanets.hasWind(lev);
    }

    public static boolean hasWeather(ResourceKey<Level> lev) {
        return NorthstarPlanets.hasWeather(lev);
    }

}
