package dev.turtywurty.mysticfactories.world.feature;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.Random;

public record FeaturePlacementContext(World world, Random random, TilePos origin) {
}
