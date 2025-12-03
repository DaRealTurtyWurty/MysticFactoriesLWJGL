package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomScatterPlacementShape implements PlacementShape {
    @Override
    public List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts) {
        List<TilePos> positions = new ArrayList<>();
        for (int i = 0; i < attempts; i++) {
            int x = chunkX * ChunkPos.SIZE + random.nextInt(ChunkPos.SIZE);
            int y = chunkY * ChunkPos.SIZE + random.nextInt(ChunkPos.SIZE);
            positions.add(new TilePos(x, y));
        }

        return positions;
    }
}
