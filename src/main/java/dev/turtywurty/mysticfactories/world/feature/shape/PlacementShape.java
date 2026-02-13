package dev.turtywurty.mysticfactories.world.feature.shape;

import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.List;
import java.util.Random;

@FunctionalInterface
public interface PlacementShape {
    List<TilePos> getPositions(WorldView world, Random random, int chunkX, int chunkY, int attempts);
}
