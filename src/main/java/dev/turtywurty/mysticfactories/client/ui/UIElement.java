package dev.turtywurty.mysticfactories.client.ui;

/**
 * Represents a drawable HUD element that renders itself in screen space.
 */
public interface UIElement {
    void render(DrawContext context);

    default void cleanup() {
        // No-op by default; override if the element owns resources.
    }
}
