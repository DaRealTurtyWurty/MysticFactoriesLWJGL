package dev.turtywurty.mysticfactories.client.render.world.entity;

import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityRendererRegistry {
    private final Map<EntityType<?>, EntityRenderer<?>> renderers = new HashMap<>();

    public <T extends Entity> void registerRenderer(EntityType<T> type, EntityRenderer<T> renderer) {
        this.renderers.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> EntityRenderer<T> getRendererFor(EntityType<T> entityType) {
        return (EntityRenderer<T>) this.renderers.get(entityType);
    }

    public void cleanup() {
        Set<EntityRenderer<?>> uniqueRenderers = new HashSet<>(this.renderers.values());
        uniqueRenderers.forEach(EntityRenderer::cleanup);
        this.renderers.clear();
    }
}
