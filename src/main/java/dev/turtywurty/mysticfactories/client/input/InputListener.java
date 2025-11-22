package dev.turtywurty.mysticfactories.client.input;

public interface InputListener {
    default void onKeyPress(int keyCode, int scanCode, int modifiers) {}
    default void onKeyRelease(int keyCode, int scanCode, int modifiers) {}

    default void onMouseMove(double xPos, double yPos) {}
    default void onMouseScroll(double xOffset, double yOffset) {}
    default void onMouseButtonPress(int button, int action, int modifiers) {}
    default void onMouseButtonRelease(int button, int action, int modifiers) {}

    /**
     * Per-frame hook; useful for reading continuous key states.
     */
    default void onUpdate(double deltaTime) {}
}
