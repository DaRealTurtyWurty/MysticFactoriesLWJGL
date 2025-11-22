package dev.turtywurty.mysticfactories.world.tile;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TileRegistry {
    private final Map<Identifier, TileType> tiles = new HashMap<>();

    public TileType register(TileType tileType) {
        Identifier id = tileType.getId();
        if (this.tiles.containsKey(id))
            throw new IllegalArgumentException("TileType already registered: " + id);

        this.tiles.put(id, tileType);
        return tileType;
    }

    public Optional<TileType> get(Identifier id) {
        return Optional.ofNullable(this.tiles.get(id));
    }

    public Map<Identifier, TileType> getAll() {
        return this.tiles;
    }

    public void cleanup() {
        this.tiles.clear();
    }
}
