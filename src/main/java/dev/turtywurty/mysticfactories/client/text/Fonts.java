package dev.turtywurty.mysticfactories.client.text;

/**
 * Simple holder for application fonts. Extend as additional fonts are needed.
 */
public final class Fonts {
    private static FontAtlas defaultFont;

    private Fonts() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void init() {
        if (defaultFont != null)
            return;

        defaultFont = new FontAtlas("/fonts/Roboto-Regular.ttf", 24);
    }

    public static FontAtlas defaultFont() {
        if (defaultFont == null)
            throw new IllegalStateException("Fonts.init() must be called before requesting fonts.");

        return defaultFont;
    }

    public static void cleanup() {
        if (defaultFont != null) {
            defaultFont.cleanup();
            defaultFont = null;
        }
    }
}
