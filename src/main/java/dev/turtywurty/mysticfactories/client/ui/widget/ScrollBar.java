package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple vertical scrollbar that renders a track and draggable knob.
 * Reports scroll fraction (0-1) to a listener when dragged.
 */
public class ScrollBar extends Widget {
    private final Consumer<Float> onScrollFractionChanged;
    private float fraction;
    private float knobHeight;
    private boolean dragging;
    private double lastMouseX;
    private double lastMouseY;
    private float dragOffset;
    @Setter
    private int trackColor = 0x66111111;
    @Setter
    private int knobColor = 0xCCAAAAAA;

    public ScrollBar(float x, float y, float width, float height, Consumer<Float> onScrollFractionChanged) {
        super(x, y, width, height);
        this.onScrollFractionChanged = Objects.requireNonNull(onScrollFractionChanged, "onScrollFractionChanged");
        this.knobHeight = height * 0.25f;
        this.fraction = 0f;
    }

    public void setFraction(float fraction) {
        this.fraction = clamp01(fraction);
    }

    public void setKnobHeight(float knobHeight) {
        this.knobHeight = Math.max(6f, Math.min(knobHeight, getHeight()));
        clampFraction();
    }

    @Override
    public void render(DrawContext context) {
        if (!isVisible())
            return;

        int track = isDisabled() ? ColorHelper.blendColors(this.trackColor, 0xFF000000, 0.35f) : this.trackColor;
        int knob = isDisabled() ? ColorHelper.blendColors(this.knobColor, 0xFF000000, 0.35f) : this.knobColor;

        context.drawRect(getX(), getY(), getWidth(), getHeight(), track);
        float knobY = knobTop();
        context.drawRect(getX(), knobY, getWidth(), this.knobHeight, knob);
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        if (!isVisible() || isDisabled())
            return;

        this.lastMouseX = xPos;
        this.lastMouseY = yPos;
        if (!this.dragging)
            return;

        this.fraction = clamp01((float) (yPos - getY() - this.dragOffset) / (getHeight() - this.knobHeight));
        this.onScrollFractionChanged.accept(this.fraction);
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT || action != GLFW.GLFW_PRESS)
            return;

        if (isInsideKnob(this.lastMouseX, this.lastMouseY)) {
            this.dragging = true;
            this.dragOffset = (float) (this.lastMouseY - knobTop());
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.dragging = false;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            this.dragging = false;
        }
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        if (disabled) {
            this.dragging = false;
        }
    }

    private boolean isInsideKnob(double mouseX, double mouseY) {
        float knobY = knobTop();
        return mouseX >= getX() && mouseX <= getX() + getWidth()
                && mouseY >= knobY && mouseY <= knobY + this.knobHeight;
    }

    private float knobTop() {
        float range = getHeight() - this.knobHeight;
        return getY() + this.fraction * (range <= 0 ? 0 : range);
    }

    private void clampFraction() {
        this.fraction = clamp01(this.fraction);
    }

    private float clamp01(float value) {
        return Math.clamp(value, 0f, 1f);
    }
}
