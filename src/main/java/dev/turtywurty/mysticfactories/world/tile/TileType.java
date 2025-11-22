package dev.turtywurty.mysticfactories.world.tile;

import dev.turtywurty.mysticfactories.util.Identifier;

public class TileType {
    private final Identifier id;
    private final boolean solid;

    public TileType(Identifier id, boolean solid) {
        if (id == null) {
            throw new IllegalArgumentException("TileType id cannot be null");
        }
        this.id = id;
        this.solid = solid;
    }

    public Identifier getId() {
        return this.id;
    }

    public boolean isSolid() {
        return this.solid;
    }
}
