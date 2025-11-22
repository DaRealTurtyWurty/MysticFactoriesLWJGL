package dev.turtywurty.mysticfactories.client.render.pipeline.passes;

import dev.turtywurty.mysticfactories.client.render.pipeline.RenderContext;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderPass;
import dev.turtywurty.mysticfactories.client.render.world.WorldRenderer;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;

public class WorldPass implements RenderPass {
    @Override
    public void render(RenderContext context) {
        ClientWorld world = context.getWorld();
        WorldRenderer worldRenderer = context.getWorldRenderer();
        if (world == null || worldRenderer == null)
            return;

        TileRenderContext tileContext = context.getTileRenderContext();
        tileContext.shader().bind();
        tileContext.shader().setUniform("uView", context.getCamera().getViewMatrixPixelAligned());
        tileContext.shader().setUniform("uProjection", context.getCamera().getProjectionMatrix());

        EntityRenderContext entityContext = context.getEntityRenderContext();
        entityContext.shader().bind();
        entityContext.shader().setUniform("uView", context.getCamera().getViewMatrixPixelAligned());
        entityContext.shader().setUniform("uProjection", context.getCamera().getProjectionMatrix());

        worldRenderer.render(world, tileContext, entityContext);

        tileContext.shader().unbind();
    }
}
