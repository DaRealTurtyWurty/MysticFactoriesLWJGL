package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Clickable button that renders a texture or sub-region of a texture atlas, optionally with text overlay.
 */
public class ImageButton extends AbstractButton {
    private final int textureId;
    private final float u0;
    private final float v0;
    private final float u1;
    private final float v1;
    private final Consumer<ImageButton> onClick;

    private int tintColor = 0xFFFFFFFF;
    private int hoverTint = 0xFFFFFFFF;
    private int pressedTint = 0xFFCCCCCC;
    private int textColor = 0xFFFFFFFF;
    private FontAtlas font;
    private String text;
    private float textPaddingX = 6f;
    private float textBaselineOffset = 0.6f; // relative to height

    /**
     * @param u0/u1/v0/v1 expected in normalized texture coordinates.
     */
    public ImageButton(float x, float y, float width, float height, int textureId, float u0, float v0, float u1, float v1, Consumer<ImageButton> onClick) {
        super(x, y, width, height);
        this.textureId = textureId;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
        this.onClick = Objects.requireNonNull(onClick, "onClick");
    }

    public ImageButton setTintColor(int color) {
        this.tintColor = color;
        return this;
    }

    public ImageButton setHoverTint(int color) {
        this.hoverTint = color;
        return this;
    }

    public ImageButton setPressedTint(int color) {
        this.pressedTint = color;
        return this;
    }

    public ImageButton setText(FontAtlas font, String text) {
        this.font = Objects.requireNonNull(font, "font");
        this.text = Objects.requireNonNull(text, "text");
        return this;
    }

    public ImageButton setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public ImageButton setTextPadding(float paddingX, float baselineOffset) {
        this.textPaddingX = paddingX;
        this.textBaselineOffset = baselineOffset;
        return this;
    }

    @Override
    protected void renderButton(DrawContext context, boolean hovered, boolean pressed) {
        int tint = pressed ? this.pressedTint : hovered ? this.hoverTint : this.tintColor;
        float red = ColorHelper.getRed(tint);
        float green = ColorHelper.getGreen(tint);
        float blue = ColorHelper.getBlue(tint);
        float alpha = ColorHelper.getAlpha(tint);
        context.drawRect(getX(), getY(), getWidth(), getHeight(), red, green, blue, alpha, this.u0, this.v0, this.u1, this.v1, this.textureId);

        if (this.font != null && this.text != null) {
            float textX = getX() + this.textPaddingX;
            float textY = getY() + getHeight() * this.textBaselineOffset;
            context.drawText(this.font, this.text, textX, textY, this.textColor);
        }
    }

    @Override
    protected void onClick() {
        this.onClick.accept(this);
    }
}
