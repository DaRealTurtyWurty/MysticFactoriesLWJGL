package dev.turtywurty.mysticfactories.world.biome.feature;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.biome.feature.shape.PlacementShape;

import java.util.Optional;

public record FeatureRule(Identifier featureId, float attemptsPerChunk, IntProvider countProvider, PlacementShape placementShape, PlacementCondition placementCondition, Optional<DensityModifier> densityModifier) {
}
