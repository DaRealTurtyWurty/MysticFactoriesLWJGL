package dev.turtywurty.mysticfactories.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Keyboard extends GLFWKeyCallback {
    private static final boolean[] KEYS = new boolean[GLFW.GLFW_KEY_LAST];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= 0 && key < GLFW.GLFW_KEY_LAST) {
            KEYS[key] = action != GLFW.GLFW_RELEASE;
        }
    }

    public static boolean isKeyDown(int key) {
        return KEYS[key];
    }
}
