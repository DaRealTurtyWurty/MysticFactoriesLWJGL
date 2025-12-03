package dev.turtywurty.mysticfactories.world.gen.impl;

import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;

public class OverworldWorldGenerator implements WorldGenerator {
    private final long seed;

    public OverworldWorldGenerator(long seed) {
        this.seed = seed;
    }

    @Override
    public void generate(WorldView world, Chunk chunk) {

    }
}
