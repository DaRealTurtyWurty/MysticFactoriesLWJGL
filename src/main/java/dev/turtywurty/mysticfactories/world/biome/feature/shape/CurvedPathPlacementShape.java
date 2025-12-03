package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

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
    public List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts) {

    }
}
