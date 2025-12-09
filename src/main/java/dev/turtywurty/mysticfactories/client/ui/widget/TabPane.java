package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple tabbed container widget. Renders a horizontal tab strip at the top and forwards input/rendering
 * to the currently selected tab content.
 */
public class TabPane extends Widget {
    private final List<Tab> tabs = new ArrayList<>();
    private final List<TabBounds> tabBounds = new ArrayList<>();
    private final FontAtlas font;

    private int selectedIndex = -1;
    private int hoveredIndex = -1;
    private double lastMouseX;
    private double lastMouseY;

    private float tabHeight = 28f;
    private float minTabWidth = 72f;
    private float tabHorizontalPadding = 12f;
    private float tabSpacing = 3f;
    private float contentPadding = 6f;

    private int backgroundColor = 0xFF101010;
    private int headerBackgroundColor = 0xFF1A1A1A;
    private int tabColor = 0xFF2A2A2A;
    private int tabHoverColor = 0xFF353535;
    private int tabSelectedColor = 0xFF3F3F3F;
    private int borderColor = 0xFF050505;
    private int textColor = 0xFFB5B5B5;
    private int selectedTextColor = 0xFFFFFFFF;

    private TabPane(float x, float y, float width, float height, FontAtlas font) {
        super(x, y, width, height);
        this.font = font == null ? Fonts.defaultFont() : font;
    }

    /**
     * @return builder for configuring a {@link TabPane}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Adds a new tab to the pane.
     */
    public void addTab(Tab tab) {
        if (tab == null)
            return;

        this.tabs.add(tab);
        if (this.selectedIndex == -1) {
            this.selectedIndex = 0;
        }
        relayoutTabs();
    }

    /**
     * Adds all provided tabs to the pane.
     */
    public void addTabs(Collection<Tab> newTabs) {
        if (newTabs == null || newTabs.isEmpty())
            return;

        for (Tab tab : newTabs) {
            addTab(tab);
        }
    }

    /**
     * Removes the provided tab, if present.
     */
    public void removeTab(Tab tab) {
        if (tab == null)
            return;

        int removedIndex = this.tabs.indexOf(tab);
        if (removedIndex >= 0) {
            this.tabs.remove(removedIndex);
            if (this.tabs.isEmpty()) {
                this.selectedIndex = -1;
            } else if (this.selectedIndex >= this.tabs.size()) {
                this.selectedIndex = this.tabs.size() - 1;
            } else if (removedIndex == this.selectedIndex) {
                this.selectedIndex = Math.max(0, removedIndex - 1);
            }
        }

        relayoutTabs();
    }

    /**
     * Clears all tabs and resets selection.
     */
    public void clearTabs() {
        this.tabs.clear();
        this.selectedIndex = -1;
        relayoutTabs();
    }

    /**
     * @return currently selected tab or {@code null} when no tabs exist.
     */
    public Tab getSelectedTab() {
        if (this.selectedIndex < 0 || this.selectedIndex >= this.tabs.size())
            return null;

        return this.tabs.get(this.selectedIndex);
    }

    /**
     * Selects the tab at the provided index, clamped to available tabs.
     */
    public void selectTab(int index) {
        if (this.tabs.isEmpty()) {
            this.selectedIndex = -1;
            return;
        }

        this.selectedIndex = Math.max(0, Math.min(index, this.tabs.size() - 1));
    }

    public int getTabCount() {
        return this.tabs.size();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        relayoutTabs();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        relayoutTabs();
    }

    @Override
    public void preRender(DrawContext context) {
        if (!isVisible())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible()) {
            active.preRender(context);
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!isVisible())
            return;

        float headerHeight = this.tabHeight;
        float contentY = getY() + headerHeight;
        float contentHeight = Math.max(0f, getHeight() - headerHeight);

        context.drawRect(getX(), getY(), getWidth(), headerHeight, this.headerBackgroundColor);
        context.drawRect(getX(), contentY, getWidth(), contentHeight, this.backgroundColor);
        context.drawLine(getX(), contentY, getX() + getWidth(), contentY, 1f, this.borderColor);

        rebuildTabBounds();
        this.hoveredIndex = findTabIndexAt(this.lastMouseX, this.lastMouseY);
        drawTabs(context);

        Widget active = getActiveContent();
        if (active != null && active.isVisible()) {
            active.render(context);
        }
    }

    @Override
    public void postRender(DrawContext context) {
        if (!isVisible())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible()) {
            active.postRender(context);
        }
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        if (!isVisible() || isDisabled())
            return;

        this.lastMouseX = xPos;
        this.lastMouseY = yPos;
        this.hoveredIndex = findTabIndexAt(xPos, yPos);

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onMouseMove(xPos, yPos);
        }
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
            int tabIndex = findTabIndexAt(this.lastMouseX, this.lastMouseY);
            if (tabIndex >= 0) {
                selectTab(tabIndex);
                return;
            }
        }

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onMouseButtonPress(button, action, modifiers);
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onMouseButtonRelease(button, action, modifiers);
        }
    }

    @Override
    public void onMouseScroll(double xOffset, double yOffset) {
        if (!isVisible() || isDisabled())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onMouseScroll(xOffset, yOffset);
        }
    }

    @Override
    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onKeyPress(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onKeyRelease(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onCharInput(int codepoint) {
        if (!isVisible() || isDisabled())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onCharInput(codepoint);
        }
    }

    @Override
    public void onUpdate(double deltaTime) {
        if (!isVisible() || isDisabled())
            return;

        Widget active = getActiveContent();
        if (active != null && active.isVisible() && !active.isDisabled()) {
            active.onUpdate(deltaTime);
        }
    }

    @Override
    public void cleanup() {
        for (Tab tab : this.tabs) {
            Widget content = tab.getContent();
            if (content != null) {
                content.cleanup();
            }
        }
    }

    private void drawTabs(DrawContext context) {
        float textBaseline = getY() + (this.tabHeight - this.font.getLineHeight()) * 0.5f;
        for (int i = 0; i < this.tabs.size(); i++) {
            Tab tab = this.tabs.get(i);
            TabBounds bounds = this.tabBounds.get(i);
            boolean selected = i == this.selectedIndex;
            boolean hovered = i == this.hoveredIndex;

            int fill = selected ? this.tabSelectedColor : hovered ? this.tabHoverColor : this.tabColor;
            int drawColor = selected ? this.selectedTextColor : this.textColor;

            context.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, fill);
            context.drawLine(bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height, 1f, this.borderColor);

            float textWidth = this.font.measureTextWidth(tab.getTitle());
            float textX = bounds.x + Math.max(this.tabHorizontalPadding, (bounds.width - textWidth) * 0.5f);
            context.drawText(this.font, tab.getTitle(), textX, textBaseline, drawColor);
        }
    }

    private void rebuildTabBounds() {
        this.tabBounds.clear();
        float cursorX = getX();
        float availableWidth = getWidth();

        for (Tab tab : this.tabs) {
            float textWidth = this.font.measureTextWidth(tab.getTitle());
            float desiredWidth = Math.max(this.minTabWidth, textWidth + this.tabHorizontalPadding * 2f);
            float remaining = availableWidth - (cursorX - getX());
            float tabWidth = Math.min(desiredWidth, Math.max(this.minTabWidth, remaining));

            this.tabBounds.add(new TabBounds(cursorX, getY(), tabWidth, this.tabHeight));
            cursorX += tabWidth + this.tabSpacing;
        }
    }

    private int findTabIndexAt(double mouseX, double mouseY) {
        if (mouseY < getY() || mouseY > getY() + this.tabHeight)
            return -1;

        for (int i = 0; i < this.tabBounds.size(); i++) {
            if (this.tabBounds.get(i).contains(mouseX, mouseY)) {
                return i;
            }
        }

        return -1;
    }

    private Widget getActiveContent() {
        Tab tab = getSelectedTab();
        return tab == null ? null : tab.getContent();
    }

    private void relayoutTabs() {
        if (this.tabs.isEmpty()) {
            this.tabBounds.clear();
            return;
        }

        float contentX = getX() + this.contentPadding;
        float contentY = getY() + this.tabHeight + this.contentPadding;
        float contentWidth = Math.max(0f, getWidth() - this.contentPadding * 2f);
        float contentHeight = Math.max(0f, getHeight() - this.tabHeight - this.contentPadding * 2f);

        for (Tab tab : this.tabs) {
            Widget content = tab.getContent();
            if (content != null) {
                content.setPosition(contentX, contentY);
                content.setSize(contentWidth, contentHeight);
            }
        }
    }

    private static final class TabBounds {
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        private TabBounds(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        private boolean contains(double px, double py) {
            return px >= this.x && px <= this.x + this.width && py >= this.y && py <= this.y + this.height;
        }
    }

    public static final class Builder {
        private final List<Tab> tabs = new ArrayList<>();
        private float x;
        private float y;
        private float width;
        private float height;
        private FontAtlas font = Fonts.defaultFont();
        private int selectedIndex = 0;

        private float tabHeight = 28f;
        private float tabSpacing = 3f;
        private float minTabWidth = 72f;
        private float tabHorizontalPadding = 12f;
        private float contentPadding = 6f;

        private int backgroundColor = 0xFF101010;
        private int headerBackgroundColor = 0xFF1A1A1A;
        private int tabColor = 0xFF2A2A2A;
        private int tabHoverColor = 0xFF353535;
        private int tabSelectedColor = 0xFF3F3F3F;
        private int borderColor = 0xFF050505;
        private int textColor = 0xFFB5B5B5;
        private int selectedTextColor = 0xFFFFFFFF;

        private Builder() {
        }

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

        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        public Builder tabHeight(float tabHeight) {
            this.tabHeight = tabHeight;
            return this;
        }

        public Builder tabSpacing(float tabSpacing) {
            this.tabSpacing = tabSpacing;
            return this;
        }

        public Builder minTabWidth(float minTabWidth) {
            this.minTabWidth = minTabWidth;
            return this;
        }

        public Builder tabHorizontalPadding(float padding) {
            this.tabHorizontalPadding = padding;
            return this;
        }

        public Builder contentPadding(float padding) {
            this.contentPadding = padding;
            return this;
        }

        public Builder backgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder headerBackgroundColor(int color) {
            this.headerBackgroundColor = color;
            return this;
        }

        public Builder tabColor(int color) {
            this.tabColor = color;
            return this;
        }

        public Builder tabHoverColor(int color) {
            this.tabHoverColor = color;
            return this;
        }

        public Builder tabSelectedColor(int color) {
            this.tabSelectedColor = color;
            return this;
        }

        public Builder borderColor(int color) {
            this.borderColor = color;
            return this;
        }

        public Builder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public Builder selectedTextColor(int color) {
            this.selectedTextColor = color;
            return this;
        }

        public Builder selectedIndex(int index) {
            this.selectedIndex = index;
            return this;
        }

        public Builder tabs(Collection<Tab> tabs) {
            if (tabs != null) {
                this.tabs.addAll(tabs);
            }

            return this;
        }

        public Builder addTab(Tab tab) {
            if (tab != null) {
                this.tabs.add(tab);
            }

            return this;
        }

        public TabPane build() {
            var pane = new TabPane(this.x, this.y, this.width, this.height, this.font);
            pane.tabHeight = this.tabHeight;
            pane.tabSpacing = this.tabSpacing;
            pane.minTabWidth = this.minTabWidth;
            pane.tabHorizontalPadding = this.tabHorizontalPadding;
            pane.contentPadding = this.contentPadding;

            pane.backgroundColor = this.backgroundColor;
            pane.headerBackgroundColor = this.headerBackgroundColor;
            pane.tabColor = this.tabColor;
            pane.tabHoverColor = this.tabHoverColor;
            pane.tabSelectedColor = this.tabSelectedColor;
            pane.borderColor = this.borderColor;
            pane.textColor = this.textColor;
            pane.selectedTextColor = this.selectedTextColor;

            pane.addTabs(this.tabs);
            pane.selectTab(this.selectedIndex);
            pane.relayoutTabs();
            return pane;
        }
    }
}
