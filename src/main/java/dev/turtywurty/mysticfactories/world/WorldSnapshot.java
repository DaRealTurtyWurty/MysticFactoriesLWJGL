package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.entity.Entity;

import java.util.List;
import java.util.Map;

public record WorldSnapshot(Map<ChunkPos, Chunk> chunks, List<Entity> entities) {
    public WorldSnapshot {
        chunks = Map.copyOf(chunks);
        entities = List.copyOf(entities);
    }
}
