package dev.turtywurty.mysticfactories.world.biome;

import java.util.Random;

public abstract class IntProvider {
    public static IntProvider constant(int value) {
        return new IntProvider() {
            @Override
            public int get(Random random) {
                return value;
            }
        };
    }

    public static IntProvider noise(float scale, float amplitude) {
        return new IntProvider() {
            @Override
            public int get(Random random) {
                return (int) (((random.nextFloat() * 2 - 1) * amplitude) / scale);
            }
        };
    }

    public static IntProvider uniform(int min, int max) {
        return new IntProvider() {
            @Override
            public int get(Random random) {
                return min + random.nextInt(max - min + 1);
            }
        };
    }

    public static IntProvider linearGradient(int startValue, int endValue, int startX, int endX) {
        return new IntProvider() {
            @Override
            public int get(Random random) {
                float position = startX + random.nextFloat() * (endX - startX);
                float t = (position - startX) / (endX - startX);
                return (int) (startValue + t * (endValue - startValue));
            }
        };
    }

    public static IntProvider composite(IntProvider a, IntProvider b, float weight) {
        return new IntProvider() {
            @Override
            public int get(Random random) {
                return (int) (a.get(random) * (1 - weight) + b.get(random) * weight);
            }
        };
    }

    public static IntProvider scaled(IntProvider provider, int scale) {
        return new IntProvider() {
            @Override
            public int get(Random random) {
                return provider.get(random) * scale;
            }
        };
    }

    public static IntProvider trapezoidal(int min, int lowerMax, int upperMin, int max) {
        return new IntProvider() {
            @Override
            public int get(Random random) {
                float sampleX = min + random.nextFloat() * (max - min);
                if (sampleX < min || sampleX > max)
                    return 0;

                if (sampleX >= lowerMax && sampleX <= upperMin)
                    return 1;

                if (sampleX >= min && sampleX < lowerMax)
                    return (int) ((sampleX - min) / (lowerMax - min));

                return (int) ((max - sampleX) / (max - upperMin));
            }
        };
    }

    public abstract int get(Random random);
}
