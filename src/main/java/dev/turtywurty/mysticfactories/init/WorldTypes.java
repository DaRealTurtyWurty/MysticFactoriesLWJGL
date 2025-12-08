package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.WorldType;

@RegistryHolder
public class WorldTypes {
    public static final WorldType OVERWORLD = register("overworld",
            WorldType.builder()
                    .generator(WorldGenerators.OVERWORLD)
                    .build());

    private WorldTypes() {
    }

    public static WorldType register(String name, WorldType worldType) {
        Identifier id = Identifier.of(name);
        return Registries.WORLD_TYPES.register(id, worldType);
    }
}
