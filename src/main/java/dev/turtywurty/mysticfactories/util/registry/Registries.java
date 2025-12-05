package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class Registries {
    private static final Map<Identifier, Registry<?>> REGISTRIES = new LinkedHashMap<>();

    public static <T extends Registerable> Registry<T> createRegistry(Identifier id, Identifier defaultId) {
        Registry<T> registry = new DefaultedRegistry<>(id);
        REGISTRIES.put(id, registry);
        return registry;
    }
}
