package dev.turtywurty.mysticfactories.world.feature;

import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

@FunctionalInterface
public interface PlacementCondition {
    boolean canPlace(WorldView world, TilePos pos);
}
