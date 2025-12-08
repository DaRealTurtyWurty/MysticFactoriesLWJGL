package dev.turtywurty.mysticfactories.world.entity;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import dev.turtywurty.mysticfactories.world.World;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public final class EntityType<T extends Entity> implements Registerable {
    @Setter
    private Identifier id;
    private final EntityFactory<T> factory;
    private final boolean shouldTick;

    public EntityType(EntityFactory<T> factory, boolean shouldTick) {
        this.factory = factory;
        this.shouldTick = shouldTick;
    }

    public static <T extends Entity> Builder<T> builder() {
        return new Builder<>();
    }

    public T create(World world) {
        return factory.create(this, world);
    }

    @FunctionalInterface
    public interface EntityFactory<T extends Entity> {
        T create(EntityType<T> type, World world);
    }

    public static class Builder<T extends Entity> {
        private EntityFactory<T> factory;
        private boolean shouldTick = true;

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

            return new EntityType<>(factory, shouldTick);
        }
    }
}
