package dev.turtywurty.mysticfactories.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private final int textureId;
    private int width, height;

    public Texture(String filePath) {
        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        // Set texture parameters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            ByteBuffer image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 0);
            if (image == null)
                throw new RuntimeException("Failed to load texture file: " + filePath + ", " + stbi_failure_reason());

            this.width = widthBuffer.get(0);
            this.height = heightBuffer.get(0);
            int channels = channelsBuffer.get(0);

            if (channels == 3) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.width, this.height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image);
            } else if (channels == 4) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
            } else
                throw new RuntimeException("Unknown number of channels: " + channels);

            stbi_image_free(image);
        }

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        GL11.glDeleteTextures(textureId);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
