package dev.turtywurty.mysticfactories.world.seed;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public record StringSeedSource(String seedString) implements SeedSource {
    @Override
    public long get() {
        return Hashing.murmur3_128()
                .hashString(this.seedString, StandardCharsets.UTF_8)
                .asLong();
    }
}
