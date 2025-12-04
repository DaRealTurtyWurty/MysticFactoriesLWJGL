package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.WorldTypeRegistry;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import dev.turtywurty.mysticfactories.world.gen.impl.OverworldWorldGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class WorldTypes {
    public static WorldType OVERWORLD;

    private WorldTypes() {
    }

    public static void register(WorldTypeRegistry registry) {
        // TODO: Have a SeedSource, and also turn this into a lambda/factory
        WorldGenerator overworldGenerator = new OverworldWorldGenerator(ThreadLocalRandom.current().nextLong());

        OVERWORLD = registry.register(new WorldType(Identifier.of("overworld"), overworldGenerator));
    }
}
