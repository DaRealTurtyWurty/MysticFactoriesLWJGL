package dev.turtywurty.mysticfactories.client.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Keyboard extends GLFWKeyCallback {
    private static final boolean[] KEYS = new boolean[GLFW.GLFW_KEY_LAST];
    private static final int[] LAST_SCANCODES = new int[GLFW.GLFW_KEY_LAST];
    private static final int[] LAST_MODIFIERS = new int[GLFW.GLFW_KEY_LAST];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= 0 && key < GLFW.GLFW_KEY_LAST) {
            KEYS[key] = action != GLFW.GLFW_RELEASE;
            LAST_SCANCODES[key] = scancode;
            LAST_MODIFIERS[key] = mods;
        }
    }

    public static boolean isKeyDown(int key) {
        return KEYS[key];
    }

    public static int getLastScancode(int key) {
        return LAST_SCANCODES[key];
    }

    public static int getLastModifiers(int key) {
        return LAST_MODIFIERS[key];
    }
}
