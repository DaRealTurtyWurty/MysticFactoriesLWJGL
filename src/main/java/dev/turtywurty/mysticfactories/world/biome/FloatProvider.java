package dev.turtywurty.mysticfactories.world.biome;

import java.util.Random;

public abstract class FloatProvider {
    public static FloatProvider constant(float value) {
        return new FloatProvider() {
            @Override
            public float get(Random random) {
                return value;
            }
        };
    }

    public static FloatProvider noise(float scale, float amplitude) {
        return new FloatProvider() {
            @Override
            public float get(Random random) {
                return ((random.nextFloat() * 2 - 1) * amplitude) / scale;
            }
        };
    }

    public static FloatProvider uniform(float min, float max) {
        return new FloatProvider() {
            @Override
            public float get(Random random) {
                return min + random.nextFloat() * (max - min);
            }
        };
    }

    public static FloatProvider linearGradient(float startValue, float endValue, float startX, float endX) {
        return new FloatProvider() {
            @Override
            public float get(Random random) {
                float position = startX + random.nextFloat() * (endX - startX);
                float t = (position - startX) / (endX - startX);
                return startValue + t * (endValue - startValue);
            }
        };
    }

    public static FloatProvider composite(FloatProvider a, FloatProvider b, float weight) {
        return new FloatProvider() {
            @Override
            public float get(Random random) {
                return a.get(random) * (1 - weight) + b.get(random) * weight;
            }
        };
    }

    public static FloatProvider scaled(FloatProvider provider, float scale) {
        return new FloatProvider() {
            @Override
            public float get(Random random) {
                return provider.get(random) * scale;
            }
        };
    }

    public static FloatProvider trapezoidal(float min, float lowerMax, float upperMin, float max) {
        return new FloatProvider() {
            @Override
            public float get(Random random) {
                float sampleX = min + random.nextFloat() * (max - min);
                if (sampleX < min || sampleX > max)
                    return 0.0f;

                if (sampleX >= lowerMax && sampleX <= upperMin)
                    return 1.0f;

                if (sampleX >= min && sampleX < lowerMax)
                    return (sampleX - min) / (lowerMax - min);

                return (max - sampleX) / (max - upperMin);
            }
        };
    }

    public abstract float get(Random random);
}
