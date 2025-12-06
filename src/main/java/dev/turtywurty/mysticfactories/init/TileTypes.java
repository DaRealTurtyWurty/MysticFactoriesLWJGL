package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.function.Function;

public class TileTypes {
    public static final TileType GRASS = register("grass", TileType.Builder::build);
    public static final TileType SAND = register("sand", TileType.Builder::build);
    public static final TileType WATER = register("water", builder -> builder.notSolid().build());

    private TileTypes() {
    }

    public static void init() {
    }

    public static TileType register(String name, Function<TileType.Builder, TileType> tileType) {
        Identifier id = Identifier.of(name);
        return Registries.TILE_TYPES.register(id, tileType.apply(new TileType.Builder(id)));
    }
}
