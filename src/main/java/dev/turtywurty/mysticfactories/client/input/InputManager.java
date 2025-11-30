package dev.turtywurty.mysticfactories.client.input;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.ui.GUIStack;
import dev.turtywurty.mysticfactories.client.window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InputManager {
    private final Camera camera;
    private final Window window;
    private final List<InputListener> listeners = new ArrayList<>();
    private final boolean[] previousKeys = new boolean[GLFW.GLFW_KEY_LAST];
    private final boolean[] previousMouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private double previousMouseX;
    private double previousMouseY;

    public InputManager(Camera camera, Window window) {
        this.camera = camera;
        this.window = window;
    }

    public void poll(double deltaTime) {
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            GLFW.glfwSetWindowShouldClose(this.window.getId(), true);
        }

        List<InputListener> activeListeners = getActiveListeners();
        dispatchKeyEvents(activeListeners);
        dispatchMouseEvents(activeListeners);

        if (Mouse.isScrollMoved()) {
            if (GUIStack.isEmpty()) {
                camera.processScroll((float) Mouse.getScrollY());
                camera.setOrthoBounds(this.window.getWidth(), this.window.getHeight());
            }
            Mouse.resetScrollMoved();
        }

        for (InputListener listener : activeListeners) {
            listener.onUpdate(deltaTime);
        }
    }

    public void addListener(InputListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(InputListener listener) {
        this.listeners.remove(listener);
    }

    private List<InputListener> getActiveListeners() {
        if (!GUIStack.isEmpty()) {
            return List.of(GUIStack.instance());
        }

        return this.listeners;
    }

    private void dispatchKeyEvents(List<InputListener> activeListeners) {
        for (int key = 0; key < GLFW.GLFW_KEY_LAST; key++) {
            boolean isDown = Keyboard.isKeyDown(key);
            boolean wasDown = this.previousKeys[key];

            if (isDown && !wasDown) {
                for (InputListener listener : activeListeners) {
                    listener.onKeyPress(key, 0, 0);
                }
            } else if (!isDown && wasDown) {
                for (InputListener listener : activeListeners) {
                    listener.onKeyRelease(key, 0, 0);
                }
            }

            this.previousKeys[key] = isDown;
        }
    }

    private void dispatchMouseEvents(List<InputListener> activeListeners) {
        double scaleX = this.window.getFramebufferWidth() / (double) this.window.getWidth();
        double scaleY = this.window.getFramebufferHeight() / (double) this.window.getHeight();
        double mouseX = Mouse.getX() * scaleX;
        double mouseY = Mouse.getY() * scaleY;
        if (mouseX != this.previousMouseX || mouseY != this.previousMouseY) {
            for (InputListener listener : activeListeners) {
                listener.onMouseMove(mouseX, mouseY);
            }
        }

        for (int button = 0; button < GLFW.GLFW_MOUSE_BUTTON_LAST; button++) {
            boolean isDown = Mouse.isButtonDown(button);
            boolean wasDown = this.previousMouseButtons[button];

            if (isDown && !wasDown) {
                for (InputListener listener : activeListeners) {
                    listener.onMouseButtonPress(button, GLFW.GLFW_PRESS, 0);
                }
            } else if (!isDown && wasDown) {
                for (InputListener listener : activeListeners) {
                    listener.onMouseButtonRelease(button, GLFW.GLFW_RELEASE, 0);
                }
            }

            this.previousMouseButtons[button] = isDown;
        }

        if (Mouse.isScrollMoved()) {
            double scrollX = Mouse.getScrollX();
            double scrollY = Mouse.getScrollY();
            for (InputListener listener : activeListeners) {
                listener.onMouseScroll(scrollX, scrollY);
            }
        }

        this.previousMouseX = mouseX;
        this.previousMouseY = mouseY;
    }
}
