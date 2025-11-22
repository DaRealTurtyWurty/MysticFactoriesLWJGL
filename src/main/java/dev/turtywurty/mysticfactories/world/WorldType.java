package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import lombok.Getter;

@Getter
public class WorldType {
    private final Identifier id;
    private final WorldGenerator generator;

    public WorldType(Identifier id, WorldGenerator generator) {
        if (id == null) {
            throw new IllegalArgumentException("WorldType id cannot be null");
        }
        this.id = id;
        this.generator = generator;
    }

}
