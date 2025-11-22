package dev.turtywurty.mysticfactories.world.entity;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.World;

public record EntityType<T extends Entity>(Identifier id, EntityFactory<T> factory, boolean shouldTick) {
    public T create(World world) {
        return factory.create(this, world);
    }

    @FunctionalInterface
    public interface EntityFactory<T extends Entity> {
        T create(EntityType<T> type, World world);
    }

    public static class Builder<T extends Entity> {
        private final Identifier id;
        private EntityFactory<T> factory;
        private boolean shouldTick = true;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder<T> factory(EntityFactory<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> shouldTick(boolean shouldTick) {
            this.shouldTick = shouldTick;
            return this;
        }

        public EntityType<T> build() {
            if (factory == null)
                throw new IllegalStateException("EntityFactory must be set before building EntityType");

            return new EntityType<>(id, factory, shouldTick);
        }
    }
}
