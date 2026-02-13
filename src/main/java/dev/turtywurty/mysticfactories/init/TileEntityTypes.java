package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.tileentity.StackBehavior;
import dev.turtywurty.mysticfactories.world.tileentity.StackedTileEntity;
import dev.turtywurty.mysticfactories.world.tileentity.TileEntity;
import dev.turtywurty.mysticfactories.world.tileentity.TileEntityType;
import dev.turtywurty.mysticfactories.world.tileentity.impl.CactusTileEntity;

@RegistryHolder
public class TileEntityTypes {
    public static final TileEntityType<StackedTileEntity> STACKED = register("stacked_tile",
            TileEntityType.<StackedTileEntity>tileBuilder()
                    .factory(StackedTileEntity::new)
                    .shouldTick(true)
                    .build());

    public static final TileEntityType<CactusTileEntity> CACTUS = register("cactus",
            TileEntityType.<CactusTileEntity>tileBuilder()
                    .factory(CactusTileEntity::new)
                    .shouldTick(true)
                    .stackBehavior(StackBehavior.AGGREGATE)
                    .build());

    private TileEntityTypes() {
    }

    public static <T extends TileEntity> TileEntityType<T> register(String name, TileEntityType<T> entityType) {
        return Registries.ENTITY_TYPES.register(Identifier.of(name), entityType);
    }
}
