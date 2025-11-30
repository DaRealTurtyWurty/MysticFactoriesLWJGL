package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.ui.widget.Button;
import dev.turtywurty.mysticfactories.client.ui.widget.WidgetList;
import dev.turtywurty.mysticfactories.client.window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

/**
 * Simple main menu with Play/Quit actions.
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
        float listX = (screenWidth - buttonWidth) * 0.5f;
        float listY = (screenHeight - (buttonHeight * 2 + 12f)) * 0.5f;

        var list = WidgetList.vertical()
                .position(listX, listY)
                .spacing(12f)
                .addChild(Button.builder()
                        .position(0, 0)
                        .size(buttonWidth, buttonHeight)
                        .text("Play")
                        .onClick(btn -> this.onPlay.run())
                        .build())
                .addChild(Button.builder()
                        .position(0, 0)
                        .size(buttonWidth, buttonHeight)
                        .text("Quit")
                        .onClick(btn -> GLFW.glfwSetWindowShouldClose(Window.getCurrentWindowId(), true))
                        .build())
                .build();

        addWidget(list);
    }

    @Override
    public void render(DrawContext context) {
        renderBackground(context);
        super.render(context);
    }
}
