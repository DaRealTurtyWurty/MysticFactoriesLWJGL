package dev.turtywurty.mysticfactories.client.text;

import org.lwjgl.stb.STBTruetype;

public class TextMeshBuilder {
    public static float[] buildTextMesh(FontAtlas font, String text, float startX, float startY) {
        float[] vertices = new float[text.length() * 6 * 4]; // 6 vertices per character, 4 components (x, y, u, v) each

        int offset = 0;

        float baselineY = startY + font.getAscent();
        float x = startX;
        float y = baselineY;

        FontAtlas.Glyph previousGlyph = null;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                x = startX;
                y += font.getLineHeight();
                previousGlyph = null;
                continue;
            }

            FontAtlas.Glyph glyph = font.getGlyph(c);
            if (glyph == null) {
                previousGlyph = null;
                continue; // Skip missing glyphs
            }

            if (previousGlyph != null) {
                int kernAdvance = STBTruetype.stbtt_GetCodepointKernAdvance(font.getFontInfo(), previousGlyph.codepoint(), glyph.codepoint());
                x += kernAdvance * font.getScale();
            }

            float x0 = x + glyph.bearingX();
            float y0 = y - glyph.bearingY();
            float x1 = x0 + glyph.width();
            float y1 = y0 + glyph.height();

            float u0 = glyph.u0();
            float v0 = glyph.v0();
            float u1 = glyph.u1();
            float v1 = glyph.v1();

            // First triangle
            // Vertex 1
            vertices[offset++] = x0;
            vertices[offset++] = y0;
            vertices[offset++] = u0;
            vertices[offset++] = v0;

            // Vertex 2
            vertices[offset++] = x1;
            vertices[offset++] = y0;
            vertices[offset++] = u1;
            vertices[offset++] = v0;

            // Vertex 3
            vertices[offset++] = x1;
            vertices[offset++] = y1;
            vertices[offset++] = u1;
            vertices[offset++] = v1;

            // Second triangle
            // Vertex 1
            vertices[offset++] = x0;
            vertices[offset++] = y0;
            vertices[offset++] = u0;
            vertices[offset++] = v0;

            // Vertex 2
            vertices[offset++] = x1;
            vertices[offset++] = y1;
            vertices[offset++] = u1;
            vertices[offset++] = v1;

            // Vertex 3
            vertices[offset++] = x0;
            vertices[offset++] = y1;
            vertices[offset++] = u0;
            vertices[offset++] = v1;

            x += glyph.advance();
            previousGlyph = glyph;
        }

        if (offset < vertices.length) {
            float[] trimmedVertices = new float[offset];
            System.arraycopy(vertices, 0, trimmedVertices, 0, offset);
            return trimmedVertices;
        }

        return vertices;
    }
}
