package dev.turtywurty.mysticfactories.core;

import dev.turtywurty.mysticfactories.window.Keyboard;
import dev.turtywurty.mysticfactories.settings.Settings;
import dev.turtywurty.mysticfactories.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Game implements Runnable {
    private static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameThread;
    private final Settings settings;

    private boolean isSpacePressed = false;

    public Game() {
        this.settings = Settings.load();
        this.window = new Window("Mystic Factories", this.settings);
        this.gameThread = new Thread(this, "game-thread");
    }

    public void start() {
        this.gameThread.start();
    }

    @Override
    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        this.window.create();
    }

    private void loop() {
        final double ups = 1.0 / TARGET_UPS;
        double accumulator = 0.0;
        double lastTime = GLFW.glfwGetTime();
        double timer = 0.0;
        int frames = 0;
        int ticks = 0;

        while (!this.window.shouldClose()) {
            double currentTime = GLFW.glfwGetTime();
            double frameTime = currentTime - lastTime;
            lastTime = currentTime;

            accumulator += frameTime;
            timer += frameTime;

            while (accumulator >= ups) {
                input();
                update();
                accumulator -= ups;
                ticks++;
            }

            render(accumulator / ups);
            this.window.update();
            frames++;

            if (!this.settings.isVsync()) {
                sync(currentTime);
            }

            if (timer >= 1.0) {
                System.out.println("FPS: " + frames + " | UPS: " + ticks);
                frames = 0;
                ticks = 0;
                timer -= 1.0;
            }
        }
    }

    private void sync(double loopStartTime) {
        float loopSlot = 1f / this.settings.getFpsCap();
        double endTime = loopStartTime + loopSlot;
        while (GLFW.glfwGetTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void input() {
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            GLFW.glfwSetWindowShouldClose(this.window.getId(), true);
        }

        this.isSpacePressed = Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE);
    }

    private void update() {
        // Update game logic
    }

    private void render(double deltaTime) {
        if (this.isSpacePressed) {
            GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        } else {
            GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Render game with interpolation using alpha
    }

    private void cleanup() {
        this.window.getSettings().save();
        this.window.destroy();
    }
}
