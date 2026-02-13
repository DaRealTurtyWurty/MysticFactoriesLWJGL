package dev.turtywurty.mysticfactories.world.tile;

import dev.turtywurty.mysticfactories.util.Direction;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import lombok.ToString;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@ToString
public class TilePos extends Vector2i {
    public TilePos(int x, int y) {
        super(x, y);
    }

    public TilePos() {
        super();
    }

    public static TilePos fromVector2i(Vector2i vec) {
        return new TilePos(vec.x, vec.y);
    }

    public static TilePos fromVector2d(Vector2d vec) {
        return new TilePos((int) Math.floor(vec.x), (int) Math.floor(vec.y));
    }

    public static TilePos fromLong(long packed) {
        int x = (int) (packed & 0xFFFFFFFFL);
        int y = (int) ((packed >> 32) & 0xFFFFFFFFL);
        return new TilePos(x, y);
    }

    public TilePos add(TilePos other) {
        return new TilePos(this.x + other.x, this.y + other.y);
    }

    public TilePos add(Vector2ic other) {
        return new TilePos(this.x + other.x(), this.y + other.y());
    }

    public TilePos sub(TilePos other) {
        return new TilePos(this.x - other.x, this.y - other.y);
    }

    public TilePos mul(int scalar) {
        return new TilePos(this.x * scalar, this.y * scalar);
    }

    public TilePos copy() {
        return new TilePos(this.x, this.y);
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(Math.floorDiv(this.x, ChunkPos.SIZE), Math.floorDiv(this.y, ChunkPos.SIZE));
    }

    public long toLong() {
        return (((long) this.x) & 0xFFFFFFFFL) | ((((long) this.y & 0xFFFFFFFFL) << 32));
    }

    public TilePos offset(Direction direction) {
        return offset(direction, 1);
    }

    public TilePos offset(Direction direction, int steps) {
        return add(direction.toDelta().mul(steps));
    }
}
