package dev.turtywurty.mysticfactories.client.ui;

import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScissorStack {
    private record ScissorRect(int x, int y, int width, int height) {}

    private final Deque<ScissorRect> stack = new ArrayDeque<>();
    private final DrawContext context;

    public ScissorStack(DrawContext context) {
        this.context = context;
    }

    /**
     * Pushes a new scissor rectangle to the stack. The new rectangle is intersected
     * with the current scissor rectangle. Coordinates are in screen space (y-down).
     * @param x top-left x
     * @param y top-left y
     * @param width rect width
     * @param height rect height
     */
    public void push(float x, float y, float width, float height) {
        int screenWidth = (int) this.context.width();
        int screenHeight = (int) this.context.height();

        int newScissorX = Math.round(x);
        int newScissorY = Math.round(screenHeight - (y + height));
        int newScissorWidth = Math.round(width);
        int newScissorHeight = Math.round(height);

        ScissorRect current = this.stack.peek();
        if (current != null) {
            int currentX = current.x();
            int currentY = current.y();
            int currentWidth = current.width();
            int currentHeight = current.height();

            int intersectX1 = Math.max(newScissorX, currentX);
            int intersectY1 = Math.max(newScissorY, currentY);
            int intersectX2 = Math.min(newScissorX + newScissorWidth, currentX + currentWidth);
            int intersectY2 = Math.min(newScissorY + newScissorHeight, currentY + currentHeight);
            
            newScissorX = intersectX1;
            newScissorY = intersectY1;
            newScissorWidth = intersectX2 - intersectX1;
            newScissorHeight = intersectY2 - intersectY1;
        } else {
            // This is the first scissor, clip against screen bounds
            int screenScissorX = 0;
            int screenScissorY = 0;

            int intersectX1 = Math.max(newScissorX, screenScissorX);
            int intersectY1 = Math.max(newScissorY, screenScissorY);
            int intersectX2 = Math.min(newScissorX + newScissorWidth, screenScissorX + screenWidth);
            int intersectY2 = Math.min(newScissorY + newScissorHeight, screenScissorY + screenHeight);
            
            newScissorX = intersectX1;
            newScissorY = intersectY1;
            newScissorWidth = intersectX2 - intersectX1;
            newScissorHeight = intersectY2 - intersectY1;
        }

        if (newScissorWidth < 0) newScissorWidth = 0;
        if (newScissorHeight < 0) newScissorHeight = 0;

        var rect = new ScissorRect(newScissorX, newScissorY, newScissorWidth, newScissorHeight);
        this.stack.push(rect);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(rect.x(), rect.y(), rect.width(), rect.height());
    }

    public void pop() {
        if (this.stack.isEmpty())
            return;

        this.stack.pop();

        ScissorRect rect = this.stack.peek();
        if (rect != null) {
            GL11.glScissor(rect.x(), rect.y(), rect.width(), rect.height());
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }
}
