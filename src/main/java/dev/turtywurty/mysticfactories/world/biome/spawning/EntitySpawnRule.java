package dev.turtywurty.mysticfactories.world.biome.spawning;

import dev.turtywurty.mysticfactories.world.biome.IntProvider;
import dev.turtywurty.mysticfactories.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public record EntitySpawnRule(EntityType<?> entityType, IntProvider groupSize, float spawnProbability,
                              SpawnCategory spawnCategory, List<SpawnCondition> spawnConditions) {
    public static class Builder {
        private final EntityType<?> entityType;
        private IntProvider groupSize = IntProvider.constant(1);
        private float spawnProbability = 1.0f;
        private SpawnCategory spawnCategory = SpawnCategory.CREATURE;
        private final List<SpawnCondition> spawnConditions = new ArrayList<>();

        public Builder(EntityType<?> entityType) {
            this.entityType = entityType;
        }

        public Builder groupSize(IntProvider groupSize) {
            this.groupSize = groupSize;
            return this;
        }

        public Builder spawnProbability(float spawnProbability) {
            this.spawnProbability = spawnProbability;
            return this;
        }

        public Builder spawnCategory(SpawnCategory spawnCategory) {
            this.spawnCategory = spawnCategory;
            return this;
        }

        public Builder addSpawnCondition(SpawnCondition spawnCondition) {
            this.spawnConditions.add(spawnCondition);
            return this;
        }

        public EntitySpawnRule build() {
            return new EntitySpawnRule(entityType, groupSize, spawnProbability, spawnCategory, spawnConditions);
        }
    }
}
