package dev.turtywurty.mysticfactories.client;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.input.InputManager;
import dev.turtywurty.mysticfactories.client.render.GameRenderer;
import dev.turtywurty.mysticfactories.client.render.world.WorldRenderer;
import dev.turtywurty.mysticfactories.client.render.world.WorldRendererBase;
import dev.turtywurty.mysticfactories.client.render.world.WorldRendererRegistry;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRendererRegistry;
import dev.turtywurty.mysticfactories.client.settings.Settings;
import dev.turtywurty.mysticfactories.client.window.Window;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.client.world.LocalWorldConnection;
import dev.turtywurty.mysticfactories.init.TileTypes;
import dev.turtywurty.mysticfactories.init.WorldTypes;
import dev.turtywurty.mysticfactories.server.IntegratedServer;
import dev.turtywurty.mysticfactories.server.ServerWorld;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.WorldTypeRegistry;
import dev.turtywurty.mysticfactories.world.tile.TileRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class GameClient implements Runnable {
    private static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameThread;
    private final Settings settings;

    private Camera camera;
    private InputManager inputManager;
    private GameRenderer gameRenderer;
    private EntityRendererRegistry entityRendererRegistry;
    private WorldRendererRegistry worldRendererRegistry;
    private TileRegistry tileRegistry;
    private WorldTypeRegistry worldTypeRegistry;
    private @Nullable IntegratedServer integratedServer; // null when connected to remote
    private ClientWorld clientWorld;
    private WorldRenderer worldRenderer;

    public GameClient() {
        this.settings = Settings.load();
        this.window = new Window("Mystic Factories", this.settings);
        this.gameThread = new Thread(this, "client-thread");
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
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.camera = new Camera(new Vector2f(0.0f, 0.0f), 200f);
        this.camera.setOrthoBounds(this.window.getWidth(), this.window.getHeight());
        this.inputManager = new InputManager(this.camera, this.window);

        setupWorldsAndRenderers();
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

            handleWindowResize();
            input(frameTime);

            while (accumulator >= ups) {
                update(ups);
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
                // noinspection BusyWait
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handleWindowResize() {
        if (this.window.hasResized()) {
            this.camera.setOrthoBounds(this.window.getWidth(), this.window.getHeight());
            this.window.setResized(false);
        }
    }

    private void input(double deltaTime) {
        this.inputManager.poll(deltaTime);
    }

    private void update(double deltaTime) {
        if (this.integratedServer != null) {
            this.integratedServer.tick(deltaTime);
        }

        if (this.clientWorld != null) {
            this.clientWorld.tick(deltaTime);
        }
    }

    private void render(double alpha) {
        renderWorld();
    }

    private void cleanup() {
        if (this.worldRenderer != null) {
            this.worldRenderer.cleanup();
        }

        if (this.worldRendererRegistry != null) {
            this.worldRendererRegistry.cleanup();
        }

        if (this.gameRenderer != null) {
            this.gameRenderer.cleanup();
        }

        if (this.tileRegistry != null) {
            this.tileRegistry.cleanup();
        }

        this.window.getSettings().save();
        this.window.destroy();
    }

    private void renderWorld() {
        if (this.gameRenderer != null) {
            WorldRendererBase renderer = this.clientWorld != null ?
                    this.worldRendererRegistry.getRendererFor(this.clientWorld.getWorldType()) :
                    null;
            this.gameRenderer.render(this.clientWorld, renderer);
        }
    }

    private void setupWorldsAndRenderers() {
        this.tileRegistry = new TileRegistry();
        TileTypes.register(this.tileRegistry);
        this.entityRendererRegistry = new EntityRendererRegistry();
        this.worldRenderer = new WorldRenderer(this.entityRendererRegistry, this.tileRegistry);
        this.worldRendererRegistry = new WorldRendererRegistry(this.worldRenderer);
        this.worldTypeRegistry = new WorldTypeRegistry();
        WorldTypes.register(this.worldTypeRegistry);

        // Integrated server for singleplayer
        this.integratedServer = new IntegratedServer();
        var overworldType = WorldTypes.OVERWORLD;
        var overworld = new ServerWorld(overworldType);
        for (int chunkX = -10; chunkX <= 10; chunkX++) {
            for (int chunkY = -10; chunkY <= 10; chunkY++) {
                overworld.addChunk(new ChunkPos(chunkX, chunkY));
            }
        }

        this.integratedServer.addWorld(overworld);

        this.clientWorld = new ClientWorld(overworldType, overworld.getWorldData().getSeed());
        this.clientWorld.setRenderer(this.worldRendererRegistry.getRendererFor(overworldType));

        // Local world connection forwards updates to the client
        var connection = new LocalWorldConnection(this.clientWorld);
        overworld.setConnection(connection);
        connection.sendFullState(overworldType, overworld.getChunks());

        this.gameRenderer = new GameRenderer(this.camera);
    }
}
