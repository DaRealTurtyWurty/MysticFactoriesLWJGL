package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.world.tile.TileRegistry;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.util.Identifier;

public class TileTypes {
    public static TileType GRASS;
    public static TileType SAND;
    public static TileType WATER;

    private TileTypes() {}

    public static void register(TileRegistry registry) {
        GRASS = registry.register(new TileType(Identifier.of("grass"), false));
        SAND = registry.register(new TileType(Identifier.of("sand"), false));
        WATER = registry.register(new TileType(Identifier.of("water"), true));
    }
}
