package dev.turtywurty.mysticfactories.client.render.world.tile;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileRendererRegistry {
    private final Map<Identifier, TileRenderer> renderers = new HashMap<>();
    private final TileRenderer defaultRenderer;

    public TileRendererRegistry(TileRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    public void registerRenderer(Identifier id, TileRenderer renderer) {
        this.renderers.put(id, renderer);
    }

    public TileRenderer getRendererFor(TileType tileType) {
        return this.renderers.getOrDefault(tileType.getId(), this.defaultRenderer);
    }

    public void cleanup() {
        Set<TileRenderer> uniqueRenderers = new HashSet<>(this.renderers.values());
        uniqueRenderers.add(this.defaultRenderer);
        uniqueRenderers.forEach(TileRenderer::cleanup);
        this.renderers.clear();
    }
}
