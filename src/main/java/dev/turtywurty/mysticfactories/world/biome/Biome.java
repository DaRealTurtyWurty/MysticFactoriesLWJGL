package dev.turtywurty.mysticfactories.world.biome;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registerable;
import dev.turtywurty.mysticfactories.world.biome.feature.FeatureRule;
import dev.turtywurty.mysticfactories.world.biome.spawning.EntitySpawnRule;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
public final class Biome implements Registerable {
    @Setter
    private Identifier id;
    private final ClimateProfile climateProfile;
    private final SurfaceProfile surfaceProfile;
    private final List<FeatureRule> featureRules;
    private final List<EntitySpawnRule> entitySpawnRules;

    public Biome(Identifier id, ClimateProfile climateProfile, SurfaceProfile surfaceProfile, List<FeatureRule> featureRules,
                 List<EntitySpawnRule> entitySpawnRules) {
        this.id = id;
        this.climateProfile = climateProfile;
        this.surfaceProfile = surfaceProfile;
        this.featureRules = featureRules;
        this.entitySpawnRules = entitySpawnRules;
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    public static class Builder {
        private final Identifier id;

        private ClimateProfile climateProfile;
        private SurfaceProfile surfaceProfile;
        private final List<FeatureRule> featureRules = new ArrayList<>();
        private final List<EntitySpawnRule> entitySpawnRules = new ArrayList<>();

        public Builder(Identifier id) {
            this.id = id;
        }

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

        public Biome build() {
            return new Biome(id, climateProfile, surfaceProfile, featureRules, entitySpawnRules);
        }
    }
}
