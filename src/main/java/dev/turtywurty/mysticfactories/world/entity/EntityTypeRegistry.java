package dev.turtywurty.mysticfactories.world.entity;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityTypeRegistry {
    private final Map<Identifier, EntityType<?>> entities = new HashMap<>();

    public <T extends Entity> EntityType<T> register(EntityType<T> entityType) {
        Identifier id = entityType.id();
        if (this.entities.containsKey(id))
            throw new IllegalArgumentException("EntityType already registered: " + id);

        this.entities.put(id, entityType);
        return entityType;
    }

    public Optional<EntityType<?>> get(Identifier id) {
        return Optional.ofNullable(this.entities.get(id));
    }

    public Map<Identifier, EntityType<?>> getAll() {
        return this.entities;
    }

    public void cleanup() {
        this.entities.clear();
    }
}
