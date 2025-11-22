package dev.turtywurty.mysticfactories.client.input;

import lombok.Getter;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

public class Mouse {
    private static final boolean[] BUTTONS = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    @Getter
    private static double x;
    @Getter
    private static double y;
    private static double xScrollOffset, yScrollOffset;
    @Getter
    private static boolean scrollMoved = false;

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

    public static class ScrollCallback extends GLFWScrollCallback {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            xScrollOffset = xoffset;
            yScrollOffset = yoffset;
            scrollMoved = true;
        }
    }

    public static boolean isButtonDown(int button) {
        return BUTTONS[button];
    }

    public static double getScrollX() {
        return xScrollOffset;
    }

    public static double getScrollY() {
        return yScrollOffset;
    }

    public static void resetScrollMoved() {
        scrollMoved = false;
        xScrollOffset = 0.0;
        yScrollOffset = 0.0;
    }
}
