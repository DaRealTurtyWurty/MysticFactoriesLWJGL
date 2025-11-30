package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.util.Identifier;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Texture atlas for GUI assets. Loads every PNG under assets/&lt;modid&gt;/textures/gui (including subfolders)
 * and packs them vertically into a single texture.
 */
public record GUITextureAtlas(int textureId, int width, int height, Map<Identifier, UV> uvMap) {
    private static GUITextureAtlas INSTANCE;
    private static String BUILT_MODID;

    public static GUITextureAtlas instance() {
        if (INSTANCE == null)
            throw new IllegalStateException("GUI texture atlas has not been built yet.");

        return INSTANCE;
    }

    public static GUITextureAtlas buildDefault(String modid) {
        if (INSTANCE != null)
            return INSTANCE;

        INSTANCE = buildInternal(modid);
        BUILT_MODID = modid;
        return INSTANCE;
    }

    public static void clearInstance() {
        INSTANCE = null;
        BUILT_MODID = null;
    }

    public static GUITextureAtlas build(String modid) {
        if (INSTANCE != null)
            return INSTANCE;

        return buildInternal(modid);
    }

    private static GUITextureAtlas buildInternal(String modid) {
        Map<Identifier, ImageData> images = loadImages(modid);
        if (images.isEmpty())
            throw new IllegalStateException("No GUI textures found for modid '" + modid + "'");

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
            STBImage.stbi_image_free(image.pixels);
        }

        atlasBuffer.flip();
        int textureId = uploadAtlasTexture(atlasBuffer, atlasWidth, atlasHeight);
        return new GUITextureAtlas(textureId, atlasWidth, atlasHeight, uvMap);
    }

    public UV getUv(Identifier id) {
        return this.uvMap.get(id);
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
        BUILT_MODID = null;
    }

    private static Map<Identifier, ImageData> loadImages(String modid) {
        String basePath = "assets/" + modid + "/textures/gui";
        URL url = GUITextureAtlas.class.getClassLoader().getResource(basePath);
        if (url == null)
            return Collections.emptyMap();

        try {
            URI uri = url.toURI();
            FileSystem fs = null;
            try {
                Path root;
                if ("jar".equals(uri.getScheme())) {
                    fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    root = fs.getPath(basePath);
                } else {
                    root = Path.of(uri);
                }

                Map<Identifier, ImageData> images = new LinkedHashMap<>();
                try (Stream<Path> stream = Files.walk(root)) {
                    stream.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".png"))
                            .sorted()
                            .forEach(path -> {
                                try {
                                    Identifier id = pathToIdentifier(modid, root, path);
                                    images.put(id, loadImage(path));
                                } catch (IOException exception) {
                                    throw new IllegalStateException("Failed to load GUI texture: " + path, exception);
                                }
                            });
                }

                return images;
            } finally {
                if (fs != null) {
                    fs.close();
                }
            }
        } catch (URISyntaxException | IOException exception) {
            throw new RuntimeException("Failed to load GUI textures from " + basePath, exception);
        }
    }

    private static Identifier pathToIdentifier(String modid, Path root, Path path) {
        Path relative = root.relativize(path);
        String normalized = relative.toString().replace('\\', '/');
        if (normalized.endsWith(".png")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }

        // Keep the path relative to the gui/ folder, e.g. gui/container/background
        return new Identifier(modid, "gui/" + normalized);
    }

    private static ImageData loadImage(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes).flip();

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer pixels = STBImage.stbi_load_from_memory(buffer, w, h, channels, 4);
        if (pixels == null)
            throw new IOException("Failed to decode image " + path + ": " + STBImage.stbi_failure_reason());

        return new ImageData(w.get(0), h.get(0), pixels);
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

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        float maxAniso = GL11.glGetFloat(GL46.GL_MAX_TEXTURE_MAX_ANISOTROPY);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAX_ANISOTROPY, Math.max(1.0f, maxAniso));

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        return textureId;
    }

    public record UV(float u0, float v0, float u1, float v1) {
    }

    private record ImageData(int width, int height, ByteBuffer pixels) {
    }
}
