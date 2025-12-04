package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.biome.IntProvider;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RingPlacementShape implements PlacementShape {
    private final IntProvider radius;

    public RingPlacementShape(IntProvider radius) {
        this.radius = radius;
    }

    @Override
    public List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts) {
        List<TilePos> positions = new ArrayList<>();
        int centerX = chunkX * ChunkPos.SIZE + ChunkPos.HALF_SIZE;
        int centerY = chunkY * ChunkPos.SIZE + ChunkPos.HALF_SIZE;
        for (int i = 0; i < attempts; i++) {
            int r = radius.get(random);
            double angle = random.nextDouble() * 2 * Math.PI;
            int x = centerX + (int) (r * Math.cos(angle));
            int y = centerY + (int) (r * Math.sin(angle));
            positions.add(new TilePos(x, y));
        }

        return positions;
    }
}
