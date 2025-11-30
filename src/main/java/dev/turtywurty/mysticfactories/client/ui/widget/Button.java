package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple clickable button widget.
 */
public class Button extends AbstractButton {
    private final FontAtlas font;
    private final String text;
    private final Consumer<Button> onClick;

    private int backgroundColor = 0xFF2F2F2F;
    private int hoverColor = 0xFF3A3A3A;
    private int pressedColor = 0xFF232323;
    private int textColor = 0xFFFFFFFF;

    protected Button(float x, float y, float width, float height, FontAtlas font, String text, Consumer<Button> onClick) {
        super(x, y, width, height);
        this.font = Objects.requireNonNull(font, "font");
        this.text = Objects.requireNonNull(text, "text");
        this.onClick = Objects.requireNonNull(onClick, "onClick");
    }

    /**
     * @return builder for a configurable button instance.
     */
    public static Button.Builder builder() {
        return new Button.Builder();
    }

    /**
     * Sets the base background color.
     */
    public Button setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    /**
     * Sets the hover background color.
     */
    public Button setHoverColor(int color) {
        this.hoverColor = color;
        return this;
    }

    /**
     * Sets the pressed background color.
     */
    public Button setPressedColor(int color) {
        this.pressedColor = color;
        return this;
    }

    /**
     * Sets the text color.
     */
    public Button setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    @Override
    protected void renderButton(DrawContext context, boolean hovered, boolean pressed) {
        int fill = pressed ? this.pressedColor : hovered ? this.hoverColor : this.backgroundColor;
        if (isDisabled()) {
            fill = ColorHelper.blendColors(fill, 0xFF000000, 0.35f);
        }

        context.drawRect(getX(), getY(), getWidth(), getHeight(), fill);

        float textWidth = this.font.measureTextWidth(this.text);
        float textX = getX() + (getWidth() - textWidth) * 0.5f;
        float textY = getY() + (getHeight() - this.font.getLineHeight()) * 0.5f;
        int drawColor = isDisabled() ? ColorHelper.withAlpha(this.textColor, 0.6f) : this.textColor;
        context.drawText(this.font, this.text, textX, textY, drawColor);
    }

    @Override
    protected void onClick() {
        this.onClick.accept(this);
    }

    public static class Builder {
        private float x, y, width, height;
        private FontAtlas font = Fonts.defaultFont();
        private String text;
        private Consumer<Button> onClick;

        /**
         * Position of the top-left corner.
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
         * Font used to render the label.
         */
        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        /**
         * Label text to display.
         */
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        /**
         * Click callback invoked on release within bounds.
         */
        public Builder onClick(Consumer<Button> onClick) {
            this.onClick = onClick;
            return this;
        }

        public Button build() {
            return new Button(x, y, width, height, font, text, onClick);
        }
    }
}
