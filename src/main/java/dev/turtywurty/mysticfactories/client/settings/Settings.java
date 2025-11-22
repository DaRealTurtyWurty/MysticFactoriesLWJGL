package dev.turtywurty.mysticfactories.client.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
    private static final Path SETTINGS_PATH = getSettingsPath();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Setter
    @Getter
    private FullscreenMode fullscreenMode = FullscreenMode.WINDOWED;
    @Getter
    private ResolutionPreset resolutionPreset = ResolutionPreset.R_1280x720;
    private int windowWidth = resolutionPreset.getWidth();
    private int windowHeight = resolutionPreset.getHeight();
    @Setter
    @Getter
    private boolean vsync = true;
    @Setter
    @Getter
    private int fpsCap = 60;

    public void save() {
        try {
            Files.createDirectories(SETTINGS_PATH.getParent());
            Files.writeString(SETTINGS_PATH, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Settings load() {
        if (Files.exists(SETTINGS_PATH)) {
            try {
                String json = Files.readString(SETTINGS_PATH, StandardCharsets.UTF_8);
                return GSON.fromJson(json, Settings.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        var defaultSettings = new Settings();
        defaultSettings.save();
        return defaultSettings;
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
        return this.resolutionPreset != null ? this.resolutionPreset.getWidth() : this.windowWidth;
    }

    public int getWindowHeight() {
        return this.resolutionPreset != null ? this.resolutionPreset.getHeight() : this.windowHeight;
    }

    public void setResolutionPreset(ResolutionPreset resolutionPreset) {
        this.resolutionPreset = resolutionPreset;
        if (resolutionPreset != null) {
            this.windowWidth = resolutionPreset.getWidth();
            this.windowHeight = resolutionPreset.getHeight();
        }
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
        this.resolutionPreset = null;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
        this.resolutionPreset = null;
    }
}
