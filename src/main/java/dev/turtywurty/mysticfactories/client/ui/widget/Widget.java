package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.input.InputListener;
import dev.turtywurty.mysticfactories.client.ui.UIElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Base widget type that lives in screen space and can react to input.
 */
@Getter
public abstract class Widget implements UIElement, InputListener {
    @Setter
    private float x;
    @Setter
    private float y;
    private float width;
    private float height;
    @Setter
    private boolean visible = true;
    @Setter
    private boolean disabled;

    protected Widget(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the widget position in screen space.
     *
     * @param x left coordinate in pixels
     * @param y top coordinate in pixels
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the widget size in pixels.
     *
     * @param width  width in pixels
     * @param height height in pixels
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean containsPoint(double px, double py) {
        if (!this.visible || this.disabled)
            return false;

        return px >= this.x && px <= this.x + this.width && py >= this.y && py <= this.y + this.height;
    }
}
