package dev.turtywurty.mysticfactories.client.world;

import dev.turtywurty.mysticfactories.client.render.world.WorldRenderer;
import dev.turtywurty.mysticfactories.world.*;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Setter
public class ClientWorld extends World {
    private UUID localPlayerId;

    public ClientWorld(WorldType worldType, long seed) {
        super(worldType, new WorldData(seed));
    }

    public void applyFullState(Map<ChunkPos, Chunk> chunks) {
        this.chunks.clear();
        this.chunks.putAll(chunks);
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
        // Client-side effects or interpolation could go here later.
    }
}
