package dev.turtywurty.mysticfactories.client.ui.impl;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.ui.GUI;
import dev.turtywurty.mysticfactories.client.ui.widget.ProgressBar;
import lombok.Setter;

public class LoadingWorldGUI extends GUI {
    private volatile float progress;
    @Setter
    private volatile String status = "Preparing world...";
    private ProgressBar progressBar;

    @Override
    protected void buildWidgets(int screenWidth, int screenHeight) {
        float centerX = screenWidth * 0.5f;
        float centerY = screenHeight * 0.5f;
        float barWidth = 220f;
        float barHeight = 18f;
        float barX = centerX - barWidth * 0.5f;
        float barY = centerY - barHeight * 0.5f;

        this.progressBar = ProgressBar.builder()
                .position(barX, barY)
                .size(barWidth, barHeight)
                .progress(this.progress)
                .label(() -> "%d%%".formatted(Math.round(Math.max(0f, Math.min(1f, this.progress)) * 100f)))
                .build();

        addWidget(this.progressBar);
    }

    @Override
    public void render(DrawContext context) {
        context.drawRect(0, 0, context.width(), context.height(), 0xFF222222);

        float centerX = context.width() * 0.5f;
        float centerY = context.height() * 0.5f;
        float barWidth = this.progressBar != null ? this.progressBar.getWidth() : 220f;
        float barHeight = this.progressBar != null ? this.progressBar.getHeight() : 18f;
        float barX = centerX - barWidth * 0.5f;
        float barY = centerY - barHeight * 0.5f;
        FontAtlas font = Fonts.defaultFont();

        int boxWidth = (int) (Math.max(font.measureTextWidth("Loading World..."), font.measureTextWidth(this.status)) + 40);
        boxWidth = Math.max(boxWidth, (int) barWidth + 16);

        context.drawCenteredRect(centerX, barY + 8, boxWidth + 4, 116, 0xFF000000);
        context.drawCenteredRect(centerX, barY + 8, boxWidth, 112, 0xFF333333);

        if (this.progressBar != null) {
            this.progressBar.setPosition(barX, barY);
        }

        context.drawCenteredText(font,
                "Loading World...",
                centerX, barY - 36,
                0xFFFFFFFF);

        super.render(context);

        context.drawCenteredText(font,
                this.status,
                centerX, barY + barHeight + 20,
                0xFFDDDDDD);
    }

    @Override
    public boolean shouldPauseGame() {
        return true;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (this.progressBar != null) {
            this.progressBar.setProgress(progress);
        }
    }
}
