package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class WorldType implements Registerable {
    private final Identifier id;
    private final Supplier<WorldGenerator> generator;

    public WorldType(Identifier id, Supplier<WorldGenerator> generator) {
        if (id == null)
            throw new IllegalArgumentException("WorldType id cannot be null");

        this.id = id;
        this.generator = generator;
    }

    public static class Builder {
        private final Identifier id;
        private Supplier<WorldGenerator> generator;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder generator(Supplier<WorldGenerator> generator) {
            this.generator = generator;
            return this;
        }

        public WorldType build() {
            return new WorldType(id, generator);
        }
    }
}
