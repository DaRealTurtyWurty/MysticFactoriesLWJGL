package dev.turtywurty.mysticfactories.world.biome;

import java.util.Random;

public abstract class IntProvider {
    protected final Random random = new Random();

    public abstract int get(float x, float y);

    public static IntProvider constant(int value) {
        return new IntProvider() {
            @Override
            public int get(float x, float y) {
                return value;
            }
        };
    }

    public static IntProvider noise(float scale, float amplitude) {
        return new IntProvider() {
            @Override
            public int get(float x, float y) {
                random.setSeed((long) (x * 49632 + y * 325176 + 123456789));
                return (int) (((random.nextFloat() * 2 - 1) * amplitude) / scale);
            }
        };
    }

    public static IntProvider uniform(int min, int max) {
        return new IntProvider() {
            @Override
            public int get(float x, float y) {
                random.setSeed((long) (x * 98765 + y * 43210 + 987654321));
                return min + random.nextInt(max - min + 1);
            }
        };
    }

    public static IntProvider linearGradient(int startValue, int endValue, int startX, int endX) {
        return new IntProvider() {
            @Override
            public int get(float x, float y) {
                if (x <= startX)
                    return startValue;

                if (x >= endX)
                    return endValue;

                float t = (x - startX) / (endX - startX);
                return (int) (startValue + t * (endValue - startValue));
            }
        };
    }

    public static IntProvider composite(IntProvider a, IntProvider b, float weight) {
        return new IntProvider() {
            @Override
            public int get(float x, float y) {
                return (int) (a.get(x, y) * (1 - weight) + b.get(x, y) * weight);
            }
        };
    }

    public static IntProvider scaled(IntProvider provider, int scale) {
        return new IntProvider() {
            @Override
            public int get(float x, float y) {
                return provider.get(x, y) * scale;
            }
        };
    }

    public static IntProvider trapezoidal(int min, int lowerMax, int upperMin, int max) {
        return new IntProvider() {
            @Override
            public int get(float x, float y) {
                if (x < min || x > max)
                    return 0;

                if (x >= lowerMax && x <= upperMin)
                    return 1;

                if (x >= min && x < lowerMax)
                    return (int) ((x - min) / (lowerMax - min));

                return (int) ((max - x) / (max - upperMin));
            }
        };
    }
}
