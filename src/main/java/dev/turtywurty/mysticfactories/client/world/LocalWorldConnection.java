package dev.turtywurty.mysticfactories.client.world;

import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.WorldConnection;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

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

    @Override
    public void sendEntitySpawn(WorldType worldType, Entity entity) {
        if (!isActive(worldType))
            return;

        var type = entity.getType();
        var clientEntity = type.create(this.clientWorld);
        clientEntity.getPosition().set(entity.getPosition());
        clientEntity.getVelocity().set(entity.getVelocity());
        clientEntity.setRotation(entity.getRotation());
        clientEntity.setOnGround(entity.isOnGround());
        clientEntity.setSilent(entity.isSilent());
        clientEntity.setUuid(entity.getUuid());

        this.clientWorld.addEntity(clientEntity);
    }

    @Override
    public void sendEntityRemove(WorldType worldType, UUID entityId) {
        if (!isActive(worldType))
            return;

        this.clientWorld.removeEntityById(entityId);
    }

    @Override
    public void sendPlayerBind(WorldType worldType, UUID playerId) {
        if (!isActive(worldType))
            return;

        this.clientWorld.setLocalPlayerId(playerId);
    }

    private boolean isActive(WorldType worldType) {
        return this.activeWorldType != null && this.activeWorldType.equals(worldType);
    }

    public ClientWorld clientWorld() {
        return clientWorld;
    }
}
