package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.world.tileentity.TileEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WorldView {
    Map<ChunkPos, Chunk> getChunks();

    List<Entity> getEntities();

    WorldType getWorldType();

    WorldData getWorldData();

    Optional<Chunk> getChunk(int chunkX, int chunkY);

    Optional<Chunk> getChunk(ChunkPos chunkPos);

    Optional<TileType> getTile(TilePos pos);

    Optional<TileEntity> getTileEntity(TilePos pos);
}
