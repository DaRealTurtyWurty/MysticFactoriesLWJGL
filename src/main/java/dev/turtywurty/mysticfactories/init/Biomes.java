package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.biome.ClimateProfile;
import dev.turtywurty.mysticfactories.world.biome.FloatProvider;
import dev.turtywurty.mysticfactories.world.biome.surface.SurfaceProfile;
import dev.turtywurty.mysticfactories.world.biome.surface.SurfaceRule;

@RegistryHolder
public class Biomes {
    public static final Biome OVERWORLD = register("overworld",
            Biome.builder(Identifier.of("overworld"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.65f)
                            .humidity(0.55f)
                            .altitudeVariation(FloatProvider.constant(0.25f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.GRASS)
                            .primaryFluid(TileTypes.WATER)
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.WATER)
                                    .addCondition(SurfaceRule.noiseBelow(-0.2f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SHALLOW_WATER)
                                    .addCondition(SurfaceRule.noiseAtLeast(-0.2f))
                                    .addCondition(SurfaceRule.noiseBelow(-0.05f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SAND)
                                    .addCondition(SurfaceRule.noiseAtLeast(-0.05f))
                                    .addCondition(SurfaceRule.noiseBelow(0.07f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.DIRT)
                                    .addCondition(SurfaceRule.noiseAtLeast(0.07f))
                                    .addCondition(SurfaceRule.noiseBelow(0.2f))
                                    .build())
                            .build())
                    .build());

    public static final Biome OVERWORLD_HILLS = register("overworld_hills",
            Biome.builder(Identifier.of("overworld_hills"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.45f)
                            .humidity(0.35f)
                            .altitudeVariation(FloatProvider.constant(0.75f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.STONE)
                            .primaryFluid(TileTypes.WATER)
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.WATER)
                                    .addCondition(SurfaceRule.noiseBelow(-0.2f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SHALLOW_WATER)
                                    .addCondition(SurfaceRule.noiseAtLeast(-0.2f))
                                    .addCondition(SurfaceRule.noiseBelow(-0.06f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SAND)
                                    .addCondition(SurfaceRule.noiseAtLeast(-0.06f))
                                    .addCondition(SurfaceRule.noiseBelow(0.08f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.DIRT)
                                    .addCondition(SurfaceRule.noiseAtLeast(0.08f))
                                    .addCondition(SurfaceRule.noiseBelow(0.18f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.GRASS)
                                    .addCondition(SurfaceRule.noiseAtLeast(0.18f))
                                    .addCondition(SurfaceRule.noiseBelow(0.45f))
                                    .build())
                            .build())
                    .build());

    public static final Biome OVERWORLD_COAST = register("overworld_coast",
            Biome.builder(Identifier.of("overworld_coast"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.7f)
                            .humidity(0.8f)
                            .altitudeVariation(FloatProvider.constant(-0.1f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.SAND)
                            .primaryFluid(TileTypes.SHALLOW_WATER)
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.DEEP_WATER)
                                    .addCondition(SurfaceRule.noiseBelow(-0.3f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.WATER)
                                    .addCondition(SurfaceRule.noiseAtLeast(-0.3f))
                                    .addCondition(SurfaceRule.noiseBelow(-0.1f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SHALLOW_WATER)
                                    .addCondition(SurfaceRule.noiseAtLeast(-0.1f))
                                    .addCondition(SurfaceRule.noiseBelow(0.07f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SAND)
                                    .addCondition(SurfaceRule.noiseAtLeast(0.07f))
                                    .addCondition(SurfaceRule.noiseBelow(0.25f))
                                    .build())
                            .build())
                    .build());

    public static final Biome OCEAN = register("ocean",
            Biome.builder(Identifier.of("ocean"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.25f)
                            .humidity(0.95f)
                            .altitudeVariation(FloatProvider.constant(-0.75f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.SHALLOW_WATER)
                            .primaryFluid(TileTypes.DEEP_WATER)
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.DEEP_WATER)
                                    .addCondition(SurfaceRule.noiseBelow(0.15f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.WATER)
                                    .addCondition(SurfaceRule.noiseAtLeast(0.15f))
                                    .addCondition(SurfaceRule.noiseBelow(0.35f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SHALLOW_WATER)
                                    .addCondition(SurfaceRule.noiseAtLeast(0.35f))
                                    .addCondition(SurfaceRule.noiseBelow(0.5f))
                                    .build())
                            .addSurfaceRule(SurfaceRule.builder(TileTypes.SAND)
                                    .addCondition(SurfaceRule.noiseAtLeast(0.5f))
                                    .build())
                            .build())
                    .build());

    private Biomes() {
    }

    private static Biome register(String name, Biome biome) {
        Identifier id = Identifier.of(name);
        return Registries.BIOMES.register(id, biome);
    }
}
