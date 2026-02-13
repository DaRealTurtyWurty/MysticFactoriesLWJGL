package dev.turtywurty.mysticfactories.server;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.WorldConnection;
import dev.turtywurty.mysticfactories.world.WorldData;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.physics.CollisionResolver;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import lombok.Setter;

public class ServerWorld extends World {
    @Setter
    private WorldConnection connection;

    public ServerWorld(WorldType worldType, long seed) {
        super(worldType, new WorldData(seed));
    }

    public ServerWorld(WorldType worldType) {
        this(worldType, World.randomizeWorldSeed());
    }

    @Override
    public void setTile(TilePos pos, TileType type) {
        super.setTile(pos, type);
        if (this.connection != null) {
            this.connection.sendTileUpdate(this.worldType, pos, type);
        }
    }

    @Override
    public void tick(double delta) {
        processPendingEntityRemovals();

        for (Entity entity : this.tickingEntities) {
            if (entity.isRemoved())
                continue;

            CollisionResolver.moveEntity(this, entity, delta);
            if (entity.isRemoved())
                continue;

            entity.tick(delta);
        }

        processPendingEntityRemovals();
    }
}
