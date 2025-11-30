package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

/**
 * Base class for interactive buttons. Handles hover/press state and input forwarding.
 */
public abstract class AbstractButton extends Widget {
    @Getter
    private boolean hovered;
    private boolean pressed;
    private double mouseX;
    private double mouseY;

    protected AbstractButton(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public final void render(DrawContext context) {
        if (!isVisible())
            return;

        this.hovered = !isDisabled() && isInside(context.mouseX(), context.mouseY());
        boolean pressedState = !isDisabled() && this.pressed;
        renderButton(context, this.hovered, pressedState);
    }

    /**
     * Called every frame to draw the button with the latest state.
     */
    protected abstract void renderButton(DrawContext context, boolean hovered, boolean pressed);

    /**
     * Called when the button is clicked (press + release within bounds).
     */
    protected void onClick() {
        // Subclasses can override.
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            this.hovered = false;
            this.pressed = false;
        }
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        if (disabled) {
            this.hovered = false;
            this.pressed = false;
        }
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        if (!isVisible() || isDisabled())
            return;

        this.mouseX = xPos;
        this.mouseY = yPos;
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT || action != GLFW.GLFW_PRESS)
            return;

        if (isInside(this.mouseX, this.mouseY)) {
            this.pressed = true;
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return;

        boolean wasPressed = this.pressed;
        this.pressed = false;
        if (wasPressed && isInside(this.mouseX, this.mouseY)) {
            onClick();
        }
    }

    /**
     * @return true if the provided mouse coordinates are within the button bounds.
     */
    protected boolean isInside(double mouseX, double mouseY) {
        return isVisible() && !isDisabled() && mouseX >= getX() && mouseX <= getX() + getWidth() &&
                mouseY >= getY() && mouseY <= getY() + getHeight();
    }
}
