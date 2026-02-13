package dev.turtywurty.mysticfactories.world.tileentity;

import dev.turtywurty.mysticfactories.world.entity.EntityType;
import lombok.Getter;

public class TileEntityType<T extends TileEntity> extends EntityType<T> {
    @Getter
    private final StackBehavior stackBehavior;

    public TileEntityType(EntityFactory<T> factory, boolean shouldTick, boolean immovable, StackBehavior stackBehavior) {
        super(factory, shouldTick, immovable);
        this.stackBehavior = stackBehavior;
    }

    public static <T extends TileEntity> Builder<T> tileBuilder() {
        return new Builder<>();
    }

    public static class Builder<T extends TileEntity> extends EntityType.Builder<T, Builder<T>> {
        private StackBehavior stackBehavior = StackBehavior.PER_LAYER;

        public Builder() {
            this.immovable = true;
        }

        public Builder<T> stackBehavior(StackBehavior stackBehavior) {
            this.stackBehavior = stackBehavior;
            return this;
        }

        public TileEntityType<T> build() {
            return new TileEntityType<>(factory, shouldTick, immovable, stackBehavior);
        }
    }
}
