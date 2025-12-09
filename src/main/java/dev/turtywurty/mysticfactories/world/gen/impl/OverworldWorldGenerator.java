package dev.turtywurty.mysticfactories.world.gen.impl;

import dev.turtywurty.mysticfactories.init.Biomes;
import dev.turtywurty.mysticfactories.init.TileTypes;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.biome.source.BiomeSource;
import dev.turtywurty.mysticfactories.world.biome.source.MultiNoiseBiomeSource;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import dev.turtywurty.mysticfactories.world.seed.SeedSource;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseType;

import java.util.List;

public class OverworldWorldGenerator extends WorldGenerator {
    private final FastNoise noise;

    public OverworldWorldGenerator(SeedSource seedSource) {
        super(seedSource);
        this.biomeSource = createBiomeSource(this.seed);
        this.noise = createOverworldNoise(seed);
    }

    private static FastNoise createOverworldNoise(long seed) {
        int intSeed = Long.hashCode(seed); // fold long seed into int
        return FastNoise.builder()
                .seed(intSeed)
                .type(NoiseType.SIMPLEX2)
                .fractal(FractalType.FBM)
                .frequency(0.01f)
                .octaves(4)
                .build();
    }

    private static BiomeSource createBiomeSource(long seed) {
        return new MultiNoiseBiomeSource(seed, List.of(
                Biomes.OVERWORLD,
                Biomes.OVERWORLD_HILLS,
                Biomes.OVERWORLD_COAST,
                Biomes.OCEAN
        ));
    }

    @Override
    public void generate(WorldView world, Chunk chunk) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int z = 0; z < Chunk.SIZE; z++) {
                var pos = new TilePos(
                        chunk.getPos().x * Chunk.SIZE + x,
                        chunk.getPos().y * Chunk.SIZE + z
                );

                TileType grass = TileTypes.GRASS;
                TileType water = TileTypes.WATER;
                TileType sand = TileTypes.SAND;
                float noiseValue = this.noise.getNoise(pos.x, pos.y);
                if (noiseValue < -0.2f) {
                    chunk.setTile(pos, water);
                } else if (noiseValue < 0.0f) {
                    chunk.setTile(pos, sand);
                } else {
                    chunk.setTile(pos, grass);
                }
            }
        }
    }
}
