package dev.turtywurty.mysticfactories.world.biome.surface;

import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.ArrayList;
import java.util.List;

public record SurfaceRule(List<SurfaceCondition> conditions, TileType resultTile) {
    public SurfaceRule {
        conditions = List.copyOf(conditions);
    }

    public boolean matches(SurfaceContext context) {
        for (SurfaceCondition condition : this.conditions) {
            if (!condition.test(context)) {
                return false;
            }
        }

        return true;
    }

    public static Builder builder(TileType resultTile) {
        return new Builder(resultTile);
    }

    public static SurfaceCondition noiseBelow(float threshold) {
        return context -> context.noiseValue() < threshold;
    }

    public static SurfaceCondition noiseAtLeast(float threshold) {
        return context -> context.noiseValue() >= threshold;
    }

    @FunctionalInterface
    public interface SurfaceCondition {
        boolean test(SurfaceContext context);
    }

    public static class Builder {
        private final TileType resultTile;
        private final List<SurfaceCondition> conditions = new ArrayList<>();

        private Builder(TileType resultTile) {
            this.resultTile = resultTile;
        }

        public Builder addCondition(SurfaceCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public SurfaceRule build() {
            return new SurfaceRule(this.conditions, this.resultTile);
        }
    }
}
