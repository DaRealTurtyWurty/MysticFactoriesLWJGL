package dev.turtywurty.mysticfactories.client.world;

import dev.turtywurty.mysticfactories.world.*;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Setter
public class ClientWorld extends World {
    private UUID localPlayerId;

    public ClientWorld(WorldType worldType, long seed) {
        super(worldType, new WorldData(seed));
    }

    public void applyFullState(WorldSnapshot snapshot) {
        this.chunks.clear();
        this.chunks.putAll(snapshot.chunks());
    }

    public void clearEntities() {
        this.entities.clear();
        this.tickingEntities.clear();
        this.pendingEntityRemovals.clear();
        this.localPlayerId = null;
    }

    public float getTileSize() {
        return 16f;
    }

    public Optional<Entity> getEntity(UUID id) {
        return this.entities.stream().filter(entity -> entity.getUuid().equals(id)).findFirst();
    }

    public void removeEntityById(UUID id) {
        getEntity(id).ifPresent(this::removeEntity);
    }

    public Optional<Entity> getLocalPlayer() {
        if (this.localPlayerId == null)
            return Optional.empty();

        return getEntity(this.localPlayerId);
    }

    @Override
    public void tick(double delta) {
        processPendingEntityRemovals();
        // Client-side effects or interpolation could go here later.
    }
}
