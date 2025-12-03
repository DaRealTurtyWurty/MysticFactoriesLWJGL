package dev.turtywurty.mysticfactories.world.biome.feature;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

@FunctionalInterface
public interface PlacementCondition {
    boolean canPlace(World world, TilePos pos);
}
