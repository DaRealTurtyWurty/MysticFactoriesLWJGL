package dev.turtywurty.mysticfactories.world.gen;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class WorldGeneratorType implements Registerable {
    private final WorldGenerator.WorldGeneratorFactory factory;
    @Setter
    private Identifier id;

    public WorldGeneratorType(WorldGenerator.WorldGeneratorFactory factory) {
        if (factory == null)
            throw new IllegalArgumentException("WorldGenerator factory cannot be null");

        this.factory = factory;
    }

    public WorldGenerator createGenerator() {
        return this.factory.create();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WorldGenerator.WorldGeneratorFactory factory;

        public Builder factory(WorldGenerator.WorldGeneratorFactory factory) {
            this.factory = factory;
            return this;
        }

        public WorldGeneratorType build() {
            if (this.factory == null)
                throw new IllegalStateException("WorldGenerator factory must be set");

            return new WorldGeneratorType(this.factory);
        }
    }
}
