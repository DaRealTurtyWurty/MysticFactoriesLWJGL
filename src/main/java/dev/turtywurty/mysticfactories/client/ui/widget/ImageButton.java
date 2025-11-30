package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;
import dev.turtywurty.mysticfactories.util.Identifier;
import lombok.Setter;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Clickable button that renders a subtexture from the GUI atlas, optionally with text overlay.
 */
public class ImageButton extends AbstractButton {
    private final Identifier textureId;
    private final Consumer<ImageButton> onClick;

    @Setter
    private int tintColor = 0xFFFFFFFF;
    @Setter
    private int hoverTint = 0xFFFFFFFF;
    @Setter
    private int pressedTint = 0xFFCCCCCC;
    @Setter
    private int textColor = 0xFFFFFFFF;
    private FontAtlas font;
    private String text;
    private float textPaddingX = 6f;
    private float textBaselineOffset = 0.6f; // relative to height

    /**
     * Renders a region from the GUI atlas.
     */
    protected ImageButton(float x, float y, float width, float height, Identifier textureId, Consumer<ImageButton> onClick) {
        super(x, y, width, height);
        this.textureId = textureId;
        this.onClick = Objects.requireNonNull(onClick, "onClick");
    }

    /**
     * @return builder for a configurable image button.
     */
    public static ImageButton.Builder builder() {
        return new ImageButton.Builder();
    }

    /**
     * Assigns overlay text to draw atop the image.
     */
    public void setText(FontAtlas font, String text) {
        this.font = Objects.requireNonNull(font, "font");
        this.text = Objects.requireNonNull(text, "text");
    }

    /**
     * Adjusts text padding from the left and baseline offset relative to height.
     */
    public void setTextPadding(float paddingX, float baselineOffset) {
        this.textPaddingX = paddingX;
        this.textBaselineOffset = baselineOffset;
    }

    @Override
    protected void renderButton(DrawContext context, boolean hovered, boolean pressed) {
        int tint = pressed ? this.pressedTint : hovered ? this.hoverTint : this.tintColor;
        float red = ColorHelper.getRed(tint);
        float green = ColorHelper.getGreen(tint);
        float blue = ColorHelper.getBlue(tint);
        float alpha = ColorHelper.getAlpha(tint);
        context.drawTexture(getX(), getY(), getWidth(), getHeight(), red, green, blue, alpha, this.textureId);

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

    public static class Builder {
        private float x, y, width, height;
        private Identifier textureId;
        private Consumer<ImageButton> onClick;
        private int tintColor = 0xFFFFFFFF;
        private int hoverTint = 0xFFFFFFFF;
        private int pressedTint = 0xFFCCCCCC;
        private int textColor = 0xFFFFFFFF;
        private FontAtlas font = Fonts.defaultFont();
        private String text;
        private float textPaddingX = 6f;
        private float textBaselineOffset = 0.6f; // relative to height

        /**
         * Top-left position in screen space.
         */
        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Size of the button in pixels.
         */
        public Builder size(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Uses a GUI atlas identifier; UVs are derived automatically.
         */
        public Builder texture(Identifier textureId) {
            this.textureId = textureId;
            return this;
        }

        /**
         * Click callback invoked on release within bounds.
         */
        public Builder onClick(Consumer<ImageButton> onClick) {
            this.onClick = onClick;
            return this;
        }

        /**
         * Base tint color applied to the image.
         */
        public Builder tintColor(int tintColor) {
            this.tintColor = tintColor;
            return this;
        }

        /**
         * Tint color when hovered.
         */
        public Builder hoverTint(int hoverTint) {
            this.hoverTint = hoverTint;
            return this;
        }

        /**
         * Tint color when pressed.
         */
        public Builder pressedTint(int pressedTint) {
            this.pressedTint = pressedTint;
            return this;
        }

        /**
         * Text color for the optional overlay label.
         */
        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * Font atlas for the optional overlay label.
         */
        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        /**
         * Text for the optional overlay label.
         */
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        /**
         * Padding from left and baseline offset for text placement.
         */
        public Builder textPadding(float paddingX, float baselineOffset) {
            this.textPaddingX = paddingX;
            this.textBaselineOffset = baselineOffset;
            return this;
        }

        public ImageButton build() {
            var button = new ImageButton(x, y, width, height, textureId, onClick);
            button.setTintColor(tintColor);
            button.setHoverTint(hoverTint);
            button.setPressedTint(pressedTint);
            button.setTextColor(textColor);
            if (font != null && text != null) {
                button.setText(font, text);
            }
            button.setTextPadding(textPaddingX, textBaselineOffset);
            return button;
        }
    }
}
