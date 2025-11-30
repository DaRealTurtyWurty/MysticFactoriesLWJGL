package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.input.InputListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Basic GUI screen that owns a collection of UI widgets and forwards rendering/input to them.
 * <p>
 * Override {@link #shouldPauseGame()} to pause the integrated server while this GUI is open.
 */
public class GUI implements UIElement, InputListener {
    private final List<UIElement> widgets = new ArrayList<>();

    public void addWidget(UIElement widget) {
        this.widgets.add(Objects.requireNonNull(widget, "widget"));
    }

    public boolean removeWidget(UIElement widget) {
        return this.widgets.remove(widget);
    }

    public void clearWidgets() {
        this.widgets.clear();
    }

    public List<UIElement> getWidgets() {
        return Collections.unmodifiableList(this.widgets);
    }

    /**
     * Reinitializes this GUI for the given screen dimensions. Clears any existing widgets and
     * delegates to {@link #buildWidgets(int, int)} so subclasses can repopulate based on the new size.
     */
    public final void init(int screenWidth, int screenHeight) {
        clearWidgets();
        buildWidgets(screenWidth, screenHeight);
    }

    /**
     * Hook for subclasses to rebuild their widgets using the provided screen dimensions.
     *
     * @param screenWidth  current screen width in pixels
     * @param screenHeight current screen height in pixels
     */
    protected void buildWidgets(int screenWidth, int screenHeight) {
    }

    @Override
    public void render(DrawContext context) {
        for (UIElement widget : List.copyOf(this.widgets)) {
            widget.render(context);
        }
    }

    public void renderBackground(DrawContext context) {
        context.drawRect(0, 0, context.width(), context.height(), 0xC0101010);
    }

    /**
     * @return true to pause the game (only effective when running against an integrated server)
     */
    public boolean shouldPauseGame() {
        return false;
    }

    @Override
    public void cleanup() {
        for (UIElement widget : List.copyOf(this.widgets)) {
            widget.cleanup();
        }

        this.widgets.clear();
    }

    @Override
    public final void onKeyPress(int keyCode, int scanCode, int modifiers) {
        handleKeyPress(keyCode, scanCode, modifiers);
        dispatchToWidgets(listener -> listener.onKeyPress(keyCode, scanCode, modifiers));
    }

    @Override
    public final void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        handleKeyRelease(keyCode, scanCode, modifiers);
        dispatchToWidgets(listener -> listener.onKeyRelease(keyCode, scanCode, modifiers));
    }

    @Override
    public final void onMouseMove(double xPos, double yPos) {
        handleMouseMove(xPos, yPos);
        dispatchToWidgets(listener -> listener.onMouseMove(xPos, yPos));
    }

    @Override
    public final void onMouseScroll(double xOffset, double yOffset) {
        handleMouseScroll(xOffset, yOffset);
        dispatchToWidgets(listener -> listener.onMouseScroll(xOffset, yOffset));
    }

    @Override
    public final void onMouseButtonPress(int button, int action, int modifiers) {
        handleMouseButtonPress(button, action, modifiers);
        dispatchToWidgets(listener -> listener.onMouseButtonPress(button, action, modifiers));
    }

    @Override
    public final void onMouseButtonRelease(int button, int action, int modifiers) {
        handleMouseButtonRelease(button, action, modifiers);
        dispatchToWidgets(listener -> listener.onMouseButtonRelease(button, action, modifiers));
    }

    @Override
    public final void onUpdate(double deltaTime) {
        handleUpdate(deltaTime);
        dispatchToWidgets(listener -> listener.onUpdate(deltaTime));
    }

    protected void handleKeyPress(int keyCode, int scanCode, int modifiers) {
    }

    protected void handleKeyRelease(int keyCode, int scanCode, int modifiers) {
    }

    protected void handleMouseMove(double xPos, double yPos) {
    }

    protected void handleMouseScroll(double xOffset, double yOffset) {
    }

    protected void handleMouseButtonPress(int button, int action, int modifiers) {
    }

    protected void handleMouseButtonRelease(int button, int action, int modifiers) {
    }

    protected void handleUpdate(double deltaTime) {
    }

    private void dispatchToWidgets(Consumer<InputListener> dispatcher) {
        for (UIElement widget : List.copyOf(this.widgets)) {
            if (widget instanceof InputListener listener) {
                dispatcher.accept(listener);
            }
        }
    }
}
