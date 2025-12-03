package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.tile.TilePos;
import org.joml.Vector2i;

public class ChunkPos extends Vector2i {
    public static final int SIZE = 32;

    public ChunkPos(int x, int y) {
        super(x, y);
    }

    public ChunkPos() {
        super();
    }

    public TilePos toTilePos(int localX, int localY) {
        return new TilePos(this.x * SIZE + localX, this.y * SIZE + localY);
    }

    public static ChunkPos fromTilePos(TilePos tilePos) {
        int chunkX = Math.floorDiv(tilePos.x(), SIZE);
        int chunkY = Math.floorDiv(tilePos.y(), SIZE);
        return new ChunkPos(chunkX, chunkY);
    }
}
