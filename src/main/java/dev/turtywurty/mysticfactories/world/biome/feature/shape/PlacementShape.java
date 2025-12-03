package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.List;
import java.util.Random;

@FunctionalInterface
public interface PlacementShape {
    List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts);
}
