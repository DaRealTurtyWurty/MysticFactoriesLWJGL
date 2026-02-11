package dev.turtywurty.mysticfactories.world.biome.surface;

import dev.turtywurty.mysticfactories.world.tile.TileType;

import java.util.ArrayList;
import java.util.List;

public record SurfaceProfile(TileType primarySurface, TileType primaryFluid, List<SurfaceRule> surfaceRules) {
    public static class Builder {
        private final List<SurfaceRule> surfaceRules = new ArrayList<>();
        private TileType primarySurface;
        private TileType primaryFluid;

        public Builder primarySurface(TileType primarySurface) {
            this.primarySurface = primarySurface;
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
            return new SurfaceProfile(primarySurface, primaryFluid, surfaceRules);
        }
    }
}
