package dev.turtywurty.mysticfactories.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.List;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;

public class AABB {
    public static final Codec<AABB> CODEC = Codec.DOUBLE.listOf().comapFlatMap(list -> {
        if (list.size() != 4)
            return DataResult.error(() -> "AABB list must contain exactly 4 elements, got " + list.size());
        return DataResult.success(new AABB(list.get(0), list.get(1), list.get(2), list.get(3)));
    }, aabb -> List.of(aabb.minX, aabb.minY, aabb.maxX, aabb.maxY));

    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    public AABB(double minX, double minY, double maxX, double maxY) {
        if (Double.isNaN(minX) || Double.isNaN(minY) || Double.isNaN(maxX) || Double.isNaN(maxY))
            throw new IllegalArgumentException("AABB coordinates cannot be NaN");

        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
    }

    public static AABB fromCenter(double centerX, double centerY, double halfWidth, double halfHeight) {
        return new AABB(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY + halfHeight);
    }

    public double minX() {
        return minX;
    }

    public double minY() {
        return minY;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    public double width() {
        return this.maxX - this.minX;
    }

    public double height() {
        return this.maxY - this.minY;
    }

    public double centerX() {
        return (this.minX + this.maxX) * 0.5;
    }

    public double centerY() {
        return (this.minY + this.maxY) * 0.5;
    }

    public boolean intersects(AABB other) {
        return this.maxX > other.minX &&
                other.maxX > this.minX &&
                this.maxY > other.minY &&
                other.maxY > this.minY;
    }

    public boolean contains(double x, double y) {
        return x >= this.minX && x <= this.maxX &&
                y >= this.minY && y <= this.maxY;
    }

    public AABB offset(double dx, double dy) {
        return new AABB(this.minX + dx, this.minY + dy, this.maxX + dx, this.maxY + dy);
    }

    public AABB expand(double amountX, double amountY) {
        return new AABB(this.minX - amountX, this.minY - amountY, this.maxX + amountX, this.maxY + amountY);
    }

    public AABB inflate(double amount) {
        return expand(amount, amount);
    }

    public Optional<AABB> intersection(AABB other) {
        if (!intersects(other))
            return Optional.empty();

        double newMinX = Math.max(this.minX, other.minX);
        double newMinY = Math.max(this.minY, other.minY);
        double newMaxX = Math.min(this.maxX, other.maxX);
        double newMaxY = Math.min(this.maxY, other.maxY);
        return Optional.of(new AABB(newMinX, newMinY, newMaxX, newMaxY));
    }

    public AABB union(AABB other) {
        double newMinX = Math.min(this.minX, other.minX);
        double newMinY = Math.min(this.minY, other.minY);
        double newMaxX = Math.max(this.maxX, other.maxX);
        double newMaxY = Math.max(this.maxY, other.maxY);
        return new AABB(newMinX, newMinY, newMaxX, newMaxY);
    }

    public AABB withTransform(DoubleUnaryOperator transformX, DoubleUnaryOperator transformY) {
        double newMinX = transformX.applyAsDouble(this.minX);
        double newMaxX = transformX.applyAsDouble(this.maxX);
        double newMinY = transformY.applyAsDouble(this.minY);
        double newMaxY = transformY.applyAsDouble(this.maxY);
        return new AABB(newMinX, newMinY, newMaxX, newMaxY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AABB other)) return false;
        return Double.compare(other.minX, minX) == 0 &&
                Double.compare(other.minY, minY) == 0 &&
                Double.compare(other.maxX, maxX) == 0 &&
                Double.compare(other.maxY, maxY) == 0;
    }

    @Override
    public int hashCode() {
        long result = Double.doubleToLongBits(minX);
        result = 31 * result + Double.doubleToLongBits(minY);
        result = 31 * result + Double.doubleToLongBits(maxX);
        result = 31 * result + Double.doubleToLongBits(maxY);
        return Long.hashCode(result);
    }

    @Override
    public String toString() {
        return "AABB[" +
                "minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ']';
    }
}
