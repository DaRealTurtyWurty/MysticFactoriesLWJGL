package dev.turtywurty.mysticfactories.client.render;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.render.mesh.QuadMesh;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderContext;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderPipeline;
import dev.turtywurty.mysticfactories.client.render.pipeline.passes.ClearPass;
import dev.turtywurty.mysticfactories.client.render.pipeline.passes.WorldPass;
import dev.turtywurty.mysticfactories.client.render.world.WorldRendererBase;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.shader.Shader;
import dev.turtywurty.mysticfactories.client.shader.ShaderManager;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import lombok.Getter;

public class GameRenderer {
    private final ShaderManager shaderManager;
    private final Shader texturedShader;
    private final QuadMesh quadMesh;
    @Getter
    private final TileRenderContext tileRenderContext;
    @Getter
    private final EntityRenderContext entityRenderContext;
    private final RenderPipeline pipeline;
    private final RenderContext renderContext;
    @Getter
    private final Camera camera;

    public GameRenderer(Camera camera) {
        this.camera = camera;
        this.shaderManager = new ShaderManager();
        this.texturedShader = this.shaderManager.create("textured", "shaders/texture.vert", "shaders/texture.frag");
        this.quadMesh = new QuadMesh();

        this.tileRenderContext = new TileRenderContext(this.texturedShader, camera, this.quadMesh.getVao(), this.quadMesh.getIndexCount());
        this.entityRenderContext = new EntityRenderContext(this.texturedShader, camera, this.quadMesh.getVao(), this.quadMesh.getIndexCount());

        this.renderContext = new RenderContext(camera, this.tileRenderContext, this.entityRenderContext);
        this.pipeline = new RenderPipeline()
                .addPass(new ClearPass(0.1f, 0.1f, 0.1f, 1.0f))
                .addPass(new WorldPass());
    }

    public void render(ClientWorld world, WorldRendererBase worldRenderer) {
        this.renderContext.setWorld(world);
        this.renderContext.setWorldRenderer(worldRenderer);
        this.pipeline.render(this.renderContext);
    }

    public void cleanup() {
        this.shaderManager.cleanup();
        this.quadMesh.cleanup();
        this.pipeline.cleanup();
    }
}
