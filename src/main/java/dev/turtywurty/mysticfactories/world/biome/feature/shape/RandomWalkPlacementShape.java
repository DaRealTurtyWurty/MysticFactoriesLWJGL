package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.List;
import java.util.Random;

public class RandomWalkPlacementShape implements PlacementShape {
    private final IntProvider steps;

    public RandomWalkPlacementShape(IntProvider steps) {
        this.steps = steps;
    }

    @Override
    public List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts) {

    }
}
