package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple clickable button widget.
 */
public class Button extends AbstractButton {
    private final FontAtlas font;
    private final String text;
    private final Consumer<Button> onClick;

    private int backgroundColor = 0xFF2F2F2F;
    private int hoverColor = 0xFF3A3A3A;
    private int pressedColor = 0xFF232323;
    private int textColor = 0xFFFFFFFF;

    public Button(float x, float y, float width, float height, FontAtlas font, String text, Consumer<Button> onClick) {
        super(x, y, width, height);
        this.font = Objects.requireNonNull(font, "font");
        this.text = Objects.requireNonNull(text, "text");
        this.onClick = Objects.requireNonNull(onClick, "onClick");
    }

    public Button setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public Button setHoverColor(int color) {
        this.hoverColor = color;
        return this;
    }

    public Button setPressedColor(int color) {
        this.pressedColor = color;
        return this;
    }

    public Button setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    @Override
    protected void renderButton(DrawContext context, boolean hovered, boolean pressed) {
        int fill = pressed ? this.pressedColor : hovered ? this.hoverColor : this.backgroundColor;
        context.drawRect(getX(), getY(), getWidth(), getHeight(), fill);

        float textWidth = this.font.measureTextWidth(this.text);
        float textX = getX() + (getWidth() - textWidth) * 0.5f;
        float textY = getY() + (getHeight() - this.font.getLineHeight()) * 0.5f;
        context.drawText(this.font, this.text, textX, textY, this.textColor);
    }

    @Override
    protected void onClick() {
        this.onClick.accept(this);
    }
}
