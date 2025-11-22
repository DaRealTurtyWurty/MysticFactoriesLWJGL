package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.Map;

public interface WorldConnection {
    /**
     * Sends a granular tile change to the client world.
     */
    void sendTileUpdate(WorldType worldType, TilePos pos, TileType type);

    /**
     * Sends the initial full state (or a resync) to the client world.
     */
    void sendFullState(WorldType worldType, Map<ChunkPos, Chunk> chunks);
}
