package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldTypeRegistry {
    private final Map<Identifier, WorldType> types = new HashMap<>();

    public WorldType register(WorldType type) {
        Identifier id = type.getId();
        if (this.types.containsKey(id)) {
            throw new IllegalArgumentException("WorldType already registered: " + id);
        }

        this.types.put(id, type);
        return type;
    }

    public Optional<WorldType> get(Identifier id) {
        return Optional.ofNullable(this.types.get(id));
    }

    public Map<Identifier, WorldType> getAll() {
        return this.types;
    }

    public void cleanup() {
        this.types.clear();
    }
}
