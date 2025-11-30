package dev.turtywurty.mysticfactories.client.window;

import dev.turtywurty.mysticfactories.client.input.Keyboard;
import dev.turtywurty.mysticfactories.client.input.Mouse;
import dev.turtywurty.mysticfactories.client.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Window {
    @Getter
    private long id;
    @Getter
    private final String title;
    @Getter
    private final Settings settings;
    @Setter
    private boolean resized;

    private final Keyboard keyboardCallback;
    private final Mouse.MouseButtonCallback mouseButtonCallback;
    private final Mouse.CursorPosCallback cursorPosCallback;
    private final Mouse.ScrollCallback scrollCallback;
    private final GLFWWindowSizeCallback windowSizeCallback;

    public Window(String title, Settings settings) {
        this.title = title;
        this.settings = settings;
        this.resized = false;

        this.keyboardCallback = new Keyboard();
        this.mouseButtonCallback = new Mouse.MouseButtonCallback();
        this.cursorPosCallback = new Mouse.CursorPosCallback();
        this.scrollCallback = new Mouse.ScrollCallback();
        this.windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.settings.setWindowWidth(width);
                Window.this.settings.setWindowHeight(height);
                Window.this.resized = true;
                GL11.glViewport(0, 0, width, height);
            }
        };
    }

    public void create() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW!");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        switch (this.settings.getFullscreenMode()) {
            case FULLSCREEN -> {
                GLFWVidMode vidMode = getVideoMode();
                this.settings.setWindowWidth(vidMode.width());
                this.settings.setWindowHeight(vidMode.height());
            }
            case BORDERLESS -> {
                GLFWVidMode vidMode = getVideoMode();
                GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, vidMode.redBits());
                GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, vidMode.greenBits());
                GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, vidMode.blueBits());
                GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, vidMode.refreshRate());
                this.settings.setWindowWidth(vidMode.width());
                this.settings.setWindowHeight(vidMode.height());
            }
        }

        long monitor = switch (this.settings.getFullscreenMode()) {
            case WINDOWED -> MemoryUtil.NULL;
            case FULLSCREEN, BORDERLESS -> GLFW.glfwGetPrimaryMonitor();
        };
        this.id = GLFW.glfwCreateWindow(this.settings.getWindowWidth(), this.settings.getWindowHeight(), this.title, monitor, MemoryUtil.NULL);
        if (this.id == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create GLFW window!");

        GLFW.glfwSetKeyCallback(this.id, this.keyboardCallback);
        GLFW.glfwSetMouseButtonCallback(this.id, this.mouseButtonCallback);
        GLFW.glfwSetCursorPosCallback(this.id, this.cursorPosCallback);
        GLFW.glfwSetScrollCallback(this.id, this.scrollCallback);
        GLFW.glfwSetWindowSizeCallback(this.id, this.windowSizeCallback);

        GLFW.glfwMakeContextCurrent(this.id);
        GL.createCapabilities();

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        setVsync(this.settings.isVsync());
        GLFW.glfwShowWindow(this.id);
    }

    private GLFWVidMode getVideoMode() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
        if (vidMode == null)
            throw new RuntimeException("Failed to get video mode for primary monitor!");

        return vidMode;
    }

    public void setVsync(boolean vsync) {
        this.settings.setVsync(vsync);
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public void setResolution(int newWidth, int newHeight) {
        GLFW.glfwSetWindowSize(this.id, newWidth, newHeight);
        GL11.glViewport(0, 0, newWidth, newHeight);
        this.settings.setWindowWidth(newWidth);
        this.settings.setWindowHeight(newHeight);
        this.resized = true;
    }

    public void update() {
        swapBuffers();
        pollEvents();
    }

    public void destroy() {
        this.keyboardCallback.free();
        this.mouseButtonCallback.free();
        this.cursorPosCallback.free();
        this.scrollCallback.free();
        this.windowSizeCallback.free();

        GLFW.glfwDestroyWindow(this.id);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.id);
    }

    private void pollEvents() {
        GLFW.glfwPollEvents();
    }

    private void swapBuffers() {
        GLFW.glfwSwapBuffers(this.id);
    }

    public boolean hasResized() {
        return resized;
    }

    public static long getCurrentWindowId() {
        return GLFW.glfwGetCurrentContext();
    }

    public int getWidth() {
        return this.settings.getWindowWidth();
    }

    public int getHeight() {
        return this.settings.getWindowHeight();
    }
}

