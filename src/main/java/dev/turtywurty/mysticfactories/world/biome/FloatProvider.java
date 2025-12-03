package dev.turtywurty.mysticfactories.world.biome;

import java.util.Random;

public abstract class FloatProvider {
    protected final Random random = new Random();

    public abstract float get(float x, float y);

    public static FloatProvider constant(float value) {
        return new FloatProvider() {
            @Override
            public float get(float x, float y) {
                return value;
            }
        };
    }

    public static FloatProvider noise(float scale, float amplitude) {
        return new FloatProvider() {
            @Override
            public float get(float x, float y) {
                random.setSeed((long) (x * 49632 + y * 325176 + 123456789));
                return ((random.nextFloat() * 2 - 1) * amplitude) / scale;
            }
        };
    }

    public static FloatProvider uniform(float min, float max) {
        return new FloatProvider() {
            @Override
            public float get(float x, float y) {
                random.setSeed((long) (x * 98765 + y * 43210 + 987654321));
                return min + random.nextFloat() * (max - min);
            }
        };
    }

    public static FloatProvider linearGradient(float startValue, float endValue, float startX, float endX) {
        return new FloatProvider() {
            @Override
            public float get(float x, float y) {
                if (x <= startX)
                    return startValue;

                if (x >= endX)
                    return endValue;

                float t = (x - startX) / (endX - startX);
                return startValue + t * (endValue - startValue);
            }
        };
    }

    public static FloatProvider composite(FloatProvider a, FloatProvider b, float weight) {
        return new FloatProvider() {
            @Override
            public float get(float x, float y) {
                return a.get(x, y) * (1 - weight) + b.get(x, y) * weight;
            }
        };
    }

    public static FloatProvider scaled(FloatProvider provider, float scale) {
        return new FloatProvider() {
            @Override
            public float get(float x, float y) {
                return provider.get(x, y) * scale;
            }
        };
    }

    public static FloatProvider trapezoidal(float min, float lowerMax, float upperMin, float max) {
        return new FloatProvider() {
            @Override
            public float get(float x, float y) {
                if (x < min || x > max)
                    return 0.0f;

                if (x >= lowerMax && x <= upperMin)
                    return 1.0f;

                if (x >= min && x < lowerMax)
                    return (x - min) / (lowerMax - min);

                return (max - x) / (max - upperMin);
            }
        };
    }
}
