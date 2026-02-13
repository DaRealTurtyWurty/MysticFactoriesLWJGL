package dev.turtywurty.mysticfactories.world.gen;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.biome.source.BiomeSource;
import dev.turtywurty.mysticfactories.world.seed.SeedSource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Random;

@EqualsAndHashCode
@ToString
@Getter
public abstract class WorldGenerator implements Registerable {
    protected final SeedSource seedSource;
    protected final long seed;
    @Setter
    protected Identifier id;
    protected BiomeSource biomeSource;

    public WorldGenerator(SeedSource seedSource) {
        this(seedSource, null);
    }

    public WorldGenerator(SeedSource seedSource, BiomeSource biomeSource) {
        if (seedSource == null)
            throw new IllegalArgumentException("SeedSource cannot be null");

        this.seedSource = seedSource;
        this.seed = seedSource.get();
        this.biomeSource = biomeSource;
    }

    public abstract void generate(World world, Chunk chunk);

    public Biome getBiome(int x, int z) {
        if (this.biomeSource == null)
            throw new IllegalStateException("BiomeSource has not been set for this world generator");

        return this.biomeSource.getBiome(x, z);
    }

    protected void setBiomeSource(BiomeSource biomeSource) {
        this.biomeSource = biomeSource;
    }

    protected Random chunkRandom(int chunkX, int chunkZ) {
        long mixed = this.seed
                ^ ((long) chunkX * 341873128712L)
                ^ ((long) chunkZ * 132897987541L);
        return new Random(mixed);
    }

    @FunctionalInterface
    public interface WorldGeneratorFactory {
        WorldGenerator create();
    }
}
