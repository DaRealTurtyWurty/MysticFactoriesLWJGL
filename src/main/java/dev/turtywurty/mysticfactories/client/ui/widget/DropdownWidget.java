package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Generic dropdown widget with customizable rendering for selected value and options.
 */
public class DropdownWidget<T> extends Widget {
    private final Supplier<List<T>> optionsSupplier;
    private final Function<T, Widget> optionFactory;
    private final Function<T, Widget> selectedFactory;
    private final Consumer<T> onSelect;
    private final Comparator<T> comparator;
    private final int maxVisible;
    private final boolean scrollable;
    private final float optionHeight;
    private boolean open;
    private boolean openedThisFrame;
    private T selected;
    private Widget selectedView;
    private Widget optionsView;
    private boolean openAbove;
    private final int backgroundColor;
    private final int borderColor;
    private double lastMouseX;
    private double lastMouseY;
    private float lastScreenHeight;

    private DropdownWidget(Builder<T> builder) {
        super(builder.x, builder.y, builder.width, builder.height);
        this.optionsSupplier = Objects.requireNonNull(builder.optionsSupplier, "optionsSupplier");
        this.optionFactory = Objects.requireNonNull(builder.optionFactory, "optionFactory");
        this.selectedFactory = Objects.requireNonNull(builder.selectedFactory, "selectedFactory");
        this.onSelect = Objects.requireNonNull(builder.onSelect, "onSelect");
        this.comparator = builder.comparator;
        this.maxVisible = builder.maxVisible;
        this.scrollable = builder.scrollable;
        this.optionHeight = builder.optionHeight;
        this.selected = builder.initialSelected;
        this.backgroundColor = builder.backgroundColor;
        this.borderColor = builder.borderColor;
        rebuildSelectedView();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    private void rebuildSelectedView() {
        if (this.selected != null) {
            this.selectedView = this.selectedFactory.apply(this.selected);
            this.selectedView.setPosition(getX(), getY());
            this.selectedView.setSize(getWidth(), getHeight());
            this.selectedView.setVisible(isVisible());
            this.selectedView.setDisabled(isDisabled());
        }
    }

    private List<T> fetchOptions() {
        List<T> list = new ArrayList<>(this.optionsSupplier.get());
        if (this.comparator != null) {
            list.sort(this.comparator);
        }

        return list;
    }

    private void rebuildOptions() {
        List<T> options = fetchOptions();
        var listBuilder = ScrollList.builder()
                .position(getX(), 0)
                .size(getWidth(), Math.min(getHeight() * this.maxVisible, this.optionHeight * options.size()))
                .spacing(2f)
                .scrollSpeed(12f)
                .barWidth(this.scrollable ? 10f : 0f)
                .showScrollBar(this.scrollable && options.size() > this.maxVisible);

        for (T option : options) {
            Widget widget = this.optionFactory.apply(option);
            widget.setSize(getWidth(), this.optionHeight);
            Widget wrapper = new OptionEntry(widget, () -> {
                this.selected = option;
                this.onSelect.accept(option);
                rebuildSelectedView();
                this.open = false;
            });

            listBuilder.addChild(wrapper);
        }

        ScrollList list = listBuilder.build();
        list.setVisible(isVisible());
        list.setDisabled(isDisabled());
        float panelHeight = list.getHeight();
        float below = getY() + getHeight() + panelHeight;
        float screenHeight = this.lastScreenHeight > 0 ? this.lastScreenHeight : below + 1;
        this.openAbove = below > screenHeight;
        float panelY = this.openAbove ? getY() - panelHeight : getY() + getHeight();
        list.setPosition(getX(), panelY);
        this.optionsView = list;
    }

    @Override
    public void preRender(DrawContext context) {
        if (!isVisible())
            return;

        if (this.selectedView != null) {
            this.selectedView.preRender(context);
        }
    }

    @Override
    public void render(DrawContext context) {
        if (!isVisible())
            return;

        this.lastScreenHeight = context.height();
        int bg = isDisabled() ? ColorHelper.blendColors(this.backgroundColor, 0xFF000000, 0.35f) : this.backgroundColor;
        int border = isDisabled() ? ColorHelper.blendColors(this.borderColor, 0xFF000000, 0.35f) : this.borderColor;
        context.drawRect(getX(), getY(), getWidth(), getHeight(), bg);
        context.drawRect(getX(), getY(), getWidth(), 1f, border);
        context.drawRect(getX(), getY() + getHeight() - 1f, getWidth(), 1f, border);

        if (this.selectedView != null) {
            this.selectedView.render(context);
        }
    }

    @Override
    public void postRender(DrawContext context) {
        if (!isVisible())
            return;

        if (this.selectedView != null) {
            this.selectedView.postRender(context);
        }

        if (this.open && this.optionsView != null) {
            float panelX = this.optionsView.getX();
            float panelY = this.optionsView.getY();
            float panelWidth = this.optionsView.getWidth();
            float panelHeight = this.optionsView.getHeight();
            int panelBg = isDisabled() ? ColorHelper.blendColors(this.backgroundColor, 0xFF000000, 0.35f) : this.backgroundColor;
            int panelBorder = isDisabled() ? ColorHelper.blendColors(this.borderColor, 0xFF000000, 0.35f) : this.borderColor;
            context.drawRect(panelX, panelY, panelWidth, panelHeight, panelBg);
            context.drawRect(panelX, panelY, panelWidth, 1f, panelBorder);
            context.drawRect(panelX, panelY + panelHeight - 1f, panelWidth, 1f, panelBorder);
            context.drawRect(panelX, panelY, 1f, panelHeight, panelBorder);
            context.drawRect(panelX + panelWidth - 1f, panelY, 1f, panelHeight, panelBorder);
            this.optionsView.preRender(context);
            this.optionsView.render(context);
            this.optionsView.postRender(context);
        }
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        if (!isVisible() || isDisabled())
            return;

        this.lastMouseX = xPos;
        this.lastMouseY = yPos;
        if (this.selectedView != null) {
            this.selectedView.onMouseMove(xPos, yPos);
        }

        if (this.open && this.optionsView != null) {
            this.optionsView.onMouseMove(xPos, yPos);
        }
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return;

        if (action == GLFW.GLFW_PRESS) {
            if (containsPoint(this.lastMouseX, this.lastMouseY)) {
                toggleOpen();
                if (this.open) {
                    this.openedThisFrame = true;
                    rebuildOptions();
                }
            } else if (this.open && this.optionsView != null) {
                if (this.optionsView.containsPoint(lastMouseX, lastMouseY)) {
                    this.optionsView.onMouseButtonPress(button, action, modifiers);
                } else {
                    this.open = false;
                }
            }
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (this.open && !this.openedThisFrame && this.optionsView != null) {
            this.optionsView.onMouseButtonRelease(button, action, modifiers);
        }

        this.openedThisFrame = false;
    }

    @Override
    public void onMouseScroll(double xOffset, double yOffset) {
        if (!isVisible() || isDisabled())
            return;

        if (this.open && this.optionsView != null) {
            this.optionsView.onMouseScroll(xOffset, yOffset);
        }
    }

    @Override
    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (this.open && this.optionsView != null) {
            this.optionsView.onKeyPress(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        if (!isVisible() || isDisabled())
            return;

        if (this.open && this.optionsView != null) {
            this.optionsView.onKeyRelease(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void onUpdate(double deltaTime) {
        if (!isVisible() || isDisabled())
            return;

        if (this.selectedView != null) {
            this.selectedView.onUpdate(deltaTime);
        }

        if (this.open && this.optionsView != null) {
            this.optionsView.onUpdate(deltaTime);
        }
    }

    @Override
    public void setX(float x) {
        setPosition(x, getY());
    }

    @Override
    public void setY(float y) {
        setPosition(getX(), y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (this.selectedView != null) {
            this.selectedView.setPosition(x, y);
        }

        updateOptionsPosition();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if (this.selectedView != null) {
            this.selectedView.setSize(width, height);
        }

        updateOptionsPosition();
    }

    private void updateOptionsPosition() {
        if (this.optionsView != null) {
            float panelHeight = this.optionsView.getHeight();
            float panelY = this.openAbove ? getY() - panelHeight : getY() + getHeight();
            this.optionsView.setPosition(getX(), panelY);
        }
    }

    private void toggleOpen() {
        if (isDisabled()) {
            return;
        }

        this.open = !this.open;
        if (this.open && this.selectedView != null) {
            this.selectedView.setPosition(getX(), getY());
            this.selectedView.setSize(getWidth(), getHeight());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            this.open = false;
            this.openedThisFrame = false;
        }

        if (this.selectedView != null) {
            this.selectedView.setVisible(visible);
        }

        if (this.optionsView != null) {
            this.optionsView.setVisible(visible);
        }
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        if (disabled) {
            this.open = false;
            this.openedThisFrame = false;
        }

        if (this.selectedView != null) {
            this.selectedView.setDisabled(disabled);
        }

        if (this.optionsView != null) {
            this.optionsView.setDisabled(disabled);
        }
    }

    private static class OptionEntry extends Widget {
        private final Widget view;
        private final Runnable onClick;
        private double lastMouseX;
        private double lastMouseY;

        OptionEntry(Widget view, Runnable onClick) {
            super(view.getX(), view.getY(), view.getWidth(), view.getHeight());
            this.view = view;
            this.onClick = onClick;
            syncView();
        }

        @Override
        public void preRender(DrawContext context) {
            if (!isVisible())
                return;

            syncView();
            this.view.preRender(context);
        }

        @Override
        public void render(DrawContext context) {
            if (!isVisible())
                return;

            syncView();
            this.view.render(context);
        }

        @Override
        public void postRender(DrawContext context) {
            if (!isVisible())
                return;

            syncView();
            this.view.postRender(context);
        }

        @Override
        public void onMouseButtonRelease(int button, int action, int modifiers) {
            if (!isVisible() || isDisabled())
                return;

            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && containsPoint(this.lastMouseX, this.lastMouseY)) {
                this.onClick.run();
            }
        }

        @Override
        public void onMouseMove(double xPos, double yPos) {
            if (!isVisible() || isDisabled())
                return;

            this.lastMouseX = xPos;
            this.lastMouseY = yPos;
            this.view.onMouseMove(xPos, yPos);
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y);
            syncView();
        }

        @Override
        public void setSize(float width, float height) {
            super.setSize(width, height);
            syncView();
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            this.view.setVisible(visible);
        }

        @Override
        public void setDisabled(boolean disabled) {
            super.setDisabled(disabled);
            this.view.setDisabled(disabled);
        }

        private void syncView() {
            this.view.setPosition(getX(), getY());
            this.view.setSize(getWidth(), getHeight());
        }
    }

    public static class Builder<T> {
        private float x;
        private float y;
        private float width = 120f;
        private float height = 24f;
        private Supplier<List<T>> optionsSupplier = List::of;
        private Function<T, Widget> optionFactory;
        private Function<T, Widget> selectedFactory;
        private Consumer<T> onSelect = t -> {
        };
        private Comparator<T> comparator;
        private int maxVisible = 5;
        private boolean scrollable = true;
        private float optionHeight = 24f;
        private T initialSelected;
        private int backgroundColor = 0xFF2F2F2F;
        private int borderColor = 0xFF444444;

        public Builder<T> position(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder<T> size(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder<T> optionHeight(float optionHeight) {
            this.optionHeight = optionHeight;
            return this;
        }

        public Builder<T> options(Supplier<List<T>> supplier) {
            this.optionsSupplier = supplier;
            return this;
        }

        public Builder<T> optionFactory(Function<T, Widget> factory) {
            this.optionFactory = factory;
            return this;
        }

        public Builder<T> selectedFactory(Function<T, Widget> factory) {
            this.selectedFactory = factory;
            return this;
        }

        public Builder<T> onSelect(Consumer<T> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder<T> comparator(Comparator<T> comparator) {
            this.comparator = comparator;
            return this;
        }

        public Builder<T> maxVisible(int maxVisible) {
            this.maxVisible = maxVisible;
            return this;
        }

        public Builder<T> scrollable(boolean scrollable) {
            this.scrollable = scrollable;
            return this;
        }

        public Builder<T> initialSelected(T value) {
            this.initialSelected = value;
            return this;
        }

        public Builder<T> backgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder<T> borderColor(int color) {
            this.borderColor = color;
            return this;
        }

        public DropdownWidget<T> build() {
            if (this.optionFactory == null) {
                this.optionFactory = value -> defaultLabel(String.valueOf(value));
            }

            if (this.selectedFactory == null) {
                this.selectedFactory = value -> defaultLabel(String.valueOf(value));
            }

            DropdownWidget<T> widget = new DropdownWidget<>(this);
            widget.setPosition(this.x, this.y);
            widget.setSize(this.width, this.height);
            return widget;
        }

        private Widget defaultLabel(String text) {
            return TextLabel.builder().text(text).build();
        }
    }
}
