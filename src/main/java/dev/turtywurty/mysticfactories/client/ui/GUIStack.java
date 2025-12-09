package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.input.InputListener;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Global stack of GUIs. Top-most GUI receives input; all GUIs render bottom-to-top.
 */
public final class GUIStack implements InputListener {
    private static final GUIStack INSTANCE = new GUIStack();
    private final Deque<GUI> guis = new ArrayDeque<>();
    private int lastWidth = -1;
    private int lastHeight = -1;

    private GUIStack() {
    }

    public static GUIStack instance() {
        return INSTANCE;
    }

    public static void push(GUI gui) {
        INSTANCE.guis.addLast(gui);
        if (INSTANCE.lastWidth > 0 && INSTANCE.lastHeight > 0) {
            gui.init(INSTANCE.lastWidth, INSTANCE.lastHeight);
        }
    }

    public static GUI pop() {
        return INSTANCE.guis.pollLast();
    }

    public static GUI peek() {
        return INSTANCE.guis.peekLast();
    }

    public static boolean isEmpty() {
        return INSTANCE.guis.isEmpty();
    }

    public static void clear() {
        for (GUI gui : List.copyOf(INSTANCE.guis)) {
            gui.cleanup();
        }

        INSTANCE.guis.clear();
    }

    public static void onResize(int width, int height) {
        INSTANCE.lastWidth = width;
        INSTANCE.lastHeight = height;
        for (GUI gui : List.copyOf(INSTANCE.guis)) {
            gui.init(width, height);
        }
    }

    public static void render(DrawContext context) {
        INSTANCE.renderInternal(context);
    }

    public static boolean shouldPauseGame() {
        GUI top = peek();
        return top != null && top.shouldPauseGame();
    }

    private void renderInternal(DrawContext context) {
        // Iterate from bottom to top so higher GUIs draw over lower ones.
        List<GUI> copy = List.copyOf(this.guis);
        for (GUI gui : copy) {
            gui.preRender(context);
        }

        for (GUI gui : copy) {
            gui.render(context);
        }

        for (GUI gui : copy) {
            gui.postRender(context);
        }
    }

    @Override
    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        GUI top = peek();
        if (top != null) {
            top.onKeyPress(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        GUI top = peek();
        if (top != null) {
            top.onKeyRelease(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onCharInput(int codepoint) {
        GUI top = peek();
        if (top != null) {
            top.onCharInput(codepoint);
        }
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        GUI top = peek();
        if (top != null) {
            top.onMouseMove(xPos, yPos);
        }
    }

    @Override
    public void onMouseScroll(double xOffset, double yOffset) {
        GUI top = peek();
        if (top != null) {
            top.onMouseScroll(xOffset, yOffset);
        }
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        GUI top = peek();
        if (top != null) {
            top.onMouseButtonPress(button, action, modifiers);
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        GUI top = peek();
        if (top != null) {
            top.onMouseButtonRelease(button, action, modifiers);
        }
    }

    @Override
    public void onUpdate(double deltaTime) {
        GUI top = peek();
        if (top != null) {
            top.onUpdate(deltaTime);
        }
    }
}
