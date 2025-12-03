package dev.turtywurty.mysticfactories.util;

import org.joml.Random;
import org.joml.Vector2i;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public static Direction fromDelta(int dx, int dy) {
        if (dx == 0 && dy < 0) return NORTH;
        if (dx > 0 && dy == 0) return EAST;
        if (dx == 0 && dy > 0) return SOUTH;
        if (dx < 0 && dy == 0) return WEST;
        throw new IllegalArgumentException("Invalid delta values: dx=" + dx + ", dy=" + dy);
    }

    public static Direction randomDirection(Random random) {
        Direction[] directions = values();
        return directions[random.nextInt(directions.length)];
    }

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }

    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }

    public int toDeltaX() {
        return switch (this) {
            case EAST -> 1;
            case WEST -> -1;
            default -> 0;
        };
    }

    public int toDeltaY() {
        return switch (this) {
            case NORTH -> -1;
            case SOUTH -> 1;
            default -> 0;
        };
    }

    public Vector2i toDelta() {
        return new Vector2i(toDeltaX(), toDeltaY());
    }

    public Direction rotateClockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    public Direction rotateCounterClockwise() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public Direction rotate(int times) {
        Direction dir = this;
        int normalizedTimes = ((times % 4) + 4) % 4; // Normalize to [0, 3]
        for (int i = 0; i < normalizedTimes; i++) {
            dir = dir.rotateClockwise();
        }

        return dir;
    }

    public Direction rotate(Direction rotation) {
        return switch (rotation) {
            case NORTH -> this;
            case EAST -> rotateClockwise();
            case SOUTH -> opposite();
            case WEST -> rotateCounterClockwise();
        };
    }

    public float toAngleDegrees() {
        return switch (this) {
            case NORTH -> 0f;
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
        };
    }

    public float toAngleRadians() {
        return (float) Math.toRadians(toAngleDegrees());
    }

    public static Direction fromAngleDegrees(float angle) {
        float normalizedAngle = ((angle % 360) + 360) % 360;
        if (normalizedAngle >= 315 || normalizedAngle < 45) return NORTH;
        if (normalizedAngle >= 45 && normalizedAngle < 135) return EAST;
        if (normalizedAngle >= 135 && normalizedAngle < 225) return SOUTH;
        return WEST;
    }

    public static Direction fromAngleRadians(float angle) {
        return fromAngleDegrees((float) Math.toDegrees(angle));
    }
}
