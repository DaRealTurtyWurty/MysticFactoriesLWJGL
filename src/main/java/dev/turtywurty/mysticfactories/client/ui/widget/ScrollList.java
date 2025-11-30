package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Vertical list that supports scrolling when content exceeds its bounds.
 */
public class ScrollList extends Widget {
    private final List<Widget> children = new ArrayList<>();
    private final ScrollBar scrollBar;
    private final float barWidth;
    private float spacing;
    private float scrollOffset;
    private boolean scrollNeeded;
    @Setter
    private boolean showScrollBar = true;
    @Setter
    private float scrollSpeed = 16f;

    protected ScrollList(float x, float y, float width, float height, float spacing, float barWidth, Collection<Widget> initialChildren) {
        super(x, y, width, height);
        this.spacing = spacing;
        this.barWidth = barWidth;
        if (initialChildren != null) {
            this.children.addAll(initialChildren);
        }

        this.scrollBar = new ScrollBar(x + width - barWidth, y, barWidth, height, this::onScrollFractionChanged);
        updateLayout();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void addChild(Widget child) {
        if (child == null)
            return;

        this.children.add(child);
        updateLayout();
    }

    public void setChildren(Collection<Widget> newChildren) {
        this.children.clear();
        if (newChildren != null) {
            this.children.addAll(newChildren);
        }

        updateLayout();
    }

    public List<Widget> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        updateLayout();
    }

    private void onScrollFractionChanged(float fraction) {
        float overflow = contentHeight() - getHeight();
        if (overflow <= 0) {
            this.scrollOffset = 0f;
        } else {
            this.scrollOffset = -fraction * overflow;
        }

        updateLayout();
    }

    private float contentHeight() {
        if (this.children.isEmpty())
            return 0f;

        float total = -this.spacing;
        for (Widget child : this.children) {
            total += child.getHeight() + this.spacing;
        }

        return total;
    }

    private void clampScroll() {
        float maxOffset = 0f;
        float minOffset = Math.min(0f, getHeight() - contentHeight());
        if (this.scrollOffset > maxOffset) {
            this.scrollOffset = maxOffset;
        } else if (this.scrollOffset < minOffset) {
            this.scrollOffset = minOffset;
        }
    }

    private void updateLayout() {
        float contentHeight = contentHeight();
        this.scrollNeeded = contentHeight > getHeight();
        clampScroll();

        float currentY = getY() + this.scrollOffset;
        for (Widget child : this.children) {
            child.setX(getX());
            child.setY(currentY);
            currentY += child.getHeight() + this.spacing;
        }

        this.scrollBar.setPosition(getX() + getWidth() - this.barWidth, getY());
        this.scrollBar.setSize(this.barWidth, getHeight());

        if (this.scrollNeeded) {
            float visibleRatio = getHeight() / contentHeight;
            this.scrollBar.setKnobHeight(Math.max(8f, getHeight() * visibleRatio));
            float overflow = contentHeight - getHeight();
            float fraction = overflow > 0 ? -this.scrollOffset / overflow : 0f;
            this.scrollBar.setFraction(fraction);
        } else {
            this.scrollBar.setKnobHeight(getHeight());
            this.scrollBar.setFraction(0f);
            this.scrollOffset = 0f;
        }
    }

    @Override
    public void render(DrawContext context) {
        for (Widget child : this.children) {
            if (child.getY() + child.getHeight() < getY() || child.getY() > getY() + getHeight())
                continue; // skip anything outside viewport

            child.render(context);
        }

        if (this.scrollNeeded && this.showScrollBar) {
            this.scrollBar.render(context);
        }
    }

    @Override
    public void onMouseScroll(double xOffset, double yOffset) {
        if (this.scrollNeeded) {
            this.scrollOffset -= (float) (yOffset * this.scrollSpeed);
            updateLayout();
            if (this.showScrollBar) {
                this.scrollBar.onMouseScroll(xOffset, yOffset);
            }
        }

        for (Widget child : this.children) {
            child.onMouseScroll(xOffset, yOffset);
        }
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        for (Widget child : this.children) {
            child.onMouseMove(xPos, yPos);
        }

        if (this.scrollNeeded && this.showScrollBar) {
            this.scrollBar.onMouseMove(xPos, yPos);
        }
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        for (Widget child : this.children) {
            child.onMouseButtonPress(button, action, modifiers);
        }

        if (this.scrollNeeded && this.showScrollBar) {
            this.scrollBar.onMouseButtonPress(button, action, modifiers);
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        for (Widget child : this.children) {
            child.onMouseButtonRelease(button, action, modifiers);
        }

        if (this.scrollNeeded && this.showScrollBar) {
            this.scrollBar.onMouseButtonRelease(button, action, modifiers);
        }
    }

    @Override
    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        for (Widget child : this.children) {
            child.onKeyPress(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        for (Widget child : this.children) {
            child.onKeyRelease(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onUpdate(double deltaTime) {
        for (Widget child : this.children) {
            child.onUpdate(deltaTime);
        }

        if (this.scrollNeeded && this.showScrollBar) {
            this.scrollBar.onUpdate(deltaTime);
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

    public static class Builder {
        private float x;
        private float y;
        private float width;
        private float height;
        private float spacing = 4f;
        private float scrollSpeed = 16f;
        private float barWidth = 10f;
        private boolean showScrollBar = true;
        private final List<Widget> children = new ArrayList<>();

        public Builder position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder spacing(float spacing) {
            this.spacing = spacing;
            return this;
        }

        public Builder scrollSpeed(float scrollSpeed) {
            this.scrollSpeed = scrollSpeed;
            return this;
        }

        public Builder barWidth(float barWidth) {
            this.barWidth = barWidth;
            return this;
        }

        public Builder showScrollBar(boolean show) {
            this.showScrollBar = show;
            return this;
        }

        public Builder addChild(Widget widget) {
            if (widget != null) {
                this.children.add(widget);
            }

            return this;
        }

        public ScrollList build() {
            var list = new ScrollList(this.x, this.y, this.width, this.height, this.spacing, this.barWidth, this.children);
            list.setScrollSpeed(this.scrollSpeed);
            list.setShowScrollBar(this.showScrollBar);
            return list;
        }
    }
}
