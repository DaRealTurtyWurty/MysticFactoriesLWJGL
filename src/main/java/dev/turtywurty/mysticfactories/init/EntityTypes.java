package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.entity.impl.PlayerEntity;

import java.util.function.Function;

public class EntityTypes {
    public static final EntityType<PlayerEntity> PLAYER = register("player", builder ->
            builder.factory((type, world) -> new PlayerEntity(world))
                    .shouldTick(true)
                    .build());

    private EntityTypes() {
    }

    public static void init() {
    }

    public static <T extends Entity> EntityType<T> register(String name, Function<EntityType.Builder<T>, EntityType<T>> supplier) {
        Identifier id = Identifier.of(name);
        return Registries.ENTITY_TYPES.register(id, supplier.apply(new EntityType.Builder<>(id)));
    }
}
