package dev.turtywurty.mysticfactories.world.tileentity;

import dev.turtywurty.mysticfactories.util.AABB;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StackedTileEntity extends TileEntity {
    private final List<TileEntity> entries = new ArrayList<>();

    public StackedTileEntity(@NotNull EntityType<? extends TileEntity> type, @Nullable World world) {
        super(type, world);
    }

    public StackedTileEntity(@NotNull EntityType<? extends TileEntity> type, @Nullable World world, TilePos pos) {
        super(type, world, pos);
    }

    public StackedTileEntity(@NotNull EntityType<? extends TileEntity> type, @Nullable World world, int x, int y) {
        super(type, world, x, y);
    }

    public void push(@NotNull TileEntity tileEntity) {
        if (tileEntity == this)
            throw new IllegalArgumentException("Cannot stack a StackedTileEntity inside itself");
        if (tileEntity instanceof StackedTileEntity)
            throw new IllegalArgumentException("Nested StackedTileEntity instances are not supported");

        this.entries.add(tileEntity);
        syncEntry(tileEntity);
    }

    public Optional<TileEntity> pop() {
        if (this.entries.isEmpty())
            return Optional.empty();

        return Optional.of(this.entries.removeLast());
    }

    public Optional<TileEntity> peek() {
        if (this.entries.isEmpty())
            return Optional.empty();

        return Optional.of(this.entries.getLast());
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public int size() {
        return this.entries.size();
    }

    public List<TileEntity> getEntries() {
        return Collections.unmodifiableList(this.entries);
    }

    public List<TileEntity> getCollisionEntries() {
        if (this.entries.isEmpty())
            return List.of();

        List<TileEntity> collisionEntries = new ArrayList<>();
        int lastIndex = this.entries.size() - 1;

        for (int index = 0; index < this.entries.size(); index++) {
            TileEntity entry = this.entries.get(index);
            StackBehavior behavior = getStackBehavior(entry);

            if (behavior == StackBehavior.TOP_ONLY && index != lastIndex)
                continue;
            if (behavior == StackBehavior.BOTTOM_ONLY && index != 0)
                continue;

            collisionEntries.add(entry);
        }

        return Collections.unmodifiableList(collisionEntries);
    }

    @Override
    public void setWorld(@Nullable World world) {
        super.setWorld(world);
        if (world == null)
            return;

        syncEntries();
    }

    @Override
    public void setPosition(double x, double y) {
        super.setPosition(x, y);
        syncEntries();
    }

    @Override
    public AABB getAABB() {
        return super.getAABB();
    }

    @Override
    public void tick(double delta) {
        super.tick(delta);
        for (TileEntity entry : this.entries) {
            entry.tick(delta);
        }
    }

    private void syncEntries() {
        for (TileEntity entry : this.entries) {
            syncEntry(entry);
        }
    }

    private void syncEntry(TileEntity entry) {
        World world = getWorld();
        if (world != null && entry.getWorld() != world) {
            entry.setWorld(world);
        }

        entry.setPosition(getPosition().x, getPosition().y);
    }

    private static StackBehavior getStackBehavior(TileEntity tileEntity) {
        if (tileEntity.getType() instanceof TileEntityType<?> tileEntityType)
            return tileEntityType.getStackBehavior();

        return StackBehavior.PER_LAYER;
    }
}
