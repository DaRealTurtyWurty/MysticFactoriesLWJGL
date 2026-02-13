package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.feature.Feature;
import dev.turtywurty.mysticfactories.world.gen.WorldGeneratorType;
import dev.turtywurty.mysticfactories.world.tile.TileType;

public class RegistryKeys {
    public static final RegistryKey<EntityType<? extends Entity>> ENTITY_TYPES = create(Identifier.of("entity_type"));
    public static final RegistryKey<TileType> TILE_TYPES = create(Identifier.of("tile_type"));
    public static final RegistryKey<WorldType> WORLD_TYPES = create(Identifier.of("world_type"));
    public static final RegistryKey<WorldGeneratorType> WORLD_GENERATORS = create(Identifier.of("world_generator"));
    public static final RegistryKey<Biome> BIOMES = create(Identifier.of("biome"));
    public static final RegistryKey<Feature> FEATURES = create(Identifier.of("feature"));

    public static <T extends Registerable> RegistryKey<T> create(Identifier id) {
        return new RegistryKey<>(id);
    }
}
