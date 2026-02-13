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
public class EntityType<T extends Entity> implements Registerable {
    private final EntityFactory<T> factory;
    private final boolean shouldTick;
    private final boolean immovable;
    @Setter
    private Identifier id;

    public EntityType(EntityFactory<T> factory, boolean shouldTick, boolean immovable) {
        this.factory = factory;
        this.shouldTick = shouldTick;
        this.immovable = immovable;
    }

    public static <T extends Entity, B extends Builder<T, B>> Builder<T, B> builder(Class<T> entityClass) {
        return new Builder<>();
    }

    public T create(World world) {
        return factory.create(this, world);
    }

    @FunctionalInterface
    public interface EntityFactory<T extends Entity> {
        T create(EntityType<T> type, World world);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T extends Entity, B extends Builder<T, B>> {
        protected EntityFactory<T> factory;
        protected boolean shouldTick = true;
        protected boolean immovable = false;

        public B factory(EntityFactory<T> factory) {
            this.factory = factory;
            return (B) this;
        }

        public B shouldTick(boolean shouldTick) {
            this.shouldTick = shouldTick;
            return (B) this;
        }

        public B immovable(boolean immovable) {
            this.immovable = immovable;
            return (B) this;
        }

        public EntityType<T> build() {
            if (factory == null)
                throw new IllegalStateException("EntityFactory must be set before building EntityType");

            return new EntityType<>(factory, shouldTick, immovable);
        }
    }
}
