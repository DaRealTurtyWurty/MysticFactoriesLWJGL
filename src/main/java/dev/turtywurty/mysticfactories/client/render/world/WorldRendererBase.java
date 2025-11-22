package dev.turtywurty.mysticfactories.client.render.world;

import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.world.WorldView;

public interface WorldRendererBase {
    void render(ClientWorld world, TileRenderContext tileContext, EntityRenderContext entityContext);

    default void cleanup() {
        // optional cleanup
    }
}
