package dev.turtywurty.mysticfactories.settings;

public enum ResolutionPreset {
    R_1280x720(1280, 720),
    R_1920x1080(1920, 1080),
    R_2560x1440(2560, 1440),
    R_3840x2160(3840, 2160);

    private final int width;
    private final int height;

    ResolutionPreset(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
