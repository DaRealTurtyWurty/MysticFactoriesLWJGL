package dev.turtywurty.mysticfactories.client.ui;

/**
 * Represents a drawable HUD element that renders itself in screen space.
 */
public interface UIElement {
    default void preRender(DrawContext context) {}
    void render(DrawContext context);
    default void postRender(DrawContext context) {}

    default void cleanup() {
        // No-op by default; override if the element owns resources.
    }
}
