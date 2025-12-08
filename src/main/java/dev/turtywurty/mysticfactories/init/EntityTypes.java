package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.entity.impl.PlayerEntity;

@RegistryHolder
public class EntityTypes {
    public static final EntityType<PlayerEntity> PLAYER = register("player",
            EntityType.<PlayerEntity>builder()
                    .factory((type, world) -> new PlayerEntity(world))
                    .shouldTick(true)
                    .build());

    private EntityTypes() {
    }

    public static <T extends Entity> EntityType<T> register(String name, EntityType<T> entityType) {
        Identifier id = Identifier.of(name);
        return Registries.ENTITY_TYPES.register(id, entityType);
    }
}
