package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.biome.ClimateProfile;
import dev.turtywurty.mysticfactories.world.biome.FloatProvider;
import dev.turtywurty.mysticfactories.world.biome.SurfaceProfile;

@RegistryHolder
public class Biomes {
    public static final Biome OVERWORLD = register("overworld",
            Biome.builder(Identifier.of("overworld"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.65f)
                            .humidity(0.55f)
                            .altitudeVariation(FloatProvider.constant(0.1f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.GRASS)
                            .secondarySurface(TileTypes.SAND)
                            .primaryFluid(TileTypes.WATER)
                            .build())
                    .build());

    public static final Biome OVERWORLD_HILLS = register("overworld_hills",
            Biome.builder(Identifier.of("overworld_hills"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.45f)
                            .humidity(0.35f)
                            .altitudeVariation(FloatProvider.constant(0.85f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.GRASS)
                            .secondarySurface(TileTypes.SAND)
                            .primaryFluid(TileTypes.WATER)
                            .build())
                    .build());

    public static final Biome OVERWORLD_COAST = register("overworld_coast",
            Biome.builder(Identifier.of("overworld_coast"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.7f)
                            .humidity(0.8f)
                            .altitudeVariation(FloatProvider.constant(0.0f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.SAND)
                            .secondarySurface(TileTypes.GRASS)
                            .primaryFluid(TileTypes.WATER)
                            .build())
                    .build());

    public static final Biome OCEAN = register("ocean",
            Biome.builder(Identifier.of("ocean"))
                    .climateProfile(new ClimateProfile.Builder()
                            .temperature(0.25f)
                            .humidity(0.95f)
                            .altitudeVariation(FloatProvider.constant(0.0f))
                            .build())
                    .surfaceProfile(new SurfaceProfile.Builder()
                            .primarySurface(TileTypes.SAND)
                            .secondarySurface(TileTypes.GRASS)
                            .primaryFluid(TileTypes.WATER)
                            .build())
                    .build());

    private Biomes() {
    }

    private static Biome register(String name, Biome biome) {
        Identifier id = Identifier.of(name);
        return Registries.BIOMES.register(id, biome);
    }
}
