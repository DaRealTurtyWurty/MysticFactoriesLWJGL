package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.shader.Shader;
import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.TextRenderer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Encapsulates the state needed to issue UI draw calls in screen space.
 */
public class DrawContext {
    private static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector2f DEFAULT_UV_MIN = new Vector2f(0f, 0f);
    private static final Vector2f DEFAULT_UV_MAX = new Vector2f(1f, 1f);

    private final Shader shader;
    private final UIRenderer renderer;
    private final TextRenderer textRenderer;
    private final Matrix4f view;
    private final Matrix4f projection;
    private final Matrix4f model = new Matrix4f();
    private final double mouseX;
    private final double mouseY;

    public DrawContext(Shader shader, UIRenderer renderer, TextRenderer textRenderer, Matrix4f view, Matrix4f projection, double mouseX, double mouseY) {
        this.shader = shader;
        this.renderer = renderer;
        this.textRenderer = textRenderer;
        this.view = new Matrix4f(view);
        this.projection = new Matrix4f(projection);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public Matrix4f projection() {
        return this.projection;
    }

    public Matrix4f view() {
        return this.view;
    }

    public double mouseX() {
        return this.mouseX;
    }

    public double mouseY() {
        return this.mouseY;
    }

    /**
     * Draw a rectangle using the shared quad mesh.
     *
     * @param x         top-left x in screen space
     * @param y         top-left y in screen space
     * @param width     width in pixels
     * @param height    height in pixels
     * @param color     RGBA multiplier (pass null for white)
     * @param uvMin     lower-left UV (pass null for 0,0)
     * @param uvMax     upper-right UV (pass null for 1,1)
     * @param textureId OpenGL texture id; if < 0, draws a solid color
     */
    public void drawRect(float x, float y, float width, float height, Vector4f color, Vector2f uvMin, Vector2f uvMax, int textureId) {
        Vector4f drawColor = color == null ? DEFAULT_COLOR : color;
        Vector2f drawUvMin = uvMin == null ? DEFAULT_UV_MIN : uvMin;
        Vector2f drawUvMax = uvMax == null ? DEFAULT_UV_MAX : uvMax;

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
     * Draw text using the provided font atlas at the given screen-space position.
     */
    public void drawText(FontAtlas font, String text, float x, float y, Vector4f color) {
        this.textRenderer.draw(font, text, x, y, this.view, this.projection, color == null ? DEFAULT_COLOR : color);
    }
}
