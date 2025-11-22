package dev.turtywurty.mysticfactories.client.render.pipeline;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.render.world.WorldRendererBase;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RenderContext {
    private final Camera camera;
    private final TileRenderContext tileRenderContext;
    private final EntityRenderContext entityRenderContext;
    @Setter
    private ClientWorld world;
    @Setter
    private WorldRendererBase worldRenderer;

    public RenderContext(Camera camera, TileRenderContext tileRenderContext, EntityRenderContext entityRenderContext) {
        this.camera = camera;
        this.tileRenderContext = tileRenderContext;
        this.entityRenderContext = entityRenderContext;
    }
}
