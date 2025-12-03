package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.util.Direction;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LinePlacementShape implements PlacementShape {
    private final Direction direction;
    private final int length;

    public LinePlacementShape(Direction direction, int length) {
        this.direction = direction;
        this.length = Math.clamp(length, 1, ChunkPos.SIZE);
    }

    @Override
    public List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts) {
        List<TilePos> positions = new ArrayList<>();
        for (int i = 0; i < attempts; i++) {
            int startX = chunkX * ChunkPos.SIZE + random.nextInt(ChunkPos.SIZE);
            int startY = chunkY * ChunkPos.SIZE + random.nextInt(ChunkPos.SIZE);

            for (int j = 0; j < length; j++) {
                int x = startX + direction.toDeltaX() * j;
                int y = startY + direction.toDeltaY() * j;

                // Ensure the position is within the chunk bounds
                if (x >= chunkX * ChunkPos.SIZE && x < (chunkX + 1) * ChunkPos.SIZE &&
                        y >= chunkY * ChunkPos.SIZE && y < (chunkY + 1) * ChunkPos.SIZE) {
                    positions.add(new TilePos(x, y));
                }
            }
        }

        return positions;
    }
}
