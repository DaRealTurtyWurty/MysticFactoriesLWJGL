package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Simple linear container that stacks child widgets horizontally or vertically with spacing.
 */
public class WidgetList extends Widget {
    private final List<Widget> children = new ArrayList<>();
    @Getter
    private float spacing;
    @Getter
    private Orientation orientation;

    protected WidgetList(float x, float y, float width, float height, float spacing, Orientation orientation, Widget... initialChildren) {
        super(x, y, width, height);
        this.spacing = spacing;
        this.orientation = orientation == null ? Orientation.VERTICAL : orientation;
        Collections.addAll(children, initialChildren);
        updateLayout();
    }

    /**
     * @return builder preconfigured for vertical stacking.
     */
    public static Builder vertical() {
        return builder().orientation(Orientation.VERTICAL);
    }

    /**
     * @return builder preconfigured for horizontal stacking.
     */
    public static Builder horizontal() {
        return builder().orientation(Orientation.HORIZONTAL);
    }

    /**
     * @return base builder for a widget list.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Sets spacing in pixels between children.
     */
    public void setSpacing(float spacing) {
        this.spacing = spacing;
        updateLayout();
    }

    /**
     * Adds a child and reflows the layout.
     */
    public void addChild(Widget child) {
        if (child == null)
            return;

        children.add(child);
        updateLayout();
    }

    /**
     * Removes a child and reflows the layout.
     */
    public void removeChild(Widget child) {
        if (child == null)
            return;

        children.remove(child);
        updateLayout();
    }

    /**
     * Clears all children.
     */
    public void clearChildren() {
        children.clear();
        updateLayout();
    }

    public List<Widget> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public int getChildCount() {
        return children.size();
    }

    /**
     * Replaces children with the provided collection.
     */
    public void setChildren(Collection<Widget> newChildren) {
        children.clear();
        if (newChildren != null) {
            children.addAll(newChildren);
        }

        updateLayout();
    }

    /**
     * Replaces children with the provided array.
     */
    public void setChildren(Widget... newChildren) {
        setChildren(List.of(newChildren));
    }

    /**
     * Sets layout orientation and reflows children.
     */
    public void setOrientation(Orientation orientation) {
        if (orientation == null || this.orientation == orientation)
            return;

        this.orientation = orientation;
        updateLayout();
    }

    private void updateLayout() {
        float cursorX = getX();
        float cursorY = getY();
        for (Widget child : children) {
            child.setX(cursorX);
            child.setY(cursorY);
            if (this.orientation == Orientation.VERTICAL) {
                cursorY += child.getHeight() + spacing;
            } else {
                cursorX += child.getWidth() + spacing;
            }
        }
    }

    @Override
    public void preRender(DrawContext context) {
        if (!isVisible())
            return;

        for (Widget child : children) {
            if (child.isVisible()) {
                child.preRender(context);
            }
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!isVisible())
            return;

        for (Widget child : children) {
            if (child.isVisible()) {
                child.render(context);
            }
        }
    }

    @Override
    public void postRender(DrawContext context) {
        if (!isVisible())
            return;

        for (Widget child : children) {
            if (child.isVisible()) {
                child.postRender(context);
            }
        }
    }

    /**
     * Forwards key press events to all children.
     */
    @Override
    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        for (Widget child : children) {
            if (child.isVisible() && !child.isDisabled()) {
                child.onKeyPress(keyCode, scanCode, modifiers);
            }
        }
    }

    /**
     * Forwards key release events to all children.
     */
    @Override
    public void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        for (Widget child : children) {
            if (child.isVisible() && !child.isDisabled()) {
                child.onKeyRelease(keyCode, scanCode, modifiers);
            }
        }
    }

    /**
     * Forwards mouse move events to all children.
     */
    @Override
    public void onMouseMove(double xPos, double yPos) {
        if (!isVisible() || isDisabled())
            return;

        for (Widget child : children) {
            if (child.isVisible() && !child.isDisabled()) {
                child.onMouseMove(xPos, yPos);
            }
        }
    }

    /**
     * Forwards scroll events to all children.
     */
    @Override
    public void onMouseScroll(double xOffset, double yOffset) {
        if (!isVisible() || isDisabled())
            return;

        for (Widget child : children) {
            if (child.isVisible() && !child.isDisabled()) {
                child.onMouseScroll(xOffset, yOffset);
            }
        }
    }

    /**
     * Forwards mouse press events to all children.
     */
    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        for (Widget child : children) {
            if (child.isVisible() && !child.isDisabled()) {
                child.onMouseButtonPress(button, action, modifiers);
            }
        }
    }

    /**
     * Forwards mouse release events to all children.
     */
    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        for (Widget child : children) {
            if (child.isVisible() && !child.isDisabled()) {
                child.onMouseButtonRelease(button, action, modifiers);
            }
        }
    }

    /**
     * Forwards update events to all children.
     */
    @Override
    public void onUpdate(double deltaTime) {
        if (!isVisible() || isDisabled())
            return;

        for (Widget child : children) {
            if (child.isVisible() && !child.isDisabled()) {
                child.onUpdate(deltaTime);
            }
        }
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        updateLayout();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        updateLayout();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateLayout();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateLayout();
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        for (Widget child : this.children) {
            child.setDisabled(disabled);
        }
    }

    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    public static class Builder {
        private float x;
        private float y;
        private float width;
        private float height;
        private float spacing = 0;
        private Orientation orientation = Orientation.VERTICAL;
        private final List<Widget> children = new ArrayList<>();

        /**
         * Top-left position in screen space.
         */
        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Size in pixels.
         */
        public Builder size(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Spacing between children in pixels.
         */
        public Builder spacing(float spacing) {
            this.spacing = spacing;
            return this;
        }

        /**
         * Layout orientation.
         */
        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        /**
         * Adds a child to the list.
         */
        public Builder addChild(Widget child) {
            if (child != null) {
                children.add(child);
            }

            return this;
        }

        public WidgetList build() {
            return new WidgetList(x, y, width, height, spacing, orientation, children.toArray(new Widget[0]));
        }
    }
}
