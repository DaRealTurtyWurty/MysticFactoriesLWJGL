package dev.turtywurty.mysticfactories.world.gen;

import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.biome.Biome;

public interface WorldGenerator {
    /**
     * Generate or populate the given chunk.
     */
    void generate(WorldView world, Chunk chunk);

    Biome getBiomeAt(int x, int z);

    long getSeed();
}
