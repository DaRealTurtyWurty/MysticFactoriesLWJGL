package dev.turtywurty.mysticfactories.world.feature.shape;

import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CurvedPathPlacementShape implements PlacementShape {
    private final int length;
    private final int maxCurveAngle;

    public CurvedPathPlacementShape(int length, int maxCurveAngle) {
        this.length = length;
        this.maxCurveAngle = maxCurveAngle;
    }

    @Override
    public List<TilePos> getPositions(WorldView world, Random random, int chunkX, int chunkY, int attempts) {
        List<TilePos> positions = new ArrayList<>();
        int chunkStartX = chunkX * ChunkPos.SIZE;
        int chunkStartY = chunkY * ChunkPos.SIZE;

        for (int i = 0; i < attempts; i++) {
            int startX = chunkStartX + random.nextInt(ChunkPos.SIZE);
            int startY = chunkStartY + random.nextInt(ChunkPos.SIZE);
            double angle = random.nextDouble() * 2 * Math.PI;

            for (int j = 0; j < length; j++) {
                int x = startX + (int) (j * Math.cos(angle));
                int y = startY + (int) (j * Math.sin(angle));
                positions.add(new TilePos(x, y));

                // Randomly adjust the angle to create a curve
                angle += (random.nextDouble() * 2 - 1) * Math.toRadians(maxCurveAngle);
            }
        }

        return positions;
    }
}
