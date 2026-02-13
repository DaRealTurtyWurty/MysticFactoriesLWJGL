package dev.turtywurty.mysticfactories.world.tileentity;

import dev.turtywurty.mysticfactories.util.AABB;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntity extends Entity {
    private TileType tileType;

    public TileEntity(@NotNull EntityType<? extends TileEntity> type, @Nullable World world) {
        super(type, world);
    }

    public TileEntity(@NotNull EntityType<? extends TileEntity> type, @Nullable World world, TilePos pos) {
        this(type, world, pos.x, pos.y);
    }

    public TileEntity(@NotNull EntityType<? extends TileEntity> type, @Nullable World world, int x, int y) {
        super(type, world);
        setPosition(x, y);
    }

    public TileEntity(@NotNull EntityType<? extends TileEntity> type) {
        super(type);
    }

    @Override
    public final void baseTick(double delta) {
        super.baseTick(delta);
    }

    @Override
    public void setPosition(double x, double y) {
        super.setPosition(Math.floor(x), Math.floor(y));
    }

    @Override
    public void tick(double delta) {}

    @Override
    public AABB getAABB() {
        return new AABB(getPosition().x, getPosition().y, getPosition().x + 1, getPosition().y + 1);
    }

    @Override
    public void setWorld(@Nullable World world) {
        super.setWorld(world);
        if (getWorld() != null && this.tileType == null) {
            this.tileType = getWorld().getTile(TilePos.fromVector2d(getPosition())).orElse(null);
        }
    }

    public TileType getTileType() {
        if (tileType == null && getWorld() != null) {
            tileType = getWorld().getTile(TilePos.fromVector2d(getPosition())).orElse(null);
        }

        return tileType;
    }
}
