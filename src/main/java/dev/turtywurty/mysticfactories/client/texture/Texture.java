package dev.turtywurty.mysticfactories.client.texture;

import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Getter
public class Texture {
    private final int id;
    private final int width;
    private final int height;

    public Texture(String resourcePath) {
        this.id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);

        // Pixel-art friendly sampling.
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        ByteBuffer imageBuffer = loadResourceToBuffer(resourcePath);
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer imageData = STBImage.stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelsBuffer, 4);
        if (imageData == null)
            throw new IllegalStateException("Failed to load texture '" + resourcePath + "': " + STBImage.stbi_failure_reason());

        this.width = widthBuffer.get(0);
        this.height = heightBuffer.get(0);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        float maxAniso = GL11.glGetFloat(GL46.GL_MAX_TEXTURE_MAX_ANISOTROPY);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAX_ANISOTROPY, Math.max(1.0f, maxAniso));

        STBImage.stbi_image_free(imageData);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private ByteBuffer loadResourceToBuffer(String resourcePath) {
        try (InputStream stream = Texture.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null)
                throw new IllegalArgumentException("Texture resource not found: " + resourcePath);

            byte[] bytes = stream.readAllBytes();
            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes).flip();
            return buffer;
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read texture resource: " + resourcePath, exception);
        }
    }

    public void bind(int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
    }

    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        GL11.glDeleteTextures(this.id);
    }
}
