package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import dev.turtywurty.mysticfactories.client.util.ColorHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Simple horizontal slider used for fake settings controls. Draws a track, fill, knob and value label.
 */
public class SliderWidget extends Widget {
    private final float min;
    private final float max;
    private final float step;
    private final Consumer<Float> onValueChanged;
    private final Function<Float, String> valueFormatter;
    private final FontAtlas font;

    private final int trackColor;
    private final int fillColor;
    private final int knobColor;
    private final int textColor;

    private float value;
    private boolean dragging;
    private double lastMouseX;
    private double lastMouseY;

    private SliderWidget(Builder builder) {
        super(builder.x, builder.y, builder.width, builder.height);
        this.min = builder.min;
        this.max = Math.max(builder.min, builder.max);
        this.step = builder.step <= 0f ? 0f : builder.step;
        this.value = clampAndQuantize(builder.initialValue);
        this.onValueChanged = Objects.requireNonNull(builder.onValueChanged, "onValueChanged");
        this.valueFormatter = Objects.requireNonNull(builder.valueFormatter, "valueFormatter");
        this.font = builder.font == null ? Fonts.defaultFont() : builder.font;
        this.trackColor = builder.trackColor;
        this.fillColor = builder.fillColor;
        this.knobColor = builder.knobColor;
        this.textColor = builder.textColor;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void render(DrawContext context) {
        if (!isVisible())
            return;

        float trackHeight = Math.min(8f, getHeight() * 0.35f);
        float trackY = getY() + getHeight() - trackHeight - 6f;
        float trackX = getX();
        float trackWidth = getWidth();

        int disabledTrack = isDisabled() ? ColorHelper.blendColors(this.trackColor, 0xFF000000, 0.4f) : this.trackColor;
        int disabledFill = isDisabled() ? ColorHelper.blendColors(this.fillColor, 0xFF000000, 0.4f) : this.fillColor;
        int disabledKnob = isDisabled() ? ColorHelper.blendColors(this.knobColor, 0xFF000000, 0.4f) : this.knobColor;
        int disabledText = isDisabled() ? ColorHelper.blendColors(this.textColor, 0xFF000000, 0.4f) : this.textColor;

        context.drawRect(trackX, trackY, trackWidth, trackHeight, disabledTrack);

        float knobCenterX = knobCenterX();
        float fillWidth = knobCenterX - trackX;
        context.drawRect(trackX, trackY, Math.max(0f, fillWidth), trackHeight, disabledFill);

        float knobSize = Math.min(16f, getHeight());
        float knobX = knobCenterX - knobSize * 0.5f;
        float knobY = trackY + (trackHeight - knobSize) * 0.5f;
        context.drawRect(knobX, knobY, knobSize, knobSize, disabledKnob);

        if (this.font != null) {
            String display = this.valueFormatter.apply(this.value);
            float textWidth = this.font.measureTextWidth(display);
            float textX = trackX + (trackWidth - textWidth) * 0.5f;
            float textY = getY() + 4f;
            context.drawText(this.font, display, textX, textY, disabledText);
        }
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        if (!isVisible() || isDisabled())
            return;

        this.lastMouseX = xPos;
        this.lastMouseY = yPos;
        if (this.dragging) {
            updateValueFromMouse(xPos);
        }
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        if (!isVisible() || isDisabled() || button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return;

        if (action == GLFW.GLFW_PRESS && containsPoint(this.lastMouseX, this.lastMouseY)) {
            this.dragging = true;
            updateValueFromMouse(this.lastMouseX);
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.dragging = false;
        }
    }

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        if (disabled) {
            this.dragging = false;
        }
    }

    private void updateValueFromMouse(double mouseX) {
        float relative = (float) ((mouseX - getX()) / Math.max(1f, getWidth()));
        float fraction = clamp(relative, 0f, 1f);
        float target = this.min + fraction * (this.max - this.min);
        setValue(target);
    }

    private void setValue(float newValue) {
        float clamped = clampAndQuantize(newValue);
        if (Math.abs(clamped - this.value) > 1.0E-4) {
            this.value = clamped;
            this.onValueChanged.accept(this.value);
        }
    }

    private float clampAndQuantize(float value) {
        float clamped = clamp(value, this.min, this.max);
        if (this.step > 0f) {
            float steps = Math.round((clamped - this.min) / this.step);
            clamped = this.min + steps * this.step;
        }
        return clamp(clamped, this.min, this.max);
    }

    private float knobCenterX() {
        if (this.max - this.min <= 0f) {
            return getX();
        }

        float fraction = (this.value - this.min) / (this.max - this.min);
        return getX() + fraction * getWidth();
    }

    public static class Builder {
        private float x;
        private float y;
        private float width = 240f;
        private float height = 48f;
        private float min = 0f;
        private float max = 100f;
        private float initialValue = 50f;
        private float step = 1f;
        private FontAtlas font = Fonts.defaultFont();
        private Consumer<Float> onValueChanged = v -> {
        };
        private Function<Float, String> valueFormatter = value -> String.format("%.0f", value);
        private int trackColor = 0xFF2A2A2A;
        private int fillColor = 0xFF5AA2FF;
        private int knobColor = 0xFFEAEAEA;
        private int textColor = 0xFFFFFFFF;

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

        public Builder range(float min, float max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder initialValue(float value) {
            this.initialValue = value;
            return this;
        }

        public Builder step(float step) {
            this.step = step;
            return this;
        }

        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        public Builder onValueChanged(Consumer<Float> onValueChanged) {
            this.onValueChanged = onValueChanged;
            return this;
        }

        public Builder valueFormatter(Function<Float, String> formatter) {
            this.valueFormatter = formatter;
            return this;
        }

        public Builder trackColor(int color) {
            this.trackColor = color;
            return this;
        }

        public Builder fillColor(int color) {
            this.fillColor = color;
            return this;
        }

        public Builder knobColor(int color) {
            this.knobColor = color;
            return this;
        }

        public Builder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public SliderWidget build() {
            return new SliderWidget(this);
        }
    }
}
