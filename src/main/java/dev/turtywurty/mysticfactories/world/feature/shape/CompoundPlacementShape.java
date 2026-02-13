package dev.turtywurty.mysticfactories.world.feature.shape;

import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CompoundPlacementShape implements PlacementShape {
    private final PlacementShape[] shapes;

    public CompoundPlacementShape(PlacementShape... shapes) {
        this.shapes = shapes;
    }

    @Override
    public List<TilePos> getPositions(WorldView world, Random random, int chunkX, int chunkY, int attempts) {
        List<TilePos> positions = new ArrayList<>();
        for (PlacementShape shape : shapes) {
            positions.addAll(shape.getPositions(world, random, chunkX, chunkY, attempts));
        }

        return positions;
    }
}
