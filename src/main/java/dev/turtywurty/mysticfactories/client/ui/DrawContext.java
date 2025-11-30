package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.shader.Shader;
import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.TextRenderer;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;
import dev.turtywurty.mysticfactories.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Encapsulates the state needed to issue UI draw calls in screen space.
 */
public class DrawContext {
    private final Shader shader;
    private final UIRenderer renderer;
    private final TextRenderer textRenderer;
    private final Matrix4f view;
    private final Matrix4f projection;
    private final float width;
    private final float height;
    private final double mouseX;
    private final double mouseY;

    /**
     * Creates a new draw context for the current UI frame.
     *
     * @param shader       bound UI shader that will receive uniforms
     * @param renderer     quad renderer used for rect draws
     * @param textRenderer text renderer for string output
     * @param view         view matrix in screen space
     * @param projection   projection matrix in screen space
     * @param width        current viewport width in pixels
     * @param height       current viewport height in pixels
     * @param mouseX       mouse x in screen space
     * @param mouseY       mouse y in screen space
     */
    public DrawContext(Shader shader, UIRenderer renderer, TextRenderer textRenderer, Matrix4f view, Matrix4f projection, float width, float height, double mouseX, double mouseY) {
        this.shader = shader;
        this.renderer = renderer;
        this.textRenderer = textRenderer;
        this.view = new Matrix4f(view);
        this.projection = new Matrix4f(projection);
        this.width = width;
        this.height = height;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    /**
     * @return projection matrix used for UI rendering.
     */
    public Matrix4f projection() {
        return this.projection;
    }

    /**
     * @return view matrix used for UI rendering.
     */
    public Matrix4f view() {
        return this.view;
    }

    /**
     * @return mouse X position in screen space.
     */
    public double mouseX() {
        return this.mouseX;
    }

    /**
     * @return mouse Y position in screen space.
     */
    public double mouseY() {
        return this.mouseY;
    }

    /**
     * @return current viewport width in pixels.
     */
    public float width() {
        return this.width;
    }

    /**
     * @return current viewport height in pixels.
     */
    public float height() {
        return this.height;
    }

    /**
     * @return shared GUI texture atlas for identifier-based UV lookups.
     */
    public GUITextureAtlas guiAtlas() {
        return GUITextureAtlas.instance();
    }

    /**
     * Draw a rectangle using the shared quad mesh.
     *
     * @param x         top-left x in screen space
     * @param y         top-left y in screen space
     * @param width     width in pixels
     * @param height    height in pixels
     * @param red       red color component (0.0 - 1.0)
     * @param green     green color component (0.0 - 1.0)
     * @param blue      blue color component (0.0 - 1.0)
     * @param alpha     alpha component (0.0 - 1.0)
     * @param u0        minimum U texture coordinate
     * @param v0        minimum V texture coordinate
     * @param u1        maximum U texture coordinate
     * @param v1        maximum V texture coordinate
     * @param textureId OpenGL texture id; if negative, no texture is sampled and a solid color is drawn
     */
    public void drawRect(float x, float y, float width, float height, float red, float green, float blue, float alpha, float u0, float v0, float u1, float v1, int textureId) {
        Vector4f drawColor = new Vector4f(red, green, blue, alpha);
        Vector2f drawUvMin = new Vector2f(u0, v0);
        Vector2f drawUvMax = new Vector2f(u1, v1);

        boolean useTexture = textureId >= 0;
        if (useTexture) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }

        this.shader.setUniform("uUseTexture", useTexture);
        this.shader.setUniform("uSampleAlphaOnly", false);
        this.shader.setUniform("uColor", drawColor);
        this.shader.setUniform("uUVMin", drawUvMin);
        this.shader.setUniform("uUVMax", drawUvMax);

        float centerX = x + width * 0.5f;
        float centerY = y + height * 0.5f;
        Matrix4f modelMat = this.renderer.model()
                .translation(centerX, centerY, 0f)
                .scale(width, height, 1f);
        this.renderer.drawQuad(modelMat);

        if (useTexture) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
    }

    /**
     * Draw a solid-color rectangle without sampling a texture.
     *
     * @param x      top-left x in screen space
     * @param y      top-left y in screen space
     * @param width  width in pixels
     * @param height height in pixels
     * @param red    red component (0.0 - 1.0)
     * @param green  green component (0.0 - 1.0)
     * @param blue   blue component (0.0 - 1.0)
     * @param alpha  alpha component (0.0 - 1.0)
     */
    public void drawRect(float x, float y, float width, float height, float red, float green, float blue, float alpha) {
        drawRect(x, y, width, height, red, green, blue, alpha, 0f, 0f, 1f, 1f, -1);
    }

    /**
     * Draw a solid-color rectangle using an ARGB int.
     *
     * @param x      top-left x in screen space
     * @param y      top-left y in screen space
     * @param width  width in pixels
     * @param height height in pixels
     * @param color  ARGB color (0xAARRGGBB)
     */
    public void drawRect(float x, float y, float width, float height, int color) {
        float red = ColorHelper.getRed(color);
        float green = ColorHelper.getGreen(color);
        float blue = ColorHelper.getBlue(color);
        float alpha = ColorHelper.getAlpha(color);
        drawRect(x, y, width, height, red, green, blue, alpha);
    }

    /**
     * Tile an atlas subtexture across the given area, repeating within its own UV range.
     *
     * @param x          top-left x in screen space
     * @param y          top-left y in screen space
     * @param width      area width in pixels
     * @param height     area height in pixels
     * @param tileWidth  tile width in pixels
     * @param tileHeight tile height in pixels
     * @param textureId  atlas identifier for the subtexture to tile
     */
    public void drawTiledTexture(float x, float y, float width, float height, float tileWidth, float tileHeight, Identifier textureId) {
        GUITextureAtlas.UV uv = GUITextureAtlas.instance().getUv(textureId);
        if (uv == null)
            return;

        float uRange = uv.u1() - uv.u0();
        float vRange = uv.v1() - uv.v0();
        int atlasTextureId = GUITextureAtlas.instance().textureId();

        float maxX = x + width;
        float maxY = y + height;

        for (float drawY = y; drawY < maxY; drawY += tileHeight) {
            float remainingY = maxY - drawY;
            float quadHeight = Math.min(tileHeight, remainingY);
            float v1 = uv.v0() + (quadHeight / tileHeight) * vRange;

            for (float drawX = x; drawX < maxX; drawX += tileWidth) {
                float remainingX = maxX - drawX;
                float quadWidth = Math.min(tileWidth, remainingX);
                float u1 = uv.u0() + (quadWidth / tileWidth) * uRange;

                drawRect(drawX, drawY, quadWidth, quadHeight, 1f, 1f, 1f, 1f, uv.u0(), uv.v0(), u1, v1, atlasTextureId);
            }
        }
    }

    /**
     * Draw a subtexture from the GUI atlas using identifier-based UVs.
     *
     * @param x         top-left x in screen space
     * @param y         top-left y in screen space
     * @param width     width in pixels
     * @param height    height in pixels
     * @param red       red component (0.0 - 1.0)
     * @param green     green component (0.0 - 1.0)
     * @param blue      blue component (0.0 - 1.0)
     * @param alpha     alpha component (0.0 - 1.0)
     * @param textureId atlas identifier for the subtexture
     */
    public void drawTexture(float x, float y, float width, float height, float red, float green, float blue, float alpha, Identifier textureId) {
        GUITextureAtlas.UV uv = GUITextureAtlas.instance().getUv(textureId);
        if (uv == null)
            return;

        float u0 = uv.u0();
        float v0 = uv.v0();
        float u1 = uv.u1();
        float v1 = uv.v1();
        drawRect(x, y, width, height, red, green, blue, alpha, u0, v0, u1, v1, GUITextureAtlas.instance().textureId());
    }

    /**
     * Draw a subtexture from the GUI atlas using identifier-based UVs with an ARGB color.
     *
     * @param x         top-left x in screen space
     * @param y         top-left y in screen space
     * @param width     width in pixels
     * @param height    height in pixels
     * @param color     ARGB color (0xAARRGGBB)
     * @param textureId atlas identifier for the subtexture
     */
    public void drawTexture(float x, float y, float width, float height, int color, Identifier textureId) {
        float red = ColorHelper.getRed(color);
        float green = ColorHelper.getGreen(color);
        float blue = ColorHelper.getBlue(color);
        float alpha = ColorHelper.getAlpha(color);
        drawTexture(x, y, width, height, red, green, blue, alpha, textureId);
    }

    /**
     * Draw text at the specified screen position.
     *
     * @param font  font atlas to use
     * @param text  string to render
     * @param x     top-left x in screen space
     * @param y     baseline y in screen space
     * @param red   red component (0.0 - 1.0)
     * @param green green component (0.0 - 1.0)
     * @param blue  blue component (0.0 - 1.0)
     * @param alpha alpha component (0.0 - 1.0)
     */
    public void drawText(FontAtlas font, String text, float x, float y, float red, float green, float blue, float alpha) {
        this.textRenderer.draw(font, text, x, y, this.view, this.projection, red, green, blue, alpha);
    }

    /**
     * Draw text with an ARGB int color.
     *
     * @param font  font atlas to use
     * @param text  string to render
     * @param x     top-left x in screen space
     * @param y     baseline y in screen space
     * @param color ARGB color (0xAARRGGBB)
     */
    public void drawText(FontAtlas font, String text, float x, float y, int color) {
        float red = ColorHelper.getRed(color);
        float green = ColorHelper.getGreen(color);
        float blue = ColorHelper.getBlue(color);
        float alpha = ColorHelper.getAlpha(color);
        drawText(font, text, x, y, red, green, blue, alpha);
    }

    /**
     * Draw white text at the specified screen position.
     *
     * @param font font atlas to use
     * @param text string to render
     * @param x    top-left x in screen space
     * @param y    baseline y in screen space
     */
    public void drawText(FontAtlas font, String text, float x, float y) {
        drawText(font, text, x, y, 0xFFFFFFFF);
    }

    /**
     * Draw a solid line between two points using a thin quad.
     *
     * @param x0        start x in screen space
     * @param y0        start y in screen space
     * @param x1        end x in screen space
     * @param y1        end y in screen space
     * @param thickness line thickness in pixels
     * @param color     ARGB color (0xAARRGGBB)
     */
    public void drawLine(float x0, float y0, float x1, float y1, float thickness, int color) {
        float red = ColorHelper.getRed(color);
        float green = ColorHelper.getGreen(color);
        float blue = ColorHelper.getBlue(color);
        float alpha = ColorHelper.getAlpha(color);
        drawLine(x0, y0, x1, y1, thickness, red, green, blue, alpha);
    }

    /**
     * Draw a solid line between two points using a thin quad.
     *
     * @param x0        start x in screen space
     * @param y0        start y in screen space
     * @param x1        end x in screen space
     * @param y1        end y in screen space
     * @param thickness line thickness in pixels
     * @param red       red component (0.0 - 1.0)
     * @param green     green component (0.0 - 1.0)
     * @param blue      blue component (0.0 - 1.0)
     * @param alpha     alpha component (0.0 - 1.0)
     */
    public void drawLine(float x0, float y0, float x1, float y1, float thickness, float red, float green, float blue, float alpha) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float length = (float) Math.hypot(dx, dy);
        if (length <= 0.0f || thickness <= 0.0f)
            return;

        Vector4f drawColor = new Vector4f(red, green, blue, alpha);
        this.shader.setUniform("uUseTexture", false);
        this.shader.setUniform("uSampleAlphaOnly", false);
        this.shader.setUniform("uColor", drawColor);
        this.shader.setUniform("uUVMin", new Vector2f(0f, 0f));
        this.shader.setUniform("uUVMax", new Vector2f(1f, 1f));

        float centerX = (x0 + x1) * 0.5f;
        float centerY = (y0 + y1) * 0.5f;
        float angle = (float) Math.atan2(dy, dx);

        Matrix4f modelMat = this.renderer.model()
                .translation(centerX, centerY, 0f)
                .rotateZ(angle)
                .scale(length, thickness, 1f);
        this.renderer.drawQuad(modelMat);
    }
}
