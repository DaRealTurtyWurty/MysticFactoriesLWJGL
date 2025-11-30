package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Basic HUD text label that pulls its text from a supplier every frame.
 */
public class TextLabel extends Widget {
    private final FontAtlas font;
    private final Supplier<String> textSupplier;
    private final int color;

    public TextLabel(FontAtlas font, Supplier<String> textSupplier, float x, float y, int color) {
        super(x, y, 0f, 0f);
        this.font = Objects.requireNonNull(font, "font");
        this.textSupplier = Objects.requireNonNull(textSupplier, "textSupplier");
        this.color = color;
    }

    public TextLabel(FontAtlas font, Supplier<String> textSupplier, float x, float y) {
        this(font, textSupplier, x, y, 0xFFFFFFFF);
    }

    public TextLabel(FontAtlas font, String text, float x, float y, int color) {
        this(font, () -> text, x, y, color);
    }

    public TextLabel(FontAtlas font, String text, float x, float y) {
        this(font, () -> text, x, y, 0xFFFFFFFF);
    }

    @Override
    public void render(DrawContext context) {
        context.drawText(this.font, this.textSupplier.get(), getX(), getY(), this.color);
    }
}
