package dev.turtywurty.mysticfactories.world.gen.impl;

import dev.turtywurty.mysticfactories.init.Biomes;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.biome.source.BiomeSource;
import dev.turtywurty.mysticfactories.world.biome.source.MultiNoiseBiomeSource;
import dev.turtywurty.mysticfactories.world.biome.surface.SurfaceContext;
import dev.turtywurty.mysticfactories.world.biome.surface.SurfaceProfile;
import dev.turtywurty.mysticfactories.world.biome.surface.SurfaceRule;
import dev.turtywurty.mysticfactories.world.feature.Feature;
import dev.turtywurty.mysticfactories.world.feature.FeaturePlacementContext;
import dev.turtywurty.mysticfactories.world.feature.FeatureRule;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import dev.turtywurty.mysticfactories.world.seed.SeedSource;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseType;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OverworldWorldGenerator extends WorldGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverworldWorldGenerator.class);
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
                Biomes.OVERWORLD_PLAINS,
                Biomes.OVERWORLD_HILLS,
                Biomes.OVERWORLD_COAST,
                Biomes.OCEAN
        ));
    }

    @Override
    public void generate(World world, Chunk chunk) {
        generateSurface(world, chunk);
        generateFeatures(world, chunk);
    }

    private void generateSurface(World world, Chunk chunk) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int z = 0; z < Chunk.SIZE; z++) {
                var pos = new TilePos(
                        chunk.getPos().x * Chunk.SIZE + x,
                        chunk.getPos().y * Chunk.SIZE + z
                );

                float noiseValue = this.noise.getNoise(pos.x, pos.y);
                Biome biome = this.biomeSource.getBiome(pos.x(), pos.y(), noiseValue);
                SurfaceProfile surfaceProfile = biome.getSurfaceProfile();
                var ctx = new SurfaceContext(noiseValue, pos.x(), pos.y());

                boolean matchedRule = false;
                for (SurfaceRule surfaceRule : surfaceProfile.surfaceRules()) {
                    if (surfaceRule.matches(ctx)) {
                        chunk.setTile(pos, surfaceRule.resultTile(), biome);
                        matchedRule = true;
                        break;
                    }
                }

                if (matchedRule)
                    continue;

                if (surfaceProfile.primaryFluid() != null && noiseValue < 0.0f) {
                    chunk.setTile(pos, surfaceProfile.primaryFluid(), biome);
                } else {
                    chunk.setTile(pos, surfaceProfile.primarySurface(), biome);
                }
            }
        }
    }

    private void generateFeatures(World world, Chunk chunk) {
        Random rng = chunkRandom(chunk.getPos().x(), chunk.getPos().y());
        int totalSuccessfulPlacements = 0;
        int totalRulesWithPlacements = 0;

        Set<Biome> biomes = new HashSet<>(chunk.getBiomes().values());
        for (Biome biome : biomes) {
            for (FeatureRule rule : biome.getFeatureRules()) {
                Feature feature = Registries.FEATURES.getOrThrow(rule.featureId());
                int attempts = Math.max(0, rule.attemptsPerChunk());
                List<TilePos> origins = rule.placementShape()
                        .getPositions(world, rng, chunk.getPos().x(), chunk.getPos().y(), attempts);
                int successfulPlacements = 0;

                for (TilePos origin : origins) {
                    if (!chunk.contains(origin))
                        continue;

                    if (chunk.getBiome(origin).orElse(null) != biome)
                        continue;

                    if (!rule.placementCondition().canPlace(world, origin))
                        continue;

                    int count = Math.max(1, rule.countProvider().get(rng));
                    for (int i = 0; i < count; i++) {
                        if (feature.place(new FeaturePlacementContext(world, rng, origin))) {
                            successfulPlacements++;
                        }
                    }
                }

                if (successfulPlacements > 0) {
                    totalRulesWithPlacements++;
                    totalSuccessfulPlacements += successfulPlacements;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Generated feature '{}' {} time(s) in chunk ({}, {}) for biome '{}'",
                                rule.featureId(),
                                successfulPlacements,
                                chunk.getPos().x(),
                                chunk.getPos().y(),
                                biome.getId());
                    }
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Feature pass complete for chunk ({}, {}): {} successful placement(s) across {} rule(s)",
                    chunk.getPos().x(),
                    chunk.getPos().y(),
                    totalSuccessfulPlacements,
                    totalRulesWithPlacements);
        }
    }

    @Override
    public Biome getBiome(int x, int z) {
        float noiseValue = this.noise.getNoise(x, z);
        return this.biomeSource.getBiome(x, z, noiseValue);
    }
}
