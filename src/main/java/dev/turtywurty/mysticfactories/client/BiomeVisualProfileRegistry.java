package dev.turtywurty.mysticfactories.client;

import dev.turtywurty.mysticfactories.client.world.biome.VisualProfile;
import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class BiomeVisualProfileRegistry {
    public static final BiomeVisualProfileRegistry INSTANCE = new BiomeVisualProfileRegistry();

    private final Map<Identifier, VisualProfile> lookupById = new HashMap<>();

    private BiomeVisualProfileRegistry() {
    }

    public void register(Identifier identifier, VisualProfile visualProfile) {
        if (contains(identifier))
            throw new IllegalArgumentException("A visual profile is already registered for the identifier: " + identifier);

        this.lookupById.put(identifier, visualProfile);
    }

    public void register(Biome biome, VisualProfile visualProfile) {
        register(biome.getId(), visualProfile);
    }

    public VisualProfile get(Identifier identifier) {
        return this.lookupById.get(identifier);
    }

    public VisualProfile get(Biome biome) {
        return get(biome.getId());
    }

    public boolean contains(Identifier identifier) {
        return this.lookupById.containsKey(identifier);
    }
}
