package dev.turtywurty.mysticfactories.client.render;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderContext;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderPipeline;
import dev.turtywurty.mysticfactories.client.render.pipeline.passes.ClearPass;
import dev.turtywurty.mysticfactories.client.render.pipeline.passes.UIPass;
import dev.turtywurty.mysticfactories.client.render.pipeline.passes.WorldPass;
import dev.turtywurty.mysticfactories.client.render.world.WorldRenderer;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.shader.Shader;
import dev.turtywurty.mysticfactories.client.shader.ShaderManager;
import dev.turtywurty.mysticfactories.client.text.TextRenderer;
import dev.turtywurty.mysticfactories.client.ui.GUITextureAtlas;
import dev.turtywurty.mysticfactories.client.ui.UIRenderer;
import dev.turtywurty.mysticfactories.client.window.Window;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.util.Identifier;
import lombok.Getter;

public class GameRenderer {
    private final ShaderManager shaderManager;
    @Getter
    private final TileRenderContext tileRenderContext;
    @Getter
    private final EntityRenderContext entityRenderContext;
    private final RenderPipeline pipeline;
    private final RenderContext renderContext;
    @Getter
    private final Camera camera;
    private final GUITextureAtlas guiTextureAtlas;

    public GameRenderer(Camera camera, Window window) {
        this.camera = camera;
        this.shaderManager = new ShaderManager();
        Shader texturedShader = this.shaderManager.create("textured",
                "shaders/texture.vert",
                "shaders/texture.frag");
        Shader uiShader = this.shaderManager.create("ui",
                "shaders/texture.vert",
                "shaders/texture.frag");

        String modid = Identifier.of("gui_atlas_init").namespace();
        this.guiTextureAtlas = GUITextureAtlas.buildDefault(modid);

        this.tileRenderContext = new TileRenderContext(texturedShader, camera);
        this.entityRenderContext = new EntityRenderContext(texturedShader, camera);
        TextRenderer textRenderer = new TextRenderer(uiShader);

        this.renderContext = new RenderContext(camera, this.tileRenderContext, this.entityRenderContext, uiShader, window);
        this.pipeline = new RenderPipeline()
                .addPass(new ClearPass(0.1f, 0.1f, 0.1f, 1.0f))
                .addPass(new WorldPass())
                .addPass(new UIPass(uiShader, new UIRenderer(uiShader), textRenderer));
    }

    public void render(ClientWorld world, WorldRenderer worldRenderer) {
        this.renderContext.setWorld(world);
        this.renderContext.setWorldRenderer(worldRenderer);
        this.pipeline.render(this.renderContext);
    }

    public void cleanup() {
        this.shaderManager.cleanup();
        this.pipeline.cleanup();
        this.guiTextureAtlas.cleanup();
        GUITextureAtlas.clearInstance();
    }
}
