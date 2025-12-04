package dev.turtywurty.mysticfactories.world.biome.spawning;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

public record SpawnContext(World world, TilePos pos) {
}
