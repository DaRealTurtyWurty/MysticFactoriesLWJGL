package dev.turtywurty.mysticfactories.client.render.pipeline;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.render.world.WorldRenderer;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.shader.Shader;
import dev.turtywurty.mysticfactories.client.window.Window;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RenderContext {
    private final Camera camera;
    private final TileRenderContext tileRenderContext;
    private final EntityRenderContext entityRenderContext;
    private final Shader uiShader;
    private final Window window;
    @Setter
    private ClientWorld world;
    @Setter
    private WorldRenderer worldRenderer;

    public RenderContext(Camera camera, TileRenderContext tileRenderContext, EntityRenderContext entityRenderContext, Shader uiShader, Window window) {
        this.camera = camera;
        this.tileRenderContext = tileRenderContext;
        this.entityRenderContext = entityRenderContext;
        this.uiShader = uiShader;
        this.window = window;
    }
}
