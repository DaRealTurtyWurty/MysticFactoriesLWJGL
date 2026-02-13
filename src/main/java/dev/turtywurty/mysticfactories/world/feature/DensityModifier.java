package dev.turtywurty.mysticfactories.world.feature;

public record DensityModifier(float noiseScale, float noiseThreshold, float adjustment) {
    public static class Builder {
        private float noiseScale;
        private float noiseThreshold;
        private float adjustment;

        public Builder noiseScale(float noiseScale) {
            this.noiseScale = noiseScale;
            return this;
        }

        public Builder noiseThreshold(float noiseThreshold) {
            this.noiseThreshold = noiseThreshold;
            return this;
        }

        public Builder adjustment(float adjustment) {
            this.adjustment = adjustment;
            return this;
        }

        public DensityModifier build() {
            return new DensityModifier(noiseScale, noiseThreshold, adjustment);
        }
    }
}
