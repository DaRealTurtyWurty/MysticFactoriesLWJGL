package dev.turtywurty.mysticfactories.world.biome.source;

import dev.turtywurty.mysticfactories.world.biome.Biome;

import java.util.Objects;

public class SingleBiomeSource extends BiomeSource {
    private final Biome biome;

    public SingleBiomeSource(long seed, Biome biome) {
        super(seed);
        this.biome = Objects.requireNonNull(biome, "Biome cannot be null");
    }

    public SingleBiomeSource(Biome biome) {
        this(0L, biome);
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biome;
    }
}
