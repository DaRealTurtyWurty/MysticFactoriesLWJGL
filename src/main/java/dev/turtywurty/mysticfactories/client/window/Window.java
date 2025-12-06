package dev.turtywurty.mysticfactories.client.window;

import dev.turtywurty.mysticfactories.client.input.InputManager;
import dev.turtywurty.mysticfactories.client.input.Keyboard;
import dev.turtywurty.mysticfactories.client.input.Mouse;
import dev.turtywurty.mysticfactories.client.settings.ResolutionPreset;
import dev.turtywurty.mysticfactories.client.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Window {
    @Getter
    private long id;
    @Getter
    private final String title;
    @Setter
    private boolean resized;
    private int framebufferWidth;
    private int framebufferHeight;

    private InputManager inputManager;

    private final Keyboard keyboardCallback;
    private final Mouse.MouseButtonCallback mouseButtonCallback;
    private final Mouse.CursorPosCallback cursorPosCallback;
    private final Mouse.ScrollCallback scrollCallback;
    private final GLFWWindowSizeCallback windowSizeCallback;
    private GLFWCharCallback charCallback;

    public Window(String title) {
        this.title = title;
        this.resized = false;

        this.keyboardCallback = new Keyboard();
        this.mouseButtonCallback = new Mouse.MouseButtonCallback();
        this.cursorPosCallback = new Mouse.CursorPosCallback();
        this.scrollCallback = new Mouse.ScrollCallback();
        this.windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Settings.getInstance().setWindowWidth(width);
                Settings.getInstance().setWindowHeight(height);
                Window.this.updateFramebufferSize();
                Window.this.resized = true;
                GL11.glViewport(0, 0, Window.this.framebufferWidth, Window.this.framebufferHeight);
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

        switch (Settings.getInstance().getFullscreenMode()) {
            case FULLSCREEN -> {
                GLFWVidMode vidMode = getVideoMode();
                Settings.getInstance().setWindowWidth(vidMode.width());
                Settings.getInstance().setWindowHeight(vidMode.height());
            }
            case BORDERLESS -> {
                GLFWVidMode vidMode = getVideoMode();
                GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, vidMode.redBits());
                GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, vidMode.greenBits());
                GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, vidMode.blueBits());
                GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, vidMode.refreshRate());
                Settings.getInstance().setWindowWidth(vidMode.width());
                Settings.getInstance().setWindowHeight(vidMode.height());
            }
        }

        long monitor = switch (Settings.getInstance().getFullscreenMode()) {
            case WINDOWED -> MemoryUtil.NULL;
            case FULLSCREEN, BORDERLESS -> GLFW.glfwGetPrimaryMonitor();
        };
        this.id = GLFW.glfwCreateWindow(Settings.getInstance().getWindowWidth(), Settings.getInstance().getWindowHeight(), this.title, monitor, MemoryUtil.NULL);
        if (this.id == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create GLFW window!");

        this.charCallback = new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                if (Window.this.inputManager != null) {
                    Window.this.inputManager.onCharInput(codepoint);
                }
            }
        };

        GLFW.glfwSetKeyCallback(this.id, this.keyboardCallback);
        GLFW.glfwSetCharCallback(this.id, this.charCallback);
        GLFW.glfwSetMouseButtonCallback(this.id, this.mouseButtonCallback);
        GLFW.glfwSetCursorPosCallback(this.id, this.cursorPosCallback);
        GLFW.glfwSetScrollCallback(this.id, this.scrollCallback);
        GLFW.glfwSetWindowSizeCallback(this.id, this.windowSizeCallback);

        GLFW.glfwMakeContextCurrent(this.id);
        GL.createCapabilities();
        updateFramebufferSize();

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        setVsync(Settings.getInstance().isVsync());
        GLFW.glfwShowWindow(this.id);
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    private GLFWVidMode getVideoMode() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
        if (vidMode == null)
            throw new RuntimeException("Failed to get video mode for primary monitor!");

        return vidMode;
    }


    public void setVsync(boolean vsync) {
        Settings.getInstance().setVsync(vsync);
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public void setResolution(int newWidth, int newHeight) {
        GLFW.glfwSetWindowSize(this.id, newWidth, newHeight);
        Settings.getInstance().setWindowWidth(newWidth);
        Settings.getInstance().setWindowHeight(newHeight);
        updateFramebufferSize();
        GL11.glViewport(0, 0, this.framebufferWidth, this.framebufferHeight);
        this.resized = true;
    }

    public void setResolutionPreset(ResolutionPreset preset) {
        if (preset == null)
            return;

        Settings.getInstance().setResolutionPreset(preset);
        setResolution(preset.getWidth(), preset.getHeight());
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
        this.charCallback.free();

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

    private void updateFramebufferSize() {
        int[] fbw = new int[1];
        int[] fbh = new int[1];
        GLFW.glfwGetFramebufferSize(this.id, fbw, fbh);
        this.framebufferWidth = fbw[0];
        this.framebufferHeight = fbh[0];
    }

    public static long getCurrentWindowId() {
        return GLFW.glfwGetCurrentContext();
    }

    public int getWidth() {
        return Settings.getInstance().getWindowWidth();
    }

    public int getHeight() {
        return Settings.getInstance().getWindowHeight();
    }

    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }

    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }
}

