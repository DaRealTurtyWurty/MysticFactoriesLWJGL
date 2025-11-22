package dev.turtywurty.mysticfactories.world;

import org.jetbrains.annotations.NotNull;

// TODO: Registry
public record Weather(String name) {
    public static final Weather CLEAR = new Weather("clear");
    public static final Weather RAIN = new Weather("rain");
    public static final Weather STORM = new Weather("storm");

    @NotNull
    @Override
    public String toString() {
        return name;
    }
}
