package dev.turtywurty.mysticfactories.client.world;

import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.WorldConnection;
import dev.turtywurty.mysticfactories.world.WorldType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode
@ToString
public final class LocalWorldConnection implements WorldConnection {
    private final ClientWorld clientWorld;
    private WorldType activeWorldType;

    public LocalWorldConnection(ClientWorld clientWorld) {
        this.clientWorld = clientWorld;
    }

    @Override
    public void sendTileUpdate(WorldType worldType, TilePos pos, TileType type) {
        if (activeWorldType == null || !activeWorldType.equals(worldType))
            return;

        this.clientWorld.setTile(pos, type);
    }

    @Override
    public void sendFullState(WorldType worldType, Map<ChunkPos, Chunk> chunks) {
        this.activeWorldType = worldType;
        this.clientWorld.applyFullState(chunks);
    }

    public ClientWorld clientWorld() {
        return clientWorld;
    }
}
