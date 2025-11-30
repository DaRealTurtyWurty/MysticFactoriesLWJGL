package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import lombok.Setter;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Clickable toggle button with on/off state and optional text.
 */
public class ToggleButton extends AbstractButton {
    private final Consumer<ToggleButton> onToggle;

    @Setter
    private FontAtlas font;
    private String onText = "On";
    private String offText = "Off";
    @Setter
    private int textColor = 0xFFFFFFFF;

    private int offColor = 0xFF2F2F2F;
    private int offHoverColor = 0xFF3A3A3A;
    private int offPressedColor = 0xFF232323;

    private int onColor = 0xFF2E7D32;
    private int onHoverColor = 0xFF388E3C;
    private int onPressedColor = 0xFF1B5E20;

    private boolean toggled;

    /**
     * @param x        left position in screen space
     * @param y        top position in screen space
     * @param width    width in pixels
     * @param height   height in pixels
     * @param onToggle callback invoked when toggled
     */
    protected ToggleButton(float x, float y, float width, float height, Consumer<ToggleButton> onToggle) {
        super(x, y, width, height);
        this.onToggle = Objects.requireNonNull(onToggle, "onToggle");
    }

    /**
     * @return builder for a configurable toggle button.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Sets the on/off label text.
     */
    public void setText(String onText, String offText) {
        this.onText = Objects.requireNonNull(onText, "onText");
        this.offText = Objects.requireNonNull(offText, "offText");
    }

    /**
     * Sets the current toggle state.
     */
    public void setOn(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isOn() {
        return this.toggled;
    }

    /**
     * Sets idle/hover/pressed colors when toggled off.
     */
    public void setOffColors(int color, int hoverColor, int pressedColor) {
        this.offColor = color;
        this.offHoverColor = hoverColor;
        this.offPressedColor = pressedColor;
    }

    /**
     * Sets idle/hover/pressed colors when toggled on.
     */
    public void setOnColors(int color, int hoverColor, int pressedColor) {
        this.onColor = color;
        this.onHoverColor = hoverColor;
        this.onPressedColor = pressedColor;
    }

    @Override
    protected void renderButton(DrawContext context, boolean hovered, boolean pressed) {
        int base = this.toggled ? this.onColor : this.offColor;
        int hover = this.toggled ? this.onHoverColor : this.offHoverColor;
        int press = this.toggled ? this.onPressedColor : this.offPressedColor;
        int fill = pressed ? press : hovered ? hover : base;
        context.drawRect(getX(), getY(), getWidth(), getHeight(), fill);

        if (this.font != null) {
            String label = this.toggled ? this.onText : this.offText;
            float textWidth = this.font.measureTextWidth(label);
            float textX = getX() + (getWidth() - textWidth) * 0.5f;
            float textY = getY() + (getHeight() - this.font.getLineHeight()) * 0.5f;
            context.drawText(this.font, label, textX, textY, this.textColor);
        }
    }

    @Override
    protected void onClick() {
        this.toggled = !this.toggled;
        this.onToggle.accept(this);
    }

    public static class Builder {
        private float x;
        private float y;
        private float width = 120f;
        private float height = 32f;
        private FontAtlas font = Fonts.defaultFont();
        private String onText = "On";
        private String offText = "Off";
        private int textColor = 0xFFFFFFFF;
        private int offColor = 0xFF2F2F2F;
        private int offHoverColor = 0xFF3A3A3A;
        private int offPressedColor = 0xFF232323;
        private int onColor = 0xFF2E7D32;
        private int onHoverColor = 0xFF388E3C;
        private int onPressedColor = 0xFF1B5E20;
        private boolean initialState;
        private Consumer<ToggleButton> onToggle = tb -> {
        };

        /**
         * Top-left position in screen space.
         */
        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Size in pixels.
         */
        public Builder size(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Font for the labels.
         */
        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        /**
         * Labels for on/off states.
         */
        public Builder text(String onText, String offText) {
            this.onText = Objects.requireNonNull(onText, "onText");
            this.offText = Objects.requireNonNull(offText, "offText");
            return this;
        }

        /**
         * Color for label text.
         */
        public Builder textColor(int color) {
            this.textColor = color;
            return this;
        }

        /**
         * Colors for the off state (idle/hover/pressed).
         */
        public Builder offColors(int color, int hoverColor, int pressedColor) {
            this.offColor = color;
            this.offHoverColor = hoverColor;
            this.offPressedColor = pressedColor;
            return this;
        }

        /**
         * Colors for the on state (idle/hover/pressed).
         */
        public Builder onColors(int color, int hoverColor, int pressedColor) {
            this.onColor = color;
            this.onHoverColor = hoverColor;
            this.onPressedColor = pressedColor;
            return this;
        }

        /**
         * Sets the initial toggle state.
         */
        public Builder initialState(boolean on) {
            this.initialState = on;
            return this;
        }

        /**
         * Callback invoked whenever the toggle flips.
         */
        public Builder onToggle(Consumer<ToggleButton> onToggle) {
            this.onToggle = Objects.requireNonNull(onToggle, "onToggle");
            return this;
        }

        public ToggleButton build() {
            var button = new ToggleButton(this.x, this.y, this.width, this.height, this.onToggle);
            button.setFont(this.font);
            button.setText(this.onText, this.offText);
            button.setTextColor(this.textColor);
            button.setOffColors(this.offColor, this.offHoverColor, this.offPressedColor);
            button.setOnColors(this.onColor, this.onHoverColor, this.onPressedColor);
            button.setOn(this.initialState);
            return button;
        }
    }
}
