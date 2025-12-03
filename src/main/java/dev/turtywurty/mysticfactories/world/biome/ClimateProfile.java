package dev.turtywurty.mysticfactories.world.biome;

public record ClimateProfile(float temperature, float humidity, FloatProvider altitudeVariation) {
    public static class Builder {
        private float temperature = 0.5f;
        private float humidity = 0.5f;
        private FloatProvider altitudeVariation = FloatProvider.constant(0.0f);

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder humidity(float humidity) {
            this.humidity = humidity;
            return this;
        }

        public Builder altitudeVariation(FloatProvider altitudeVariation) {
            this.altitudeVariation = altitudeVariation;
            return this;
        }

        public ClimateProfile build() {
            return new ClimateProfile(temperature, humidity, altitudeVariation);
        }
    }
}
