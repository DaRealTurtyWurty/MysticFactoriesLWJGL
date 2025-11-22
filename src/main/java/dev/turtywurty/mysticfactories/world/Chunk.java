package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class Chunk {
    public static final int SIZE = ChunkPos.SIZE;

    private final ChunkPos pos;
    private final Map<TilePos, TileType> tiles = new HashMap<>();
    private int modificationCount = 0;

    public Chunk(ChunkPos pos) {
        this.pos = pos;
    }

    public void setTile(TilePos tilePos, TileType type) {
        this.tiles.put(tilePos, type);
        this.modificationCount++;
    }

    public Optional<TileType> getTile(TilePos tilePos) {
        return Optional.ofNullable(this.tiles.get(tilePos));
    }
}
