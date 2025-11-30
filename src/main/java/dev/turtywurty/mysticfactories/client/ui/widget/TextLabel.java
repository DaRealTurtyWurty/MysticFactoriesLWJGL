package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Basic HUD text label that pulls its text from a supplier every frame.
 */
public class TextLabel extends Widget {
    private final FontAtlas font;
    private final Supplier<String> textSupplier;
    private final int color;

    protected TextLabel(FontAtlas font, Supplier<String> textSupplier, float x, float y, int color) {
        super(x, y, 0f, 0f);
        this.font = Objects.requireNonNull(font, "font");
        this.textSupplier = Objects.requireNonNull(textSupplier, "textSupplier");
        this.color = color;
    }

    /**
     * @return builder for a configurable text label.
     */
    public static TextLabel.Builder builder() {
        return new TextLabel.Builder();
    }

    @Override
    public void render(DrawContext context) {
        context.drawText(this.font, this.textSupplier.get(), getX(), getY(), this.color);
    }

    public static class Builder {
        private FontAtlas font = Fonts.defaultFont();
        private Supplier<String> textSupplier;
        private float x;
        private float y;
        private int color = 0xFFFFFFFF;

        /**
         * Font used to render the label.
         */
        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        /**
         * Supplier providing text each frame.
         */
        public Builder textSupplier(Supplier<String> textSupplier) {
            this.textSupplier = textSupplier;
            return this;
        }

        /**
         * Static text.
         */
        public Builder text(String text) {
            this.textSupplier = () -> text;
            return this;
        }

        /**
         * Number to be converted to string each frame.
         */
        public Builder text(Number number) {
            this.textSupplier = number::toString;
            return this;
        }

        /**
         * Position in screen space.
         */
        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Left position in screen space.
         */
        public Builder x(float x) {
            this.x = x;
            return this;
        }

        /**
         * Top position in screen space.
         */
        public Builder y(float y) {
            this.y = y;
            return this;
        }

        /**
         * ARGB color (0xAARRGGBB).
         */
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public TextLabel build() {
            if (this.font == null)
                throw new IllegalStateException("Font must be set on TextLabel builder");

            var label = new TextLabel(this.font, this.textSupplier, this.x, this.y, this.color);
            String sample = this.textSupplier != null ? this.textSupplier.get() : "";
            float width = this.font.measureTextWidth(sample);
            float height = this.font.getLineHeight();
            label.setSize(width, height);
            return label;
        }
    }
}
