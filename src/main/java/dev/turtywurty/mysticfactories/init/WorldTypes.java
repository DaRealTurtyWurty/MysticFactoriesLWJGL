package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.gen.impl.OverworldWorldGenerator;
import dev.turtywurty.mysticfactories.world.seed.RandomSeedSource;

import java.util.function.Function;

public class WorldTypes {
    public static final WorldType OVERWORLD = register("overworld",
            builder -> builder.generator(() -> new OverworldWorldGenerator(new RandomSeedSource())).build());

    private WorldTypes() {
    }

    public static void init() {
    }

    public static WorldType register(String name, Function<WorldType.Builder, WorldType> worldType) {
        Identifier id = Identifier.of(name);
        return Registries.WORLD_TYPES.register(id, worldType.apply(new WorldType.Builder(id)));
    }
}
