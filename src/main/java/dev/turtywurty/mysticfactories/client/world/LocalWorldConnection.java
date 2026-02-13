package dev.turtywurty.mysticfactories.client.world;

import dev.turtywurty.mysticfactories.world.WorldConnection;
import dev.turtywurty.mysticfactories.world.WorldSnapshot;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.world.tileentity.StackedTileEntity;
import dev.turtywurty.mysticfactories.world.tileentity.TileEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
    public void sendFullState(WorldType worldType, WorldSnapshot snapshot) {
        this.activeWorldType = worldType;
        this.clientWorld.applyFullState(snapshot);
        this.clientWorld.clearEntities();
        for (Entity entity : snapshot.entities()) {
            spawnEntity(entity);
        }
    }

    @Override
    public void sendEntitySpawn(WorldType worldType, Entity entity) {
        if (!isActive(worldType))
            return;

        spawnEntity(entity);
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

    private void spawnEntity(Entity entity) {
        var type = entity.getType();
        var clientEntity = type.create(this.clientWorld);
        clientEntity.setPosition(entity.getPosition().x, entity.getPosition().y);
        clientEntity.getVelocity().set(entity.getVelocity());
        clientEntity.setRotation(entity.getRotation());
        clientEntity.setOnGround(entity.isOnGround());
        clientEntity.setSilent(entity.isSilent());
        clientEntity.setUuid(entity.getUuid());

        if (entity instanceof StackedTileEntity serverStacked && clientEntity instanceof StackedTileEntity clientStacked) {
            for (TileEntity serverEntry : serverStacked.getEntries()) {
                var entryType = serverEntry.getType();
                var clientEntryEntity = entryType.create(this.clientWorld);
                if (!(clientEntryEntity instanceof TileEntity clientEntry))
                    continue;

                clientEntry.setRotation(serverEntry.getRotation());
                clientEntry.setOnGround(serverEntry.isOnGround());
                clientEntry.setSilent(serverEntry.isSilent());
                clientEntry.setUuid(serverEntry.getUuid());
                clientStacked.push(clientEntry);
            }
        }

        this.clientWorld.addEntity(clientEntity);
    }

    public ClientWorld clientWorld() {
        return clientWorld;
    }
}
