package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.entity.Entity;

import java.util.List;
import java.util.Map;

public interface WorldView {
    Map<ChunkPos, Chunk> getChunks();

    List<Entity> getEntities();

    WorldType getWorldType();

    WorldData getWorldData();
}
