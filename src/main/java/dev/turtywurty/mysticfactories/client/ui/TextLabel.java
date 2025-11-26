package dev.turtywurty.mysticfactories.client.ui;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import org.joml.Vector4f;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Basic HUD text label that pulls its text from a supplier every frame.
 */
public class TextLabel implements UIElement {
    private final FontAtlas font;
    private final Supplier<String> textSupplier;
    private final float x;
    private final float y;
    private final Vector4f color;

    public TextLabel(FontAtlas font, Supplier<String> textSupplier, float x, float y, Vector4f color) {
        this.font = Objects.requireNonNull(font, "font");
        this.textSupplier = Objects.requireNonNull(textSupplier, "textSupplier");
        this.x = x;
        this.y = y;
        this.color = color == null ? new Vector4f(1f, 1f, 1f, 1f) : color;
    }

    public TextLabel(FontAtlas font, String text, float x, float y, Vector4f color) {
        this(font, () -> text, x, y, color);
    }

    @Override
    public void render(DrawContext context) {
        context.drawText(this.font, this.textSupplier.get(), this.x, this.y, this.color);
    }
}
