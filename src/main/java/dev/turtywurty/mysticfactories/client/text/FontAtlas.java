package dev.turtywurty.mysticfactories.client.text;

import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Getter
public class FontAtlas {
    private static final int FIRST_CHAR = 32; // Space character
    public static final int CHAR_COUNT = 95; // From space (32) to tilde (126) - inclusive

    private final Glyph[] glyphs = new Glyph[CHAR_COUNT]; // Array of glyphs indexed by (codepoint - FIRST_CHAR)
    private final ByteBuffer fontData; // Keep the font buffer alive for stb
    private final int textureId; // OpenGL texture ID for the font atlas
    private final int atlasWidth; // Width of the font atlas texture
    private final int atlasHeight; // Height of the font atlas texture

    private final int fontSize; // Size of the font (in pixels)
    private final int ascent; // Positive value
    private final int descent; // Negative value
    private final int lineGap; // Space between lines
    private final float scale; // Scale factor for converting font units to pixels

    private final STBTTFontinfo fontInfo;

    public FontAtlas(String fontPath, int fontSize) {
        this.fontSize = fontSize;

        this.fontData = loadResourceToByteBuffer(fontPath);

        this.fontInfo = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(fontInfo, this.fontData))
            throw new IllegalStateException("Failed to initialize font information for " + fontPath);

        try (var stack = MemoryStack.stackPush()) {
            IntBuffer ascentBuf = stack.mallocInt(1);
            IntBuffer descentBuf = stack.mallocInt(1);
            IntBuffer lineGapBuf = stack.mallocInt(1);

            STBTruetype.stbtt_GetFontVMetrics(fontInfo, ascentBuf, descentBuf, lineGapBuf);
            this.scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, fontSize);
            this.ascent = (int) (ascentBuf.get(0) * scale);
            this.descent = (int) (descentBuf.get(0) * scale);
            this.lineGap = (int) (lineGapBuf.get(0) * scale);
        }

        int maxGlyphWidth = 0;
        int maxGlyphHeight = 0;

        try (var stack = MemoryStack.stackPush()) {
            IntBuffer x0 = stack.mallocInt(1);
            IntBuffer y0 = stack.mallocInt(1);
            IntBuffer x1 = stack.mallocInt(1);
            IntBuffer y1 = stack.mallocInt(1);

            for (int i = 0; i < CHAR_COUNT; i++) {
                int codepoint = FIRST_CHAR + i;
                STBTruetype.stbtt_GetCodepointBitmapBox(fontInfo, codepoint, scale, scale, x0, y0, x1, y1);

                int glyphWidth = x1.get(0) - x0.get(0);
                int glyphHeight = y1.get(0) - y0.get(0);

                if (glyphWidth > maxGlyphWidth) maxGlyphWidth = glyphWidth;
                if (glyphHeight > maxGlyphHeight) maxGlyphHeight = glyphHeight;
            }
        }

        int padding = 2;
        int cellWidth = maxGlyphWidth + padding * 2;
        int cellHeight = maxGlyphHeight + padding * 2;

        int cols = (int) Math.ceil(Math.sqrt(CHAR_COUNT));
        int rows = (int) Math.ceil((double) CHAR_COUNT / cols);

        this.atlasWidth = cellWidth * cols;
        this.atlasHeight = cellHeight * rows;

        ByteBuffer atlasBuffer = BufferUtils.createByteBuffer(atlasWidth * atlasHeight);

        try (var stack = MemoryStack.stackPush()) {
            IntBuffer advanceWidthBuf = stack.mallocInt(1);
            IntBuffer leftBearingBuf = stack.mallocInt(1);
            IntBuffer x0 = stack.mallocInt(1);
            IntBuffer y0 = stack.mallocInt(1);
            IntBuffer x1 = stack.mallocInt(1);
            IntBuffer y1 = stack.mallocInt(1);

            for (int i = 0; i < CHAR_COUNT; i++) {
                int codepoint = FIRST_CHAR + i;

                STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, codepoint, advanceWidthBuf, leftBearingBuf);
                float advance = advanceWidthBuf.get(0) * scale;

                STBTruetype.stbtt_GetCodepointBitmapBox(fontInfo, codepoint, scale, scale, x0, y0, x1, y1);
                int glyphWidth = x1.get(0) - x0.get(0);
                int glyphHeight = y1.get(0) - y0.get(0);

                int column = i % cols;
                int row = i / cols;
                int xOffset = column * cellWidth + padding;
                int yOffset = row * cellHeight + padding;

                int offset = yOffset * atlasWidth + xOffset;
                atlasBuffer.position(offset);

                STBTruetype.stbtt_MakeCodepointBitmap(fontInfo, atlasBuffer, glyphWidth, glyphHeight,
                        atlasWidth, scale, scale, codepoint);

                float bearingX = x0.get(0);
                float bearingY = -y0.get(0);

                float u0 = xOffset / (float) atlasWidth;
                float v0 = yOffset / (float) atlasHeight;
                float u1 = (xOffset + glyphWidth) / (float) atlasWidth;
                float v1 = (yOffset + glyphHeight) / (float) atlasHeight;
                glyphs[i] = new Glyph(codepoint, advance, bearingX, bearingY, glyphWidth, glyphHeight, u0, v0, u1, v1);
            }
        }

        atlasBuffer.position(0);

        this.textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_R8,
                atlasWidth, atlasHeight, 0,
                GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, atlasBuffer);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    }

    private static ByteBuffer loadResourceToByteBuffer(String resourcePath) {
        try (InputStream in = FontAtlas.class.getResourceAsStream(resourcePath)) {
            if (in == null)
                throw new IllegalArgumentException("Resource not found: " + resourcePath);

            byte[] data = in.readAllBytes();
            ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
            buffer.put(data);
            buffer.flip();
            return buffer;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load resource: " + resourcePath, exception);
        }
    }

    public Glyph getGlyph(char c) {
        int index = c - FIRST_CHAR;
        if (index < 0 || index >= CHAR_COUNT)
            throw new IllegalArgumentException("Character out of bounds: " + c);

        return glyphs[index];
    }

    public void cleanup() {
        GL11.glDeleteTextures(textureId);
        this.fontInfo.free();
    }

    public float getLineHeight() {
        return ascent - descent + lineGap;
    }

    /**
     * @param advance  Horizontal advance after rendering this glyph (in pixels)
     * @param bearingX Horizontal bearing (offset from cursor to left edge of glyph)
     * @param bearingY Vertical bearing (offset from baseline to top edge of glyph)
     * @param width    Width of the glyph (in pixels)
     * @param height   Height of the glyph (in pixels)
     * @param v1       Texture coordinates in the atlas
     */
    public record Glyph(int codepoint, float advance, float bearingX, float bearingY, float width, float height,
                        float u0, float v0, float u1, float v1) {
    }
}
