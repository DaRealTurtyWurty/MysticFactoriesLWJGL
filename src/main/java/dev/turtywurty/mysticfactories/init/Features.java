package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.util.registry.Registries;
import dev.turtywurty.mysticfactories.util.registry.RegistryHolder;
import dev.turtywurty.mysticfactories.world.feature.Feature;
import dev.turtywurty.mysticfactories.world.feature.impl.CactusFeature;

@RegistryHolder
public class Features {
    public static final Feature CACTUS = register("cactus", new CactusFeature());

    private Features() {
    }

    public static Feature register(String name, Feature feature) {
        return Registries.FEATURES.register(Identifier.of(name), feature);
    }
}
