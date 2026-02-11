package dev.turtywurty.mysticfactories.world.biome.surface;

import dev.turtywurty.mysticfactories.world.tile.TileType;

public interface SurfaceAction {
    TileType apply(SurfaceContext context);
}
