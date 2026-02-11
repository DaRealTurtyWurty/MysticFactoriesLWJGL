package dev.turtywurty.mysticfactories.client.render.world.tile;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Packs tile textures into a single atlas texture and provides UV ranges for each tile.
 */
public record TileAtlas(int textureId, int width, int height, Map<Identifier, UV> uvMap) {
    public static TileAtlas build() {
        Map<Identifier, ImageData> images = loadImages();
        if (images.isEmpty())
            throw new IllegalStateException("No tile textures available to build atlas");

        int atlasWidth = images.values().stream().mapToInt(img -> img.width).max().orElse(1);
        int atlasHeight = images.values().stream().mapToInt(img -> img.height).sum();

        ByteBuffer atlasBuffer = BufferUtils.createByteBuffer(atlasWidth * atlasHeight * 4);
        Map<Identifier, UV> uvMap = new LinkedHashMap<>();

        int currentY = 0;
        for (Map.Entry<Identifier, ImageData> entry : images.entrySet()) {
            Identifier id = entry.getKey();
            ImageData image = entry.getValue();

            copyIntoAtlas(image, atlasBuffer, atlasWidth, currentY);
            float u0 = 0f;
            float v0 = (float) currentY / atlasHeight;
            float u1 = (float) image.width / atlasWidth;
            float v1 = (float) (currentY + image.height) / atlasHeight;
            uvMap.put(id, new UV(u0, v0, u1, v1));

            currentY += image.height;
            image.free();
        }

        atlasBuffer.flip();
        int textureId = uploadAtlasTexture(atlasBuffer, atlasWidth, atlasHeight);
        return new TileAtlas(textureId, atlasWidth, atlasHeight, uvMap);
    }

    private static Map<Identifier, ImageData> loadImages() {
        Map<Identifier, ImageData> images = new LinkedHashMap<>();
        for (Identifier id : Registries.TILE_TYPES.getIds()) {
            String path = "textures/tiles/" + id.path() + ".png";
            try {
                images.put(id, loadImage(path));
            } catch (IOException exception) {
                System.err.println("Failed to load tile texture for " + id + ": " + exception.getMessage());
                images.put(id, ImageData.MISSING);
            }
        }

        return images;
    }

    private static ImageData loadImage(String resourcePath) throws IOException {
        byte[] bytes;
        try (InputStream stream = TileAtlas.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null)
                throw new IOException("Resource not found: " + resourcePath);

            bytes = stream.readAllBytes();
        }

        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes).flip();

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer pixels = STBImage.stbi_load_from_memory(buffer, w, h, channels, 4);
        if (pixels == null)
            throw new IOException("Failed to decode image " + resourcePath + ": " + STBImage.stbi_failure_reason());

        return new ImageData(w.get(0), h.get(0), pixels, true);
    }

    private static void copyIntoAtlas(ImageData image, ByteBuffer atlas, int atlasWidth, int destY) {
        int rowSize = image.width * 4;
        for (int y = 0; y < image.height; y++) {
            int destOffset = ((destY + y) * atlasWidth * 4);
            int srcOffset = y * rowSize;
            for (int x = 0; x < rowSize; x++) {
                atlas.put(destOffset + x, image.pixels.get(srcOffset + x));
            }
        }
    }

    private static int uploadAtlasTexture(ByteBuffer data, int width, int height) {
        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return textureId;
    }

    public UV getUv(TileType tileType) {
        return this.uvMap.get(tileType.getId());
    }

    public void bind(int slot) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
    }

    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        GL11.glDeleteTextures(this.textureId);
    }

    public record UV(float u0, float v0, float u1, float v1) {
    }

    public record ImageData(int width, int height, ByteBuffer pixels, boolean stbAllocated) {
        public static final ImageData MISSING = createMissing(16, 16);

        public static ImageData createMissing(int width, int height) {
            // checkered magenta and black pattern for missing texture
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    boolean isMagenta = ((x / 8) + (y / 8)) % 2 == 0;
                    buffer.put(isMagenta ? (byte) 255 : (byte) 0); // R
                    buffer.put((byte) 0); // G
                    buffer.put(isMagenta ? (byte) 255 : (byte) 0); // B
                    buffer.put((byte) 255); // A
                }
            }

            buffer.flip();
            return new ImageData(width, height, buffer, false);
        }

        public void free() {
            if (this.stbAllocated) {
                STBImage.stbi_image_free(this.pixels);
            }
        }
    }
}
