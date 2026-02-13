package dev.turtywurty.mysticfactories.world.feature;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.biome.IntProvider;
import dev.turtywurty.mysticfactories.world.feature.shape.PlacementShape;

import java.util.Optional;

public record FeatureRule(Identifier featureId, int attemptsPerChunk, IntProvider countProvider,
                          PlacementShape placementShape, PlacementCondition placementCondition,
                          Optional<DensityModifier> densityModifier) {
}
