package dev.turtywurty.mysticfactories.client.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.turtywurty.mysticfactories.util.GsonReader;
import dev.turtywurty.mysticfactories.util.ObservableProperty;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
    private static final Path SETTINGS_PATH = getSettingsPath();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Settings INSTANCE = load();

    private final ObservableProperty<FullscreenMode> fullscreenMode = new ObservableProperty<>(FullscreenMode.WINDOWED);
    private final ObservableProperty<ResolutionPreset> resolutionPreset = new ObservableProperty<>(ResolutionPreset.R_1280x720);
    private final ObservableProperty<Integer> windowWidth = new ObservableProperty<>(ResolutionPreset.R_1280x720.getWidth());
    private final ObservableProperty<Integer> windowHeight = new ObservableProperty<>(ResolutionPreset.R_1280x720.getHeight());
    private final ObservableProperty<Boolean> vsync = new ObservableProperty<>(true);
    private final ObservableProperty<Integer> fpsCap = new ObservableProperty<>(60);

    private Settings() {
    }

    public static Settings getInstance() {
        return INSTANCE;
    }

    public void save() {
        try {
            Files.createDirectories(SETTINGS_PATH.getParent());
            writeJson(SETTINGS_PATH);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static Settings load() {
        if (Files.exists(SETTINGS_PATH)) {
            try {
                var settings = new Settings();
                settings.readJson(SETTINGS_PATH);
                return settings;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        var defaults = new Settings();
        defaults.save();
        return defaults;
    }

    public void writeJson(Path path) throws IOException {
        var data = new JsonObject();
        data.addProperty("FullscreenMode", this.fullscreenMode.get().name());
        data.addProperty("ResolutionPreset", this.resolutionPreset.get() != null ? this.resolutionPreset.get().name() : "CUSTOM");
        data.addProperty("WindowWidth", this.windowWidth.get());
        data.addProperty("WindowHeight", this.windowHeight.get());
        data.addProperty("VSync", this.vsync.get());
        data.addProperty("FpsCap", this.fpsCap.get());
        Files.writeString(path, GSON.toJson(data), StandardCharsets.UTF_8);
    }

    public void readJson(Path path) throws IOException {
        String json = Files.readString(path, StandardCharsets.UTF_8);
        JsonObject data = GSON.fromJson(json, JsonObject.class);
        this.fullscreenMode.set(GsonReader.readEnumSafe(data, "FullscreenMode", FullscreenMode.class, FullscreenMode.WINDOWED));
        this.resolutionPreset.set(GsonReader.readEnumSafe(data, "ResolutionPreset", ResolutionPreset.class, null));
        this.windowWidth.set(GsonReader.readIntSafe(data, "WindowWidth", ResolutionPreset.R_1280x720.getWidth()));
        this.windowHeight.set(GsonReader.readIntSafe(data, "WindowHeight", ResolutionPreset.R_1280x720.getHeight()));
        this.vsync.set(GsonReader.readBooleanSafe(data, "VSync", true));
        this.fpsCap.set(GsonReader.readIntSafe(data, "FpsCap", 60));
    }

    private static Path getSettingsPath() {
        String os = System.getProperty("os.name").toLowerCase();
        Path settingsDir;

        if (os.contains("win")) {
            settingsDir = Paths.get(System.getenv("APPDATA"), "MysticFactories");
        } else if (os.contains("mac")) {
            settingsDir = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "MysticFactories");
        } else {
            settingsDir = Paths.get(System.getProperty("user.home"), ".config", "MysticFactories");
        }

        return settingsDir.resolve("settings.json");
    }

    public int getWindowWidth() {
        ResolutionPreset preset = this.resolutionPreset.get();
        return preset != null ? preset.getWidth() : this.windowWidth.get();
    }

    public int getWindowHeight() {
        ResolutionPreset preset = this.resolutionPreset.get();
        return preset != null ? preset.getHeight() : this.windowHeight.get();
    }

    public void setResolutionPreset(ResolutionPreset preset) {
        this.resolutionPreset.set(preset);
        if (preset != null) {
            this.windowWidth.set(preset.getWidth());
            this.windowHeight.set(preset.getHeight());
        }
    }

    public void setWindowWidth(int width) {
        this.windowWidth.set(width);
        this.resolutionPreset.set(null);
    }

    public void setWindowHeight(int height) {
        this.windowHeight.set(height);
        this.resolutionPreset.set(null);
    }

    public FullscreenMode getFullscreenMode() {
        return this.fullscreenMode.get();
    }

    public void setFullscreenMode(FullscreenMode mode) {
        this.fullscreenMode.set(mode);
    }

    public ObservableProperty<FullscreenMode> fullscreenModeProperty() {
        return this.fullscreenMode;
    }

    public ResolutionPreset getResolutionPreset() {
        return this.resolutionPreset.get();
    }

    public ObservableProperty<ResolutionPreset> resolutionPresetProperty() {
        return this.resolutionPreset;
    }

    public ObservableProperty<Integer> windowWidthProperty() {
        return this.windowWidth;
    }

    public ObservableProperty<Integer> windowHeightProperty() {
        return this.windowHeight;
    }

    public boolean isVsync() {
        return this.vsync.get();
    }

    public void setVsync(boolean value) {
        this.vsync.set(value);
    }

    public ObservableProperty<Boolean> vsyncProperty() {
        return this.vsync;
    }

    public int getFpsCap() {
        return this.fpsCap.get();
    }

    public void setFpsCap(int fps) {
        this.fpsCap.set(fps);
    }

    public ObservableProperty<Integer> fpsCapProperty() {
        return this.fpsCap;
    }
}
