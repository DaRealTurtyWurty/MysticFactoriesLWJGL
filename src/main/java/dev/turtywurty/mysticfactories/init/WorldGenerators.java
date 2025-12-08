package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.gen.WorldGeneratorType;
import dev.turtywurty.mysticfactories.world.gen.impl.OverworldWorldGenerator;
import dev.turtywurty.mysticfactories.world.seed.RandomSeedSource;

@RegistryHolder
public class WorldGenerators {
    public static final WorldGeneratorType OVERWORLD = register("overworld",
            WorldGeneratorType.builder()
                    .factory(() -> new OverworldWorldGenerator(new RandomSeedSource()))
                    .build());

    public static <T extends WorldGeneratorType> T register(String name, T generatorType) {
        return Registries.WORLD_GENERATORS.register(Identifier.of(name), generatorType);
    }
}
