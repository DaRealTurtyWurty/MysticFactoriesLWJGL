package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.Map;
import java.util.UUID;

public interface WorldConnection {
    /**
     * Sends a granular tile change to the client world.
     */
    void sendTileUpdate(WorldType worldType, TilePos pos, TileType type);

    /**
     * Sends the initial full state (or a resync) to the client world.
     */
    void sendFullState(WorldType worldType, Map<ChunkPos, Chunk> chunks);

    /**
     * Sends an entity spawn to the client.
     */
    void sendEntitySpawn(WorldType worldType, Entity entity);

    /**
     * Notifies the client that an entity should be removed.
     */
    void sendEntityRemove(WorldType worldType, UUID entityId);

    /**
     * Identifies which entity represents the local player.
     */
    void sendPlayerBind(WorldType worldType, UUID playerId);
}
