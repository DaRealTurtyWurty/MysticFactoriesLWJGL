package dev.turtywurty.mysticfactories.client.ui.widget;

import dev.turtywurty.mysticfactories.client.text.FontAtlas;
import dev.turtywurty.mysticfactories.client.text.Fonts;
import dev.turtywurty.mysticfactories.client.ui.DrawContext;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

public class TextInput extends Widget {
    @Getter
    private String text = "";
    private int maxLength = 256;
    @Getter
    private boolean focused = false;
    private String placeholder = "";
    private FontAtlas font = Fonts.defaultFont();

    private int cursorPosition = 0;
    private int offset = 0;
    private final SelectionRange selectionRange = new SelectionRange(0, 0);
    private final Cursor cursor = new Cursor(0, 0, this.font);

    private int backgroundColor = 0xFF000000;
    private int borderColor = 0xFF888888;
    private int focusBorderColor = 0xFFFFFFFF;
    private int selectionColor = 0xFF4444FF;
    private int placeholderColor = 0x888888;
    private int textColor = 0xFFFFFFFF;
    private int borderWidth = 1;

    private float lastMouseX = 0;
    private float lastMouseY = 0;
    private boolean selecting = false;

    protected TextInput(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void preRender(DrawContext context) {
        int borderCol = this.focused ? this.focusBorderColor : this.borderColor;
        context.drawRect(getX(), getY(), getWidth(), getHeight(), borderCol);
        context.drawRect(getX() + this.borderWidth, getY() + this.borderWidth,
                getWidth() - this.borderWidth * 2, getHeight() - this.borderWidth * 2, this.backgroundColor);
    }

    @Override
    public void render(DrawContext context) {
        context.getScissorStack().push(getX() + this.borderWidth, getY() + this.borderWidth,
                getWidth() - this.borderWidth * 2, getHeight() - this.borderWidth * 2);

        if (this.selectionRange.getStart() != this.selectionRange.getEnd()) {
            float selXStart = getX() + 4 - this.font.measureTextWidth(this.text.substring(0, this.offset))
                    + this.font.measureTextWidth(this.text.substring(this.offset, this.selectionRange.getStart()));
            float selXEnd = getX() + 4 - this.font.measureTextWidth(this.text.substring(0, this.offset))
                    + this.font.measureTextWidth(this.text.substring(this.offset, this.selectionRange.getEnd()));
            float selY = getY() + (getHeight() - this.font.getLineHeight()) / 2f;
            context.drawRect(selXStart, selY, selXEnd - selXStart, this.font.getLineHeight(), this.selectionColor);
        }

        float x = getX() + 4 - this.font.measureTextWidth(this.text.substring(0, this.offset));
        float y = getY() + (getHeight() - this.font.getLineHeight()) / 2f;

        String displayText = this.text.isEmpty() ? this.placeholder : this.text;
        int color = this.text.isEmpty() ? this.placeholderColor : this.textColor;

        context.drawText(this.font, displayText, x, y, color);

        if (this.focused) {
            this.cursor.render(context);
        }

        context.getScissorStack().pop();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = Math.max(1, maxLength);
        if (this.text.length() > this.maxLength) {
            setText(this.text);
        }
    }

    public void setText(String text) {
        if (text == null)
            text = "";

        if (text.length() > this.maxLength) {
            text = text.substring(0, this.maxLength);
        }

        this.text = text;
        this.offset = 0;
        this.cursorPosition = this.text.length();
        updateCursorPosition();
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            updateCursorPosition();
        }
    }

    public void setPlaceholder(String placeholder) {
        if (placeholder == null)
            placeholder = "";

        this.placeholder = placeholder;
    }

    public void setFont(FontAtlas font) {
        this.font = font;
        this.cursor.setFont(font);
        updateCursorPosition();
    }

    private void updateCursorPosition() {
        float textWidth = getWidth() - this.borderWidth * 2 - 8;

        if (this.cursorPosition < this.offset) {
            this.offset = this.cursorPosition;
        }

        while (this.font.measureTextWidth(this.text.substring(this.offset, this.cursorPosition)) > textWidth) {
            this.offset++;
        }

        // Try to scroll left to fill empty space
        int newOffset = this.offset;
        while (newOffset > 0) {
            if (this.font.measureTextWidth(this.text.substring(newOffset - 1)) <= textWidth) {
                newOffset--;
            } else {
                break;
            }
        }
        this.offset = newOffset;

        float x = getX() + 4 + this.font.measureTextWidth(this.text.substring(this.offset, this.cursorPosition));
        float y = getY() + (getHeight() - this.font.getLineHeight()) / 2f;
        this.cursor.setPosition(x, y);

        this.cursor.setVisible(this.focused);
    }

    @Override
    public void onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!this.focused)
            return;

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (this.text.isEmpty() || this.cursorPosition == 0)
                return;

            this.text = this.text.substring(0, this.cursorPosition - 1) + this.text.substring(this.cursorPosition);
            this.cursorPosition--;
            updateCursorPosition();
        } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
            if (this.text.isEmpty() || this.cursorPosition >= this.text.length())
                return;

            this.text = this.text.substring(0, this.cursorPosition) + this.text.substring(this.cursorPosition + 1);
            updateCursorPosition();
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            if (this.cursorPosition > 0) {
                this.cursorPosition--;
                updateCursorPosition();
            }
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (this.cursorPosition < this.text.length()) {
                this.cursorPosition++;
                updateCursorPosition();
            }
        } else if (keyCode == GLFW.GLFW_KEY_END) {
            this.cursorPosition = this.text.length();
            updateCursorPosition();
        } else if (keyCode == GLFW.GLFW_KEY_HOME) {
            this.cursorPosition = 0;
            updateCursorPosition();
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.focused = false;
            this.cursor.setVisible(false);
        }
    }

    @Override
    public void onMouseMove(double xPos, double yPos) {
        this.lastMouseX = (float) xPos;
        this.lastMouseY = (float) yPos;

        if (this.selecting && containsPoint(this.lastMouseX, this.lastMouseY)) {
            int relativeX = (int) (this.lastMouseX - getX() - 4
                    + this.font.measureTextWidth(this.text.substring(0, this.offset)));
            int pos = 0;
            float accumulatedWidth = 0f;
            for (int i = 0; i < this.text.length(); i++) {
                float charWidth = this.font.measureTextWidth(String.valueOf(this.text.charAt(i)));
                if (accumulatedWidth + charWidth / 2f >= relativeX) {
                    pos = i;
                    break;
                }

                accumulatedWidth += charWidth;
                pos = i + 1;
            }

            this.selectionRange.extend(pos);
        }
    }

    @Override
    public void onMouseButtonPress(int button, int action, int modifiers) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT || action != GLFW.GLFW_PRESS)
            return;

        if (containsPoint(this.lastMouseX, this.lastMouseY)) {
            int relativeX = (int) (this.lastMouseX - getX() - 4
                    + this.font.measureTextWidth(this.text.substring(0, this.offset)));
            int pos = 0;
            float accumulatedWidth = 0f;
            for (int i = 0; i < this.text.length(); i++) {
                float charWidth = this.font.measureTextWidth(String.valueOf(this.text.charAt(i)));
                if (accumulatedWidth + charWidth / 2f >= relativeX) {
                    pos = i;
                    break;
                }

                accumulatedWidth += charWidth;
                pos = i + 1;
            }

            this.cursorPosition = pos;
            updateCursorPosition();
            setFocused(true);
            this.selecting = true;
            this.selectionRange.begin(pos);
        }
    }

    @Override
    public void onMouseButtonRelease(int button, int action, int modifiers) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT || action != GLFW.GLFW_RELEASE)
            return;

        setFocused(containsPoint(this.lastMouseX, this.lastMouseY));
        this.selecting = false;
    }

    @Override
    public void onCharInput(int codepoint) {
        if (!this.focused)
            return;

        if (this.text.length() >= this.maxLength)
            return;

        char character = (char) codepoint;
        if (Character.isISOControl(character))
            return;

        this.text = this.text.substring(0, this.cursorPosition) + character + this.text.substring(this.cursorPosition);
        this.cursorPosition++;
        updateCursorPosition();
    }


    @Getter
    public static class SelectionRange {
        private int start;
        private int end;
        private int anchor;

        public SelectionRange(int start, int end) {
            this.start = start;
            this.end = end;
            this.anchor = start;
        }

        public void begin(int position) {
            this.anchor = position;
            this.start = position;
            this.end = position;
        }

        public void extend(int position) {
            if (position < this.anchor) {
                this.start = position;
                this.end = this.anchor;
            } else {
                this.start = this.anchor;
                this.end = position;
            }
        }
    }

    public static class Builder {
        private float x;
        private float y;
        private float width;
        private float height;
        private int maxLength = 256;
        private String placeholder = "";
        private FontAtlas font = Fonts.defaultFont();
        private int backgroundColor = 0xFF000000;
        private int borderColor = 0xFF888888;
        private int focusBorderColor = 0xFFFFFFFF;
        private int selectionColor = 0xFF4444FF;
        private int placeholderColor = 0x888888;
        private int textColor = 0xFFFFFFFF;
        private int borderWidth = 1;

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

        public Builder maxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder font(FontAtlas font) {
            this.font = font;
            return this;
        }

        public Builder backgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder borderColor(int color) {
            this.borderColor = color;
            return this;
        }

        public Builder focusBorderColor(int color) {
            this.focusBorderColor = color;
            return this;
        }

        public Builder selectionColor(int color) {
            this.selectionColor = color;
            return this;
        }

        public Builder placeholderColor(int color) {
            this.placeholderColor = color;
            return this;
        }

        public Builder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public Builder borderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public TextInput build() {
            var input = new TextInput(this.x, this.y, this.width, this.height);
            input.setMaxLength(this.maxLength);
            input.setPlaceholder(this.placeholder);
            input.setFont(this.font);
            input.backgroundColor = this.backgroundColor;
            input.borderColor = this.borderColor;
            input.focusBorderColor = this.focusBorderColor;
            input.selectionColor = this.selectionColor;
            input.placeholderColor = this.placeholderColor;
            input.textColor = this.textColor;
            input.borderWidth = this.borderWidth;
            return input;
        }
    }
}
