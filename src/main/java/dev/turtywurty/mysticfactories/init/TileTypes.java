package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.function.Function;

@RegistryHolder
public class TileTypes {
    public static final TileType GRASS = register("grass", TileType.builder().build());
    public static final TileType SAND = register("sand", TileType.builder().build());
    public static final TileType WATER = register("water", TileType.builder().notSolid().build());

    private TileTypes() {
    }

    public static TileType register(String name, TileType tileType) {
        Identifier id = Identifier.of(name);
        return Registries.TILE_TYPES.register(id, tileType);
    }
}
