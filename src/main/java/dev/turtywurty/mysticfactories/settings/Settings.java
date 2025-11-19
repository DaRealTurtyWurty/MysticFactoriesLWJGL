package dev.turtywurty.mysticfactories.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
    private static final Path SETTINGS_PATH = getSettingsPath();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private FullscreenMode fullscreenMode = FullscreenMode.WINDOWED;
    private ResolutionPreset resolutionPreset = ResolutionPreset.R_1280x720;
    private int width = resolutionPreset.getWidth();
    private int height = resolutionPreset.getHeight();
    private boolean vsync = true;
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

    public FullscreenMode getFullscreenMode() {
        return this.fullscreenMode;
    }
    
    public int getWidth() {
        return this.resolutionPreset != null ? this.resolutionPreset.getWidth() : this.width;
    }

    public int getHeight() {
        return this.resolutionPreset != null ? this.resolutionPreset.getHeight() : this.height;
    }

    public boolean isVsync() {
        return this.vsync;
    }

    public int getFpsCap() {
        return this.fpsCap;
    }

    public void setFullscreenMode(FullscreenMode fullscreenMode) {
        this.fullscreenMode = fullscreenMode;
    }
    
    public ResolutionPreset getResolutionPreset() {
        return this.resolutionPreset;
    }
    
    public void setResolutionPreset(ResolutionPreset resolutionPreset) {
        this.resolutionPreset = resolutionPreset;
        if (resolutionPreset != null) {
            this.width = resolutionPreset.getWidth();
            this.height = resolutionPreset.getHeight();
        }
    }

    public void setWidth(int width) {
        this.width = width;
        this.resolutionPreset = null;
    }

    public void setHeight(int height) {
        this.height = height;
        this.resolutionPreset = null;
    }
    
    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public void setFpsCap(int fpsCap) {
        this.fpsCap = fpsCap;
    }
}
