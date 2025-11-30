package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Clickable toggle button with on/off state and optional text.
 */
public class ToggleButton extends AbstractButton {
    private final Consumer<ToggleButton> onToggle;

    private FontAtlas font;
    private String onText = "On";
    private String offText = "Off";
    private int textColor = 0xFFFFFFFF;

    private int offColor = 0xFF2F2F2F;
    private int offHoverColor = 0xFF3A3A3A;
    private int offPressedColor = 0xFF232323;

    private int onColor = 0xFF2E7D32;
    private int onHoverColor = 0xFF388E3C;
    private int onPressedColor = 0xFF1B5E20;

    private boolean toggled;

    public ToggleButton(float x, float y, float width, float height, Consumer<ToggleButton> onToggle) {
        super(x, y, width, height);
        this.onToggle = Objects.requireNonNull(onToggle, "onToggle");
    }

    public ToggleButton setFont(FontAtlas font) {
        this.font = font;
        return this;
    }

    public ToggleButton setText(String onText, String offText) {
        this.onText = Objects.requireNonNull(onText, "onText");
        this.offText = Objects.requireNonNull(offText, "offText");
        return this;
    }

    public ToggleButton setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public ToggleButton setOn(boolean toggled) {
        this.toggled = toggled;
        return this;
    }

    public boolean isOn() {
        return this.toggled;
    }

    public ToggleButton setOffColors(int color, int hoverColor, int pressedColor) {
        this.offColor = color;
        this.offHoverColor = hoverColor;
        this.offPressedColor = pressedColor;
        return this;
    }

    public ToggleButton setOnColors(int color, int hoverColor, int pressedColor) {
        this.onColor = color;
        this.onHoverColor = hoverColor;
        this.onPressedColor = pressedColor;
        return this;
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
            float textX = getX() + 6f;
            float textY = getY() + getHeight() * 0.6f;
            context.drawText(this.font, label, textX, textY, this.textColor);
        }
    }

    @Override
    protected void onClick() {
        this.toggled = !this.toggled;
        this.onToggle.accept(this);
    }
}
