package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import lombok.Setter;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Simple progress bar widget with optional centered label.
 */
public class ProgressBar extends Widget {
    @Setter
    private float progress;
    private final int backgroundColor;
    private final int fillColor;
    private final int borderColor;
    private final FontAtlas font;
    private final Supplier<String> labelSupplier;
    private final int labelColor;
    private final boolean showLabel;

    protected ProgressBar(float x, float y, float width, float height, float progress, int backgroundColor, int fillColor, int borderColor, FontAtlas font, Supplier<String> labelSupplier, int labelColor, boolean showLabel) {
        super(x, y, width, height);
        this.progress = progress;
        this.backgroundColor = backgroundColor;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.font = font;
        this.labelSupplier = labelSupplier;
        this.labelColor = labelColor;
        this.showLabel = showLabel;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void render(DrawContext context) {
        if (!isVisible())
            return;

        float barX = getX();
        float barY = getY();
        float barWidth = getWidth();
        float barHeight = getHeight();

        context.drawRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4, this.borderColor);
        context.drawRect(barX, barY, barWidth, barHeight, this.backgroundColor);

        float clamped = Math.max(0f, Math.min(1f, this.progress));
        context.drawRect(barX, barY, barWidth * clamped, barHeight, this.fillColor);

        if (this.showLabel && this.labelSupplier != null) {
            String label = this.labelSupplier.get();
            float textY = barY + (barHeight + this.font.getLineHeight()) * 0.5f - 2f;
            context.drawCenteredText(this.font, label, barX + barWidth * 0.5f, textY, this.labelColor);
        }
    }

    public static class Builder {
        private float x;
        private float y;
        private float width = 200f;
        private float height = 18f;
        private float progress;
        private int backgroundColor = 0xFF555555;
        private int fillColor = 0xFF6AC96A;
        private int borderColor = 0xFF111111;
        private FontAtlas font = Fonts.defaultFont();
        private Supplier<String> labelSupplier;
        private int labelColor = 0xFFFFFFFF;
        private boolean showLabel = true;

        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder progress(float progress) {
            this.progress = progress;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder fillColor(int fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public Builder borderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        public Builder label(Supplier<String> labelSupplier) {
            this.labelSupplier = Objects.requireNonNull(labelSupplier, "labelSupplier");
            return this;
        }

        public Builder label(String text) {
            this.labelSupplier = () -> text;
            return this;
        }

        public Builder labelColor(int labelColor) {
            this.labelColor = labelColor;
            return this;
        }

        public Builder showLabel(boolean showLabel) {
            this.showLabel = showLabel;
            return this;
        }

        public ProgressBar build() {
            return new ProgressBar(this.x, this.y, this.width, this.height, this.progress, this.backgroundColor, this.fillColor, this.borderColor, this.font, this.labelSupplier, this.labelColor, this.showLabel);
        }
    }
}
