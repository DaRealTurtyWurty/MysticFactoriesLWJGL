package dev.turtywurty.mysticfactories.window;

import dev.turtywurty.mysticfactories.settings.FullscreenMode;
import dev.turtywurty.mysticfactories.settings.Settings;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Window {
    private long id;
    private final String title;
    private final Settings settings;

    private final Keyboard keyboardCallback;
    private final Mouse.MouseButtonCallback mouseButtonCallback;
    private final Mouse.CursorPosCallback cursorPosCallback;
    private final GLFWWindowSizeCallback windowSizeCallback;

    public Window(String title, Settings settings) {
        this.title = title;
        this.settings = settings;

        this.keyboardCallback = new Keyboard();
        this.mouseButtonCallback = new Mouse.MouseButtonCallback();
        this.cursorPosCallback = new Mouse.CursorPosCallback();
        this.windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.settings.setWidth(width);
                Window.this.settings.setHeight(height);
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
        
        long monitor = MemoryUtil.NULL;
        switch (this.settings.getFullscreenMode()) {
            case FULLSCREEN -> {
                monitor = GLFW.glfwGetPrimaryMonitor();
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
                this.settings.setWidth(vidMode.width());
                this.settings.setHeight(vidMode.height());
            }
            case BORDERLESS -> {
                monitor = GLFW.glfwGetPrimaryMonitor();
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
                GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, vidMode.redBits());
                GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, vidMode.greenBits());
                GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, vidMode.blueBits());
                GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, vidMode.refreshRate());
                this.settings.setWidth(vidMode.width());
                this.settings.setHeight(vidMode.height());
            }
        }

        this.id = GLFW.glfwCreateWindow(this.settings.getWidth(), this.settings.getHeight(), this.title, monitor, MemoryUtil.NULL);
        if (this.id == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create GLFW window!");

        GLFW.glfwSetKeyCallback(this.id, this.keyboardCallback);
        GLFW.glfwSetMouseButtonCallback(this.id, this.mouseButtonCallback);
        GLFW.glfwSetCursorPosCallback(this.id, this.cursorPosCallback);
        GLFW.glfwSetWindowSizeCallback(this.id, this.windowSizeCallback);

        GLFW.glfwMakeContextCurrent(this.id);
        GL.createCapabilities();

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        setVsync(this.settings.isVsync());
        GLFW.glfwShowWindow(this.id);
    }
    
    public void setVsync(boolean vsync) {
        this.settings.setVsync(vsync);
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }
    
    public void setResolution(int newWidth, int newHeight) {
        GLFW.glfwSetWindowSize(this.id, newWidth, newHeight);
        GL11.glViewport(0, 0, newWidth, newHeight);
    }

    public void update() {
        swapBuffers();
        pollEvents();
    }

    public void destroy() {
        this.keyboardCallback.free();
        this.mouseButtonCallback.free();
        this.cursorPosCallback.free();
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

    public long getId() {
        return this.id;
    }

    public int getWidth() {
        return this.settings.getWidth();
    }

    public int getHeight() {
        return this.settings.getHeight();
    }

    public String getTitle() {
        return this.title;
    }
    
    public Settings getSettings() {
        return this.settings;
    }
}

