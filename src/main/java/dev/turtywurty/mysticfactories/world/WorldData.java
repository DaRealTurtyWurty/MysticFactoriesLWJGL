package dev.turtywurty.mysticfactories.world;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WorldData {
    @Getter
    private final long seed;
    private final Set<Weather> allowedWeathers = new HashSet<>();
    @Setter
    private boolean hasDaylightCycle = true;
    @Getter
    private int time = 0;
    @Getter
    private Weather weather = Weather.CLEAR;
    @Getter
    private int weatherTime = 0;
    @Setter
    private boolean hasWeatherCycle = true;

    public WorldData(long seed) {
        this.seed = seed;
    }

    public boolean hasDaylightCycle() {
        return hasDaylightCycle;
    }

    public void setTime(int time) {
        this.time = Math.abs(time);
    }

    public void setWeather(Weather weather) {
        if (weather == null)
            throw new IllegalArgumentException("Weather cannot be null");

        if (!allowedWeathers.contains(weather))
            throw new IllegalArgumentException("Weather " + weather + " is not allowed in this world");

        this.weather = weather;
    }

    public void setWeatherTime(int weatherTime) {
        this.weatherTime = Math.abs(weatherTime);
    }

    public Set<Weather> getAllowedWeathers() {
        return Set.copyOf(allowedWeathers);
    }

    public void setAllowedWeathers(Weather... allowedWeathers) {
        setAllowedWeathers(Arrays.asList(allowedWeathers));
    }

    public void setAllowedWeathers(Collection<Weather> allowedWeathers) {
        this.allowedWeathers.clear();
        this.allowedWeathers.addAll(allowedWeathers);
    }

    public boolean hasWeatherCycle() {
        return hasWeatherCycle;
    }
}
