package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.ui.UIElement;
import lombok.Getter;

@Getter
public class Tab extends Widget {
    private UIElement graphic;
    private String title;
    private Widget content;

    protected Tab(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void preRender(DrawContext context) {

    }

    @Override
    public void render(DrawContext context) {

    }

    @Override
    public void postRender(DrawContext context) {

    }
}
