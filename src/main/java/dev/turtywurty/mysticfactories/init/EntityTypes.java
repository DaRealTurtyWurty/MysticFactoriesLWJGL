package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.entity.EntityTypeRegistry;
import dev.turtywurty.mysticfactories.world.entity.impl.PlayerEntity;

public class EntityTypes {
    public static EntityType<PlayerEntity> PLAYER;

    private EntityTypes() {
    }

    public static void register(EntityTypeRegistry registry) {
        PLAYER = registry.register(new EntityType.Builder<PlayerEntity>(Identifier.of("player"))
                .factory((type, world) -> new PlayerEntity(world))
                .shouldTick(true)
                .build());
    }
}
