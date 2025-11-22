package dev.turtywurty.mysticfactories.world.gen;

import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.WorldView;

public interface WorldGenerator {
    /**
     * Generate or populate the given chunk.
     */
    void generate(WorldView world, Chunk chunk);
}
