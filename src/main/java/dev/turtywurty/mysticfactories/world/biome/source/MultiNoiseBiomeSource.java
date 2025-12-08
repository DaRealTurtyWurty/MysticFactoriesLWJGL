package dev.turtywurty.mysticfactories.world.biome.source;

import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.biome.ClimateProfile;
import dev.turtywurty.mysticfactories.world.biome.FloatProvider;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseType;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MultiNoiseBiomeSource extends BiomeSource {
    private static final long X_PRIME = 341873128712L;
    private static final long Z_PRIME = 132897987541L;

    private final List<Biome> biomes;
    private final FastNoise temperatureNoise;
    private final FastNoise humidityNoise;
    private final FastNoise altitudeNoise;

    public MultiNoiseBiomeSource(long seed, List<Biome> biomes) {
        super(seed);
        if (biomes == null || biomes.isEmpty())
            throw new IllegalArgumentException("Biomes cannot be null or empty");

        this.biomes = List.copyOf(biomes);

        int noiseSeed = Long.hashCode(seed);
        this.temperatureNoise = createNoise(noiseSeed, 0.0075f);
        this.humidityNoise = createNoise(noiseSeed + 1, 0.0075f);
        this.altitudeNoise = createNoise(noiseSeed + 2, 0.005f);
    }

    @Override
    public Biome getBiome(int x, int z) {
        float temperatureSample = clamp01(normalize(this.temperatureNoise.getNoise(x, z)) * 0.9f);
        float humiditySample = clamp01(normalize(this.humidityNoise.getNoise(x, z)) * 1.1f);
        float altitudeSample = clamp01(normalize(this.altitudeNoise.getNoise(x, z)) * 0.95f);

        Biome closestBiome = null;
        float closestDistance = Float.MAX_VALUE;
        for (Biome biome : this.biomes) {
            ClimateProfile climate = biome.getClimateProfile();
            if (climate == null) {
                if (closestBiome == null) {
                    closestBiome = biome;
                }

                continue;
            }

            float altitudeTarget = sampleAltitude(climate.altitudeVariation(), x, z);
            float distance = squaredDistance(
                    temperatureSample, humiditySample, altitudeSample,
                    climate.temperature(), climate.humidity(), altitudeTarget);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestBiome = biome;
            }
        }

        return Objects.requireNonNullElse(closestBiome, this.biomes.getFirst());
    }

    private static float normalize(float value) {
        return (value + 1.0f) * 0.5f;
    }

    private static float clamp01(float value) {
        return Math.clamp(value, 0, 1);
    }

    private static FastNoise createNoise(int seed, float frequency) {
        return FastNoise.builder()
                .seed(seed)
                .type(NoiseType.SIMPLEX2)
                .fractal(FractalType.FBM)
                .frequency(frequency)
                .octaves(4)
                .build();
    }

    private float sampleAltitude(FloatProvider provider, int x, int z) {
        if (provider == null)
            return 0.0f;

        long mixedSeed = this.seed ^ (long) x * X_PRIME ^ (long) z * Z_PRIME;
        return provider.get(new Random(mixedSeed));
    }

    private static float squaredDistance(
            float sampleTemperature, float sampleHumidity, float sampleAltitude,
            float targetTemperature, float targetHumidity, float targetAltitude) {
        float temperatureDiff = sampleTemperature - targetTemperature;
        float humidityDiff = sampleHumidity - targetHumidity;
        float altitudeDiff = sampleAltitude - targetAltitude;
        return temperatureDiff * temperatureDiff
                + humidityDiff * humidityDiff
                + altitudeDiff * altitudeDiff;
    }
}
