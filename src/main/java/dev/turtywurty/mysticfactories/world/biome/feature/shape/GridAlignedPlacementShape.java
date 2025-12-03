package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.List;
import java.util.Random;

public class GridAlignedPlacementShape implements PlacementShape {
    private final int gridSize;

    public GridAlignedPlacementShape(int gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts) {
        List<TilePos> positions = new java.util.ArrayList<>();
        int chunkStartX = chunkX * ChunkPos.SIZE;
        int chunkStartY = chunkY * ChunkPos.SIZE;

        for (int i = 0; i < attempts; i++) {
            int x = chunkStartX + random.nextInt(ChunkPos.SIZE);
            int y = chunkStartY + random.nextInt(ChunkPos.SIZE);

            // Align to grid
            x = (x / gridSize) * gridSize;
            y = (y / gridSize) * gridSize;

            positions.add(new TilePos(x, y));
        }

        return positions;
    }
}
