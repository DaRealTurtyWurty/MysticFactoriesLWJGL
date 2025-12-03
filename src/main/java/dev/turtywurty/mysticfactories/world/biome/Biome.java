package dev.turtywurty.mysticfactories.world.biome;

import dev.turtywurty.mysticfactories.world.biome.feature.FeatureRule;

import java.util.ArrayList;
import java.util.List;

public record Biome(ClimateProfile climateProfile, SurfaceProfile surfaceProfile, List<FeatureRule> featureRules,
                    List<EntitySpawnRule> entitySpawnRules, VisualProfile visualProfile) {
    public static class Builder {
        private ClimateProfile climateProfile;
        private SurfaceProfile surfaceProfile;
        private final List<FeatureRule> featureRules = new ArrayList<>();
        private final List<EntitySpawnRule> entitySpawnRules = new ArrayList<>();
        private VisualProfile visualProfile;

        public Builder climateProfile(ClimateProfile climateProfile) {
            this.climateProfile = climateProfile;
            return this;
        }

        public Builder surfaceProfile(SurfaceProfile surfaceProfile) {
            this.surfaceProfile = surfaceProfile;
            return this;
        }

        public Builder addFeatureRule(FeatureRule featureRule) {
            this.featureRules.add(featureRule);
            return this;
        }

        public Builder addEntitySpawnRule(EntitySpawnRule entitySpawnRule) {
            this.entitySpawnRules.add(entitySpawnRule);
            return this;
        }

        public Builder visualProfile(VisualProfile visualProfile) {
            this.visualProfile = visualProfile;
            return this;
        }

        public Biome build() {
            return new Biome(climateProfile, surfaceProfile, featureRules, entitySpawnRules, visualProfile);
        }
    }
}
