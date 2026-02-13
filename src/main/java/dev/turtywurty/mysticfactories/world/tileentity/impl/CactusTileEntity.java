package dev.turtywurty.mysticfactories.world.tileentity.impl;

import dev.turtywurty.mysticfactories.init.TileEntityTypes;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.DamageSource;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.entity.impl.LivingEntity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CactusTileEntity extends TileEntity {
    public CactusTileEntity(@NotNull EntityType<? extends TileEntity> type, @Nullable World world) {
        super(type, world);
    }

    public CactusTileEntity() {
        super(TileEntityTypes.CACTUS);
    }

    public CactusTileEntity(@Nullable World world, TilePos pos) {
        super(TileEntityTypes.CACTUS, world, pos);
    }

    public CactusTileEntity(@Nullable World world, int x, int y) {
        super(TileEntityTypes.CACTUS, world, x, y);
    }

    @Override
    public void onEntityCollision(Entity other) {
        if (other instanceof LivingEntity living) {
            living.damage(DamageSource.CACTUS, 1);
        }
    }
}
