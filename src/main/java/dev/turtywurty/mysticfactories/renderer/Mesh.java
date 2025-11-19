package dev.turtywurty.mysticfactories.renderer;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private final int vertexCount;
    private final Texture texture;

    public Mesh(float[] positions, float[] texCoords, int[] indices, Texture texture) {
        this.texture = texture;
        this.vertexCount = indices.length;

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboId = GL15.glGenBuffers();
        FloatBuffer posTexBuffer = null;
        try {
            posTexBuffer = MemoryUtil.memAllocFloat(positions.length + texCoords.length);
            posTexBuffer.put(positions).put(texCoords).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posTexBuffer, GL15.GL_STATIC_DRAW);
        } finally {
            if (posTexBuffer != null) {
                MemoryUtil.memFree(posTexBuffer);
            }
        }

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, positions.length * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        eboId = GL15.glGenBuffers();
        IntBuffer indicesBuffer = null;
        try {
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        } finally {
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }

        GL30.glBindVertexArray(0);
    }

    public void render() {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        texture.bind();

        GL30.glBindVertexArray(vaoId);

        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
        texture.unbind();
    }

    public void cleanup() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(eboId);

        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);

        texture.cleanup();
    }
}
