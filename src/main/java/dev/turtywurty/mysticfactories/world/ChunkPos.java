package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.tile.TilePos;
import org.joml.Vector2i;

public class ChunkPos extends Vector2i {
    public static final int SIZE = 32;
    public static final int HALF_SIZE = SIZE / 2;

    public ChunkPos(int x, int z) {
        super(x, z);
    }

    public ChunkPos() {
        super();
    }

    public static ChunkPos fromTilePos(TilePos tilePos) {
        int chunkX = Math.floorDiv(tilePos.x(), SIZE);
        int chunkZ = Math.floorDiv(tilePos.y(), SIZE);
        return new ChunkPos(chunkX, chunkZ);
    }

    public int z() {
        return this.y;
    }

    public TilePos toTilePos(int localX, int localZ) {
        return new TilePos(this.x * SIZE + localX, z() * SIZE + localZ);
    }
}
