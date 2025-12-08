package dev.turtywurty.mysticfactories.world.tile;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class TileType implements Registerable {
    @Setter
    private Identifier id;
    private final boolean solid;

    public TileType(boolean solid) {
        this.solid = solid;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean solid = true;

        public Builder notSolid() {
            this.solid = false;
            return this;
        }

        public TileType build() {
            return new TileType(this.solid);
        }
    }
}
