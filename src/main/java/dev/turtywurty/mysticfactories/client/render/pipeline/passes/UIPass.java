package dev.turtywurty.mysticfactories.client.render.pipeline.passes;

import dev.turtywurty.mysticfactories.client.input.Mouse;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderContext;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderPass;
import dev.turtywurty.mysticfactories.client.shader.Shader;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.ui.HUDManager;
import dev.turtywurty.mysticfactories.client.ui.UIRenderer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

/**
 * UI pass that draws HUD/screens in screen space using a UI renderer.
 */
public record UIPass(Shader uiShader, UIRenderer uiRenderer) implements RenderPass {
    @Override
    public void render(RenderContext context) {
        if (this.uiShader == null)
            return;

        boolean depthWasEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean blendWasEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        uiShader.bind();
        uiShader.setUniform("uTexture", 0);
        float w = context.getWindow().getWidth();
        float h = context.getWindow().getHeight();
        var view = new Matrix4f().identity();
        var proj = new Matrix4f().ortho(0, w, h, 0, -1, 1);
        uiShader.setUniform("uView", view);
        uiShader.setUniform("uProjection", proj);

        double mouseX = Mouse.getX();
        double mouseY = Mouse.getY();
        var drawContext = new DrawContext(uiShader, uiRenderer, view, proj, mouseX, mouseY);
        HUDManager.render(drawContext);

        uiShader.unbind();
        if (blendWasEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (depthWasEnabled) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }

    @Override
    public void cleanup() {
        HUDManager.cleanup();
        this.uiRenderer.cleanup();
    }
}
