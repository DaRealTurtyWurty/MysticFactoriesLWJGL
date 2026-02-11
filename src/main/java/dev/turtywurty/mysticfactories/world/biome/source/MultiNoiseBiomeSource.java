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
    private static final float ALTITUDE_BAND_MARGIN = 0.12f;
    private static final float TEMPERATURE_FREQUENCY = 0.0022f;
    private static final float HUMIDITY_FREQUENCY = 0.0022f;
    private static final float FALLBACK_ALTITUDE_FREQUENCY = 0.0016f;

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
        this.temperatureNoise = createNoise(noiseSeed, TEMPERATURE_FREQUENCY);
        this.humidityNoise = createNoise(noiseSeed + 1, HUMIDITY_FREQUENCY);
        this.altitudeNoise = createNoise(noiseSeed + 2, FALLBACK_ALTITUDE_FREQUENCY);
    }

    private static float normalize(float value) {
        return (value + 1.0f) * 0.5f;
    }

    private static float clamp01(float value) {
        return Math.clamp(value, 0, 1);
    }

    private static float clampSigned(float value) {
        return Math.clamp(value, -1, 1);
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

    private static float squaredDistance2D(
            float sampleTemperature, float sampleHumidity,
            float targetTemperature, float targetHumidity) {
        float temperatureDiff = sampleTemperature - targetTemperature;
        float humidityDiff = sampleHumidity - targetHumidity;
        return temperatureDiff * temperatureDiff + humidityDiff * humidityDiff;
    }

    @Override
    public Biome getBiome(int x, int z) {
        float terrainNoise = this.altitudeNoise.getNoise(x, z);
        return getBiome(x, z, terrainNoise);
    }

    @Override
    public Biome getBiome(int x, int z, float terrainNoise) {
        float temperatureSample = clamp01(normalize(this.temperatureNoise.getNoise(x, z)) * 0.9f);
        float humiditySample = clamp01(normalize(this.humidityNoise.getNoise(x, z)) * 1.1f);
        float altitudeSample = clampSigned(terrainNoise);

        Biome fallbackBiome = null;
        float bestAltitudeDiff = Float.MAX_VALUE;
        for (Biome biome : this.biomes) {
            ClimateProfile climate = biome.getClimateProfile();
            if (climate == null) {
                if (fallbackBiome == null) {
                    fallbackBiome = biome;
                }

                continue;
            }

            float altitudeTarget = sampleAltitude(climate.altitudeVariation(), x, z);
            float altitudeDiff = Math.abs(altitudeSample - altitudeTarget);
            if (altitudeDiff < bestAltitudeDiff) {
                bestAltitudeDiff = altitudeDiff;
            }
        }

        if (bestAltitudeDiff == Float.MAX_VALUE) {
            return Objects.requireNonNullElse(fallbackBiome, this.biomes.getFirst());
        }

        Biome closestBiome = null;
        float closestDistance = Float.MAX_VALUE;
        float altitudeThreshold = bestAltitudeDiff + ALTITUDE_BAND_MARGIN;
        for (Biome biome : this.biomes) {
            ClimateProfile climate = biome.getClimateProfile();
            if (climate == null) {
                if (closestBiome == null) {
                    closestBiome = biome;
                }

                continue;
            }

            float altitudeTarget = sampleAltitude(climate.altitudeVariation(), x, z);
            float altitudeDiff = Math.abs(altitudeSample - altitudeTarget);
            if (altitudeDiff > altitudeThreshold) {
                continue;
            }

            float climateDistance = squaredDistance2D(
                    temperatureSample, humiditySample,
                    climate.temperature(), climate.humidity());

            float distance = climateDistance + altitudeDiff * 0.01f;
            if (distance < closestDistance) {
                closestDistance = distance;
                closestBiome = biome;
            }
        }

        if (closestBiome != null) {
            return closestBiome;
        }

        Biome nearestAltitudeBiome = null;
        float nearestAltitudeDistance = Float.MAX_VALUE;
        for (Biome biome : this.biomes) {
            ClimateProfile climate = biome.getClimateProfile();
            if (climate == null) {
                if (nearestAltitudeBiome == null) {
                    nearestAltitudeBiome = biome;
                }
                continue;
            }

            float altitudeTarget = sampleAltitude(climate.altitudeVariation(), x, z);
            float distance = squaredDistance(
                    temperatureSample, humiditySample, altitudeSample,
                    climate.temperature(), climate.humidity(), altitudeTarget);
            if (distance < nearestAltitudeDistance) {
                nearestAltitudeDistance = distance;
                nearestAltitudeBiome = biome;
            }
        }

        return Objects.requireNonNullElse(nearestAltitudeBiome, Objects.requireNonNullElse(fallbackBiome, this.biomes.getFirst()));
    }

    private float sampleAltitude(FloatProvider provider, int x, int z) {
        if (provider == null)
            return 0.0f;

        long mixedSeed = this.seed ^ (long) x * X_PRIME ^ (long) z * Z_PRIME;
        return provider.get(new Random(mixedSeed));
    }
}
