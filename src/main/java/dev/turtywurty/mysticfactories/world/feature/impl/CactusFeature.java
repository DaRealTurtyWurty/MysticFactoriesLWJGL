package dev.turtywurty.mysticfactories.world.feature.impl;

import dev.turtywurty.mysticfactories.init.TileTypes;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.feature.Feature;
import dev.turtywurty.mysticfactories.world.feature.FeaturePlacementContext;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tileentity.impl.CactusTileEntity;

import java.util.Random;

public class CactusFeature extends Feature {
    @Override
    public boolean place(FeaturePlacementContext context) {
        World world = context.world();
        TilePos pos = context.origin();
        Random rng = context.random();

        if (world.getTile(pos).orElse(null) != TileTypes.SAND || world.getTileEntity(pos).isPresent())
            return false;

        int height = 1 + rng.nextInt(3);
        for (int i = 0; i < height; i++) {
            world.pushTileEntity(pos, new CactusTileEntity());
        }

        return true;
    }
}
