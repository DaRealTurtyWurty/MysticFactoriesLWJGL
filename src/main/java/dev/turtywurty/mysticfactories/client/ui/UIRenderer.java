package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.render.mesh.QuadMesh;
import dev.turtywurty.mysticfactories.client.shader.Shader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Minimal UI renderer that can draw simple textured or colored quads. Extend as needed for text/icons.
 */
public class UIRenderer {
    private final Shader shader;
    private final QuadMesh quadMesh = new QuadMesh();
    private final Matrix4f model = new Matrix4f();

    public UIRenderer(Shader shader) {
        this.shader = shader;
    }

    /**
     * Draws a unit quad transformed by the provided model matrix.
     * Texture binding or color uniforms should be set by the caller before invoking this.
     */
    public void drawQuad(Matrix4f transform) {
        this.shader.setUniform("uModel", transform);
        GL30.glBindVertexArray(this.quadMesh.getVao());
        GL11.glDrawElements(GL11.GL_TRIANGLES, this.quadMesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    public Matrix4f model() {
        return this.model.identity();
    }

    public void cleanup() {
        this.quadMesh.cleanup();
    }
}
