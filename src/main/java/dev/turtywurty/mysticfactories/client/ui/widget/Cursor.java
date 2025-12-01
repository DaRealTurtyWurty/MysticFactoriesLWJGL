package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class Cursor extends Widget {
    @Getter
    private FontAtlas font;
    @Setter
    @Getter
    private int color = 0xFFFFFFFF;

    @Getter
    private int blinkTimer = 0;
    @Getter
    private int blinkInterval = 60;

    protected Cursor(float x, float y, @NotNull FontAtlas font) {
        super(x, y, 2, font.getLineHeight());
        this.font = font;
    }

    @Override
    public void render(DrawContext context) {
        blinkTimer++;
        if (blinkTimer >= blinkInterval * 2) {
            blinkTimer = 0;
        }

        if (blinkTimer < blinkInterval)
            return;

        int color = ColorHelper.blendColors(this.color, 0x00000000, (float) blinkTimer / blinkInterval);
        context.drawRect(getX(), getY(), getWidth(), getHeight(), color);
    }

    public void setFont(@NotNull FontAtlas font) {
        this.font = font;
        setSize(getWidth(), font.getLineHeight());
    }

    public void setBlinkInterval(int blinkInterval) {
        this.blinkInterval = Math.max(1, blinkInterval);
    }
}
