package dev.turtywurty.mysticfactories.world.seed;

import java.util.concurrent.ThreadLocalRandom;

public class RandomSeedSource implements SeedSource {
    @Override
    public long get() {
        return ThreadLocalRandom.current().nextLong();
    }
}
