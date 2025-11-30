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

    protected Widget(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
