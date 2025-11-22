package dev.turtywurty.mysticfactories.client.input;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InputManager {
    private final Camera camera;
    private final Window window;
    private final List<InputListener> listeners = new ArrayList<>();

    public InputManager(Camera camera, Window window) {
        this.camera = camera;
        this.window = window;
    }

    public void poll(double deltaTime) {
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            GLFW.glfwSetWindowShouldClose(this.window.getId(), true);
        }

        if (Mouse.isScrollMoved()) {
            camera.processScroll((float) Mouse.getScrollY());
            camera.setOrthoBounds(this.window.getWidth(), this.window.getHeight());
            Mouse.resetScrollMoved();
        }

        for (InputListener listener : this.listeners) {
            listener.onUpdate(deltaTime);
        }
    }

    public void addListener(InputListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(InputListener listener) {
        this.listeners.remove(listener);
    }
}
