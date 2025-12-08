package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.gen.WorldGeneratorType;
import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.LinkedHashMap;
import java.util.Map;

public class Registries {
    private static final Map<RegistryKey<?>, Registry<?>> REGISTRIES = new LinkedHashMap<>();

    public static final Registry<EntityType<? extends Entity>> ENTITY_TYPES = createRegistry(RegistryKeys.ENTITY_TYPES, Identifier.of("default")); // TODO: Replace with actual default ID
    public static final Registry<TileType> TILE_TYPES = createRegistry(RegistryKeys.TILE_TYPES, Identifier.of("default")); // TODO: Replace with actual default ID
    public static final Registry<WorldType> WORLD_TYPES = createRegistry(RegistryKeys.WORLD_TYPES, Identifier.of("default")); // TODO: Replace with actual default ID
    public static final Registry<WorldGeneratorType> WORLD_GENERATORS = createRegistry(RegistryKeys.WORLD_GENERATORS, Identifier.of("default")); // TODO: Replace with actual default ID
    public static final Registry<Biome> BIOMES = createRegistry(RegistryKeys.BIOMES, Identifier.of("default")); // TODO: Replace with actual default ID

    public static <T extends Registerable> Registry<T> createRegistry(RegistryKey<T> key, Identifier defaultId) {
        Registry<T> registry = new DefaultedRegistry<>(defaultId);
        REGISTRIES.put(key, registry);
        return registry;
    }

    public static <T extends Registerable> Registry<T> getRegistry(RegistryKey<T> key) {
        @SuppressWarnings("unchecked")
        Registry<T> registry = (Registry<T>) REGISTRIES.get(key);
        if (registry == null)
            throw new IllegalStateException("Registry not found for key: " + key);

        return registry;
    }

    public static Map<RegistryKey<?>, Registry<?>> getAllRegistries() {
        return Map.copyOf(REGISTRIES);
    }
}
