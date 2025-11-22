package dev.turtywurty.mysticfactories.client.render.pipeline.passes;

import dev.turtywurty.mysticfactories.client.render.pipeline.RenderContext;
import dev.turtywurty.mysticfactories.client.render.pipeline.RenderPass;
import org.lwjgl.opengl.GL11;

public record ClearPass(float r, float g, float b, float a) implements RenderPass {
    @Override
    public void render(RenderContext context) {
        GL11.glClearColor(r, g, b, a);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
}
