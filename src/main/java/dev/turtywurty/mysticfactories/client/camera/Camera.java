package dev.turtywurty.mysticfactories.client.camera;

import dev.turtywurty.mysticfactories.world.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {
    @Getter
    private final Vector2f position;
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final float orthoNear, orthoFar;
    @Setter
    private Entity followTarget;
    @Setter
    private float followTargetScale = 1.0f;
    @Getter
    private float orthoLeft;
    @Getter
    private float orthoRight;
    @Getter
    private float orthoBottom;
    @Getter
    private float orthoTop;
    @Setter
    private float targetWorldHeight;
    @Getter
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
        updateFollowTarget();
        return this.viewMatrix.identity().translate(-position.x, -position.y, 0.0f);
    }

    public Matrix4f getViewMatrixPixelAligned() {
        updateFollowTarget();
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

    public void processScroll(float yOffset) {
        float zoomSensitivity = 0.1f;
        zoom += yOffset * zoomSensitivity;
        if (zoom < 0.1f) {
            zoom = 0.1f;
        }

        if (zoom > 10.0f) {
            zoom = 10.0f;
        }
    }

    public void clearFollowTarget() {
        this.followTarget = null;
    }

    private void updateFollowTarget() {
        if (this.followTarget != null) {
            var pos = this.followTarget.getPosition();
            this.position.set((float) (pos.x * this.followTargetScale), (float) (pos.y * this.followTargetScale));
        }
    }

    public enum CameraMovement {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
