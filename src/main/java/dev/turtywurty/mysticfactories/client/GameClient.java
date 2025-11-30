package dev.turtywurty.mysticfactories.client;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.input.InputManager;
import dev.turtywurty.mysticfactories.client.input.PlayerInputController;
import dev.turtywurty.mysticfactories.client.render.GameRenderer;
import dev.turtywurty.mysticfactories.client.render.world.WorldRenderer;
import dev.turtywurty.mysticfactories.client.render.world.entity.BasicEntityRenderer;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRendererRegistry;
import dev.turtywurty.mysticfactories.client.settings.Settings;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.GUIStack;
import dev.turtywurty.mysticfactories.client.ui.HUDManager;
import dev.turtywurty.mysticfactories.client.ui.MainMenuGUI;
import dev.turtywurty.mysticfactories.client.ui.widget.TextLabel;
import dev.turtywurty.mysticfactories.client.window.Window;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.client.world.LocalWorldConnection;
import dev.turtywurty.mysticfactories.init.EntityTypes;
import dev.turtywurty.mysticfactories.init.TileTypes;
import dev.turtywurty.mysticfactories.init.WorldTypes;
import dev.turtywurty.mysticfactories.server.IntegratedServer;
import dev.turtywurty.mysticfactories.server.ServerWorld;
import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.WorldTypeRegistry;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityTypeRegistry;
import dev.turtywurty.mysticfactories.world.tile.TileRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.Optional;

public class GameClient implements Runnable {
    private static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameThread;
    private final Settings settings;

    private Camera camera;
    private InputManager inputManager;
    private GameRenderer gameRenderer;
    private TileRegistry tileRegistry;
    private @Nullable IntegratedServer integratedServer; // null when connected to remote
    private ClientWorld clientWorld;
    private WorldRenderer worldRenderer;
    private boolean worldStarted;

    public GameClient() {
        this.settings = Settings.load();
        this.window = new Window("Mystic Factories", this.settings);
        this.gameThread = new Thread(this, "client-thread");
        this.worldStarted = false;
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
        GUIStack.onResize(this.window.getWidth(), this.window.getHeight());
        this.inputManager = new InputManager(this.camera, this.window);
        Fonts.init();
        this.gameRenderer = new GameRenderer(this.camera, this.window);

        GUIStack.push(new MainMenuGUI(() -> startGameWorld()));
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
            GUIStack.onResize(this.window.getWidth(), this.window.getHeight());
            this.window.setResized(false);
        }
    }

    private void input(double deltaTime) {
        this.inputManager.poll(deltaTime);
    }

    private void update(double deltaTime) {
        boolean pauseIntegrated = this.integratedServer != null && GUIStack.shouldPauseGame();
        if (this.integratedServer != null && !pauseIntegrated) {
            this.integratedServer.tick(deltaTime);
        }

        if (this.clientWorld != null && !pauseIntegrated) {
            this.clientWorld.tick(deltaTime);
        }
    }

    private void render(double alpha) {
        if (this.gameRenderer != null) {
            WorldRenderer renderer = this.clientWorld != null ?
                    this.worldRenderer :
                    null;

            this.gameRenderer.render(this.clientWorld, renderer);
        }
    }

    private void cleanup() {
        if (this.worldRenderer != null) {
            this.worldRenderer.cleanup();
        }

        if (this.gameRenderer != null) {
            this.gameRenderer.cleanup();
        }

        if (this.tileRegistry != null) {
            this.tileRegistry.cleanup();
        }

        Fonts.cleanup();
        this.window.getSettings().save();
        this.window.destroy();
    }

    private void startGameWorld() {
        if (this.worldStarted)
            return;

        this.worldStarted = true;

        this.tileRegistry = new TileRegistry();
        TileTypes.register(this.tileRegistry);
        var entityTypeRegistry = new EntityTypeRegistry();
        EntityTypes.register(entityTypeRegistry);
        var entityRendererRegistry = new EntityRendererRegistry();
        entityRendererRegistry.registerRenderer(EntityTypes.PLAYER, new BasicEntityRenderer<>(EntityTypes.PLAYER.id(), 16.0f));
        this.worldRenderer = new WorldRenderer(entityRendererRegistry, this.tileRegistry);
        var worldTypeRegistry = new WorldTypeRegistry();
        WorldTypes.register(worldTypeRegistry);

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

        // Local world connection forwards updates to the client
        var connection = new LocalWorldConnection(this.clientWorld);
        overworld.setConnection(connection);
        connection.sendFullState(overworldType, overworld.getChunks());

        // Spawn a local player and bind it
        var player = EntityTypes.PLAYER.create(overworld);
        overworld.addEntity(player);
        connection.sendEntitySpawn(overworldType, player);
        connection.sendPlayerBind(overworldType, player.getUuid());
        this.clientWorld.getLocalPlayer().ifPresent(this.camera::setFollowTarget);
        this.inputManager.addListener(new PlayerInputController(this.clientWorld));

        HUDManager.addLastElement(Identifier.of("debug_position"),
                TextLabel.builder()
                        .textSupplier(() -> {
                            Optional<Entity> localPlayerOpt = this.clientWorld.getLocalPlayer();
                            if (localPlayerOpt.isEmpty())
                                return "X: 0.00, Y: 0.00";

                            Vector2d pos = localPlayerOpt.get().getPosition();

                            return String.format("X: %.2f, Y: %.2f", pos.x, pos.y);
                        })
                        .position(8, 8)
                        .build());

        GUIStack.pop();
    }
}
