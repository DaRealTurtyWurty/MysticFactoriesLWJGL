package dev.turtywurty.mysticfactories.client.text;

import dev.turtywurty.mysticfactories.client.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Uploads text meshes to the GPU and draws them with the shared UI shader.
 */
public class TextRenderer {
    private static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);

    private final Shader shader;
    private final int vao;
    private final int vbo;
    private final Matrix4f identity = new Matrix4f();
    private int floatCapacity;

    public TextRenderer(Shader shader) {
        this.shader = shader;
        this.vao = GL30.glGenVertexArrays();
        this.vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(this.vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        allocateBuffer(6 * 4); // space for one character (6 vertices * 4 floats)

        int stride = 4 * Float.BYTES;
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 2L * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    /**
     * Draws text in screen space. Assumes the UI shader is already bound and the appropriate
     * blend state is configured by the caller.
     */
    public void draw(FontAtlas font, String text, float x, float y, Matrix4f view, Matrix4f projection, Vector4f color) {
        if (font == null || text == null || text.isEmpty())
            return;

        float[] vertices = TextMeshBuilder.buildTextMesh(font, text, x, y);
        if (vertices.length == 0)
            return;

        GL30.glBindVertexArray(this.vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        ensureCapacity(vertices.length);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureId());

        this.shader.setUniform("uModel", this.identity.identity());
        this.shader.setUniform("uView", view);
        this.shader.setUniform("uProjection", projection);
        this.shader.setUniform("uUseTexture", true);
        this.shader.setUniform("uSampleAlphaOnly", true);
        this.shader.setUniform("uColor", color == null ? DEFAULT_COLOR : color);
        this.shader.setUniform("uUVMin", new Vector2f(0f, 0f));
        this.shader.setUniform("uUVMax", new Vector2f(1f, 1f));
        this.shader.setUniform("uTexture", 0);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices.length / 4);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);

        // Reset so the next non-text draw does not inherit alpha-only sampling.
        this.shader.setUniform("uSampleAlphaOnly", false);
    }

    public void cleanup() {
        GL15.glDeleteBuffers(this.vbo);
        GL30.glDeleteVertexArrays(this.vao);
    }

    private void ensureCapacity(int floatCount) {
        if (floatCount <= this.floatCapacity)
            return;

        int newCapacity = Math.max(floatCount, this.floatCapacity * 2);
        allocateBuffer(newCapacity);
    }

    private void allocateBuffer(int floatCount) {
        this.floatCapacity = floatCount;
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (long) floatCount * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
    }
}
