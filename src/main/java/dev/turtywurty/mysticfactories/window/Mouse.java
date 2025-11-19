package dev.turtywurty.mysticfactories.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class Mouse {
    private static final boolean[] BUTTONS = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double x, y;

    public static class MouseButtonCallback extends GLFWMouseButtonCallback {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (button >= 0 && button < GLFW.GLFW_MOUSE_BUTTON_LAST) {
                BUTTONS[button] = action != GLFW.GLFW_RELEASE;
            }
        }
    }

    public static class CursorPosCallback extends GLFWCursorPosCallback {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            x = xpos;
            y = ypos;
        }
    }

    public static boolean isButtonDown(int button) {
        return BUTTONS[button];
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }
}
