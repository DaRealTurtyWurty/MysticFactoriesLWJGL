package dev.turtywurty.mysticfactories.client.render.world;

import dev.turtywurty.mysticfactories.world.WorldType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldRendererRegistry {
    private final Map<WorldType, WorldRendererBase> renderers = new HashMap<>();
    private final WorldRendererBase defaultRenderer;

    public WorldRendererRegistry(WorldRendererBase defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    public void registerRenderer(WorldType worldType, WorldRendererBase renderer) {
        this.renderers.put(worldType, renderer);
    }

    public WorldRendererBase getRendererFor(WorldType worldType) {
        return this.renderers.getOrDefault(worldType, this.defaultRenderer);
    }

    public void cleanup() {
        Set<WorldRendererBase> unique = new HashSet<>(this.renderers.values());
        unique.add(this.defaultRenderer);
        unique.forEach(WorldRendererBase::cleanup);
        this.renderers.clear();
    }
}
