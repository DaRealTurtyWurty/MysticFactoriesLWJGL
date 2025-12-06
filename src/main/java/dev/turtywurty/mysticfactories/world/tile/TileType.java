package dev.turtywurty.mysticfactories.world.tile;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import lombok.Getter;

@Getter
public class TileType implements Registerable {
    private final Identifier id;
    private final boolean solid;

    public TileType(Identifier id, boolean solid) {
        if (id == null)
            throw new IllegalArgumentException("TileType id cannot be null");

        this.id = id;
        this.solid = solid;
    }

    public static class Builder {
        private final Identifier id;
        private boolean solid = true;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder notSolid() {
            this.solid = false;
            return this;
        }

        public TileType build() {
            return new TileType(this.id, this.solid);
        }
    }
}
