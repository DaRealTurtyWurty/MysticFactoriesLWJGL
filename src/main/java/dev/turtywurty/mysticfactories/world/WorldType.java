package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import dev.turtywurty.mysticfactories.world.gen.WorldGeneratorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.function.Supplier;

@EqualsAndHashCode
@ToString
@Getter
public class WorldType implements Registerable {
    @Setter
    private Identifier id;
    private final Supplier<WorldGenerator> generator;

    public WorldType(Supplier<WorldGenerator> generator) {
        if (generator == null)
            throw new IllegalArgumentException("WorldGenerator supplier cannot be null");

        this.generator = generator;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Supplier<WorldGenerator> generator;

        public Builder generator(Supplier<WorldGenerator> generator) {
            this.generator = generator;
            return this;
        }

        public Builder generator(WorldGeneratorType generatorType) {
            if (generatorType == null)
                throw new IllegalArgumentException("WorldGeneratorType cannot be null");

            this.generator = generatorType::createGenerator;
            return this;
        }

        public WorldType build() {
            if (this.generator == null)
                throw new IllegalStateException("World generator supplier must be set");

            return new WorldType(generator);
        }
    }
}
