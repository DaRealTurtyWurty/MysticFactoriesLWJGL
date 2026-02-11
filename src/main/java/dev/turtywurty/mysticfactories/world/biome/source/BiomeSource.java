package dev.turtywurty.mysticfactories.world.biome.source;

import dev.turtywurty.mysticfactories.world.biome.Biome;

public abstract class BiomeSource {
    protected final long seed;

    protected BiomeSource(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return this.seed;
    }

    public Biome getBiome(int x, int z, float terrainNoise) {
        return getBiome(x, z);
    }

    public abstract Biome getBiome(int x, int z);
}
