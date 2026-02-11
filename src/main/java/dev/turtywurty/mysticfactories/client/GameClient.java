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
import dev.turtywurty.mysticfactories.client.ui.GUI;
import dev.turtywurty.mysticfactories.client.ui.GUIStack;
import dev.turtywurty.mysticfactories.client.ui.HUDManager;
import dev.turtywurty.mysticfactories.client.ui.MainMenuGUI;
import dev.turtywurty.mysticfactories.client.ui.SettingsGUI;
import dev.turtywurty.mysticfactories.client.ui.impl.LoadingWorldGUI;
import dev.turtywurty.mysticfactories.client.ui.widget.TextLabel;
import dev.turtywurty.mysticfactories.client.window.Window;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.client.world.LocalWorldConnection;
import dev.turtywurty.mysticfactories.init.EntityTypes;
import dev.turtywurty.mysticfactories.init.WorldTypes;
import dev.turtywurty.mysticfactories.server.IntegratedServer;
import dev.turtywurty.mysticfactories.server.ServerWorld;
import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.RegistryKeys;
import dev.turtywurty.mysticfactories.util.registry.RegistryLifecycle;
import dev.turtywurty.mysticfactories.util.registry.RegistryScanner;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.biome.BiomeMapExporter;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GameClient implements Runnable {
    private static final int TARGET_UPS = 30;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameClient.class);

    private final Window window;
    private final Thread gameThread;

    private Camera camera;
    private InputManager inputManager;
    private GameRenderer gameRenderer;
    private @Nullable IntegratedServer integratedServer; // null when connected to remote
    private ClientWorld clientWorld;
    private WorldRenderer worldRenderer;
    private boolean loadingWorld;
    private boolean loadingScreenShown;
    private LoadingWorldGUI loadingScreen;
    private final AtomicReference<WorldLoadResult> pendingWorldLoad = new AtomicReference<>();
    private final AtomicReference<Throwable> pendingWorldLoadError = new AtomicReference<>();
    private int fps, ups;

    public GameClient() {
        this.window = new Window("Mystic Factories");
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

        RegistryScanner.scanForRegistryHolders();
        LOGGER.info("Registries scanned.");

        var lifecycle = new RegistryLifecycle();
        lifecycle.add(RegistryKeys.TILE_TYPES);
        lifecycle.add(RegistryKeys.ENTITY_TYPES);
        lifecycle.add(RegistryKeys.WORLD_GENERATORS);
        lifecycle.add(RegistryKeys.BIOMES);
        lifecycle.add(RegistryKeys.WORLD_TYPES);
        lifecycle.freeze();

        this.camera = new Camera(new Vector2f(0.0f, 0.0f), 200f);
        this.camera.setOrthoBounds(this.window.getWidth(), this.window.getHeight());
        GUIStack.onResize(this.window.getWidth(), this.window.getHeight());
        this.inputManager = new InputManager(this.camera, this.window);
        this.window.setInputManager(this.inputManager);
        Fonts.init();
        this.gameRenderer = new GameRenderer(this.camera, this.window);

        GUIStack.push(new MainMenuGUI(this::startGameWorld, this::openSettings));
    }

    private void loop() {
        final double ups = 1.0 / TARGET_UPS;
        double accumulator = 0.0;
        double lastTime = GLFW.glfwGetTime();
        double timer = 0.0;
        int frames = 0;
        int ticks = 0;

        while (!this.window.shouldClose()) {
            long frameStartNanos = System.nanoTime();
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

            if (!Settings.getInstance().isVsync()) {
                sync(frameStartNanos);
            }

            if (timer >= 1.0) {
                this.fps = frames;
                this.ups = ticks;
                LOGGER.info("FPS: {} | UPS: {}", frames, ticks);
                frames = 0;
                ticks = 0;
                timer -= 1.0;
            }
        }
    }

    private void sync(long frameStartTimeNanos) {
        int fpsCap = Settings.getInstance().getFpsCap();
        if (fpsCap <= 0)
            return;

        long frameDuration = 1_000_000_000L / fpsCap;
        long endTime = frameStartTimeNanos + frameDuration;
        long now;
        while ((now = System.nanoTime()) < endTime) {
            long remaining = endTime - now;
            if (remaining > 2_000_000L) { // more than ~2ms left
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } else {
                Thread.onSpinWait();
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
        handleWorldLoading();

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

        Fonts.cleanup();
        Settings.getInstance().save();
        this.window.destroy();
    }

    private void handleWorldLoading() {
        if (!this.loadingWorld)
            return;

        Throwable error = this.pendingWorldLoadError.getAndSet(null);
        if (error != null) {
            LOGGER.error("Failed to load world!", error);
            removeLoadingScreen();
            this.loadingWorld = false;
            return;
        }

        WorldLoadResult result = this.pendingWorldLoad.getAndSet(null);
        if (result == null)
            return;

        completeWorldLoad(result);
        updateLoadingScreen(1.0f, "Starting world...");
        removeLoadingScreen();
        removeMainMenuIfTop();
        this.loadingWorld = false;
    }

    private void completeWorldLoad(WorldLoadResult result) {
        var entityRendererRegistry = new EntityRendererRegistry();
        entityRendererRegistry.registerRenderer(EntityTypes.PLAYER, new BasicEntityRenderer<>(EntityTypes.PLAYER.getId(), 16.0f));
        this.worldRenderer = new WorldRenderer(entityRendererRegistry);

        this.integratedServer = result.integratedServer();
        var overworld = result.overworld();
        this.clientWorld = new ClientWorld(WorldTypes.OVERWORLD, overworld.getWorldData().getSeed());

        // Local world connection forwards updates to the client
        var connection = new LocalWorldConnection(this.clientWorld);
        overworld.setConnection(connection);
        connection.sendFullState(WorldTypes.OVERWORLD, overworld.getChunks());

        // Spawn a local player and bind it
        var player = EntityTypes.PLAYER.create(overworld);
        overworld.addEntity(player);
        connection.sendEntitySpawn(WorldTypes.OVERWORLD, player);
        connection.sendPlayerBind(WorldTypes.OVERWORLD, player.getUuid());
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

        HUDManager.addLastElement(Identifier.of("debug_biome"),
                TextLabel.builder()
                        .textSupplier(() -> {
                            Optional<Entity> localPlayerOpt = this.clientWorld.getLocalPlayer();
                            if (localPlayerOpt.isEmpty())
                                return "Biome: unknown";

                            Vector2d pos = localPlayerOpt.get().getPosition();
                            float tileSize = this.clientWorld.getTileSize();
                            var tilePos = new TilePos(
                                    (int) Math.floor(pos.x / tileSize),
                                    (int) Math.floor(pos.y / tileSize));
                            return this.clientWorld.getBiome(tilePos)
                                    .map(biome -> "Biome: " + biome.getId())
                                    .orElse("Biome: unknown");
                        })
                        .position(8, 28)
                        .build());

        HUDManager.addLastElement(Identifier.of("debug_fps"),
                TextLabel.builder()
                        .textSupplier(() -> "FPS: %d, UPS: %d".formatted(this.fps, this.ups))
                        .position(8, 48)
                        .build());
    }

    private void updateLoadingScreen(float progress, String status) {
        LoadingWorldGUI screen = this.loadingScreen;
        if (screen == null)
            return;

        screen.setProgress(progress);
        screen.setStatus(status);
    }

    private void removeLoadingScreen() {
        if (!this.loadingScreenShown)
            return;

        GUI top = GUIStack.peek();
        if (top instanceof LoadingWorldGUI) {
            GUIStack.pop();
        }

        this.loadingScreenShown = false;
        this.loadingScreen = null;
    }

    private void removeMainMenuIfTop() {
        GUI top = GUIStack.peek();
        if (top instanceof MainMenuGUI) {
            GUIStack.pop();
        }
    }

    private void startGameWorld() {
        if (this.clientWorld != null || this.loadingWorld)
            return;

        this.loadingWorld = true;
        this.pendingWorldLoad.set(null);
        this.pendingWorldLoadError.set(null);

        if (!this.loadingScreenShown) {
            this.loadingScreen = new LoadingWorldGUI();
            this.loadingScreen.setProgress(0f);
            this.loadingScreen.setStatus("Preparing world...");
            GUIStack.push(this.loadingScreen);
            this.loadingScreenShown = true;
        }

        Thread worldLoader = new Thread(() -> {
            try {
                updateLoadingScreen(0.05f, "Generating chunks...");
                var integratedServer = new IntegratedServer();
                var overworld = new ServerWorld(WorldTypes.OVERWORLD);
                int totalChunks = 101 * 101;
                int chunksGenerated = 0;
                for (int chunkX = -50; chunkX <= 50; chunkX++) {
                    for (int chunkY = -50; chunkY <= 50; chunkY++) {
                        overworld.addChunk(new ChunkPos(chunkX, chunkY));
                        chunksGenerated++;
                        float progress = 0.05f + 0.6f * (chunksGenerated / (float) totalChunks);
                        updateLoadingScreen(progress, "Generating chunks...");
                    }
                }

                updateLoadingScreen(0.7f, "Exporting biome map...");
                Path biomeMapPath = Path.of("build", "biome_map.png");
                BiomeMapExporter.export(overworld, biomeMapPath);

                updateLoadingScreen(0.9f, "Finishing up...");
                integratedServer.addWorld(overworld);
                this.pendingWorldLoad.set(new WorldLoadResult(integratedServer, overworld));
                updateLoadingScreen(1.0f, "Starting world...");
            } catch (Throwable throwable) {
                this.pendingWorldLoadError.set(throwable);
                updateLoadingScreen(0f, "Failed to load world");
            }
        }, "world-loader");
        worldLoader.start();
    }

    private void openSettings() {
        GUIStack.pop();
        GUIStack.push(new SettingsGUI(() -> {
            GUIStack.pop();
            GUIStack.push(new MainMenuGUI(this::startGameWorld, this::openSettings));
        }));
    }

    private record WorldLoadResult(IntegratedServer integratedServer, ServerWorld overworld) {
    }
}
