package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.widget.Button;

import java.util.Objects;

/**
 * Simple main menu with a single "Play" button that starts the game world.
 */
public class MainMenuGUI extends GUI {
    private final Runnable onPlay;

    public MainMenuGUI(Runnable onPlay) {
        this.onPlay = Objects.requireNonNull(onPlay, "onPlay");
    }

    @Override
    protected void buildWidgets(int screenWidth, int screenHeight) {
        float buttonWidth = 180f;
        float buttonHeight = 42f;
        float x = (screenWidth - buttonWidth) * 0.5f;
        float y = (screenHeight - buttonHeight) * 0.5f;

        addWidget(new Button(x, y, buttonWidth, buttonHeight, Fonts.defaultFont(), "Play", btn -> this.onPlay.run()));
    }

    @Override
    public void render(DrawContext context) {
        renderBackground(context);
        super.render(context);
    }
}
