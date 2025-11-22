package dev.turtywurty.mysticfactories.client.camera;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {
    private final Vector2f position;
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();

    // Orthographic projection bounds
    private float orthoLeft, orthoRight, orthoBottom, orthoTop;
    private final float orthoNear, orthoFar;

    private float targetWorldHeight;
    private float zoom;
    private float unitsPerPixel = 1.0f;

    public Camera(Vector2f position, float targetWorldHeight) {
        this.position = position;
        this.targetWorldHeight = targetWorldHeight;
        this.zoom = 1.0f;

        this.orthoNear = -1.0f;
        this.orthoFar = 1.0f;
    }

    public Matrix4f getViewMatrix() {
        return this.viewMatrix.identity().translate(-position.x, -position.y, 0.0f);
    }

    public Matrix4f getViewMatrixPixelAligned() {
        float snappedX = (float) Math.round(position.x / unitsPerPixel) * unitsPerPixel;
        float snappedY = (float) Math.round(position.y / unitsPerPixel) * unitsPerPixel;
        return this.viewMatrix.identity().translate(-snappedX, -snappedY, 0.0f);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix.identity()
                .ortho(orthoLeft, orthoRight, orthoBottom, orthoTop, orthoNear, orthoFar);
    }
    
    public void setOrthoBounds(float windowWidth, float windowHeight) {
        float effectiveWorldHeight = targetWorldHeight / zoom;
        float aspectRatio = windowWidth / windowHeight;
        float effectiveWorldWidth = effectiveWorldHeight * aspectRatio;

        this.orthoLeft = -effectiveWorldWidth / 2.0f;
        this.orthoRight = effectiveWorldWidth / 2.0f;
        this.orthoBottom = -effectiveWorldHeight / 2.0f;
        this.orthoTop = effectiveWorldHeight / 2.0f;
        this.unitsPerPixel = (this.orthoTop - this.orthoBottom) / windowHeight;
    }

    public void processKeyboard(CameraMovement direction, float deltaTime) {
        float velocity = 2.5f * deltaTime;
        if (direction == CameraMovement.UP) {
            position.y += velocity;
        }

        if (direction == CameraMovement.DOWN) {
            position.y -= velocity;
        }

        if (direction == CameraMovement.LEFT) {
            position.x -= velocity;
        }

        if (direction == CameraMovement.RIGHT) {
            position.x += velocity;
        }
    }
    
    public void processScroll(float yoffset) {
        float zoomSensitivity = 0.1f;
        zoom += yoffset * zoomSensitivity;
        if (zoom < 0.1f) {
            zoom = 0.1f;
        }

        if (zoom > 10.0f) {
            zoom = 10.0f;
        }
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getZoom() {
        return zoom;
    }

    public void setTargetWorldHeight(float targetWorldHeight) {
        this.targetWorldHeight = targetWorldHeight;
    }

    public enum CameraMovement {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
