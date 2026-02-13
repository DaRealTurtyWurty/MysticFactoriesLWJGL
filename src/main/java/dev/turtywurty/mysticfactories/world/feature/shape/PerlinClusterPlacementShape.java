package dev.turtywurty.mysticfactories.world.feature.shape;

import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerlinClusterPlacementShape implements PlacementShape {
    private final float noiseScale;
    private final float threshold;
    private final FastNoise noise;

    public PerlinClusterPlacementShape(float noiseScale, float threshold) {
        this(noiseScale, threshold, FastNoise.builder()
                .type(NoiseType.PERLIN)
                .build());
    }

    public PerlinClusterPlacementShape(float noiseScale, float threshold, FastNoise noise) {
        this.noiseScale = noiseScale;
        this.threshold = threshold;
        this.noise = noise;
    }

    @Override
    public List<TilePos> getPositions(WorldView world, Random random, int chunkX, int chunkY, int attempts) {
        List<TilePos> positions = new ArrayList<>();
        int baseX = chunkX * ChunkPos.SIZE;
        int baseY = chunkY * ChunkPos.SIZE;

        for (int x = 0; x < ChunkPos.SIZE; x++) {
            for (int y = 0; y < ChunkPos.SIZE; y++) {
                float noiseValue = noise.getNoise((baseX + x) * noiseScale, (baseY + y) * noiseScale);
                if (noiseValue > threshold && positions.size() < attempts) {
                    positions.add(new TilePos(baseX + x, baseY + y));
                }
            }
        }

        return positions;
    }
}
