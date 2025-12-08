package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class Chunk {
    public static final int SIZE = ChunkPos.SIZE;

    private final ChunkPos pos;
    private final Map<TilePos, TileType> tiles = new HashMap<>();
    private final Map<TilePos, Biome> biomes = new HashMap<>();
    private int modificationCount = 0;

    public Chunk(ChunkPos pos) {
        this.pos = pos;
    }

    public void setTile(TilePos tilePos, TileType type) {
        this.tiles.put(tilePos, type);
        this.modificationCount++;
    }

    public void setTile(TilePos tilePos, TileType type, Biome biome) {
        setTile(tilePos, type);
        if (biome != null) {
            this.biomes.put(tilePos, biome);
        }
    }

    public Optional<TileType> getTile(TilePos tilePos) {
        return Optional.ofNullable(this.tiles.get(tilePos));
    }

    public Optional<Biome> getBiome(TilePos tilePos) {
        return Optional.ofNullable(this.biomes.get(tilePos));
    }

    public boolean contains(TilePos pos) {
        return this.tiles.containsKey(pos);
    }

    public boolean contains(int x, int y) {
        return contains(new TilePos(x, y));
    }
}
