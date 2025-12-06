package dev.turtywurty.mysticfactories.world.biome;

import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.ArrayList;
import java.util.List;

public record SurfaceProfile(TileType primarySurface, TileType secondarySurface, TileType primaryFluid,
                             List<SurfaceRule> surfaceRules) {
    public static class Builder {
        private TileType primarySurface;
        private TileType secondarySurface;
        private TileType primaryFluid;
        private final List<SurfaceRule> surfaceRules = new ArrayList<>();

        public Builder primarySurface(TileType primarySurface) {
            this.primarySurface = primarySurface;
            return this;
        }

        public Builder secondarySurface(TileType secondarySurface) {
            this.secondarySurface = secondarySurface;
            return this;
        }

        public Builder primaryFluid(TileType primaryFluid) {
            this.primaryFluid = primaryFluid;
            return this;
        }

        public Builder addSurfaceRule(SurfaceRule surfaceRule) {
            this.surfaceRules.add(surfaceRule);
            return this;
        }

        public SurfaceProfile build() {
            return new SurfaceProfile(primarySurface, secondarySurface, primaryFluid, surfaceRules);
        }
    }
}
