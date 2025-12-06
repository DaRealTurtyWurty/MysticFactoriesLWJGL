package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;
import lombok.Getter;

@Getter
public final class RegistryKey<T extends Registerable> {
    private final Identifier registryId;

    public RegistryKey(Identifier registryId) {
        this.registryId = registryId;
    }

    public static <T extends Registerable> RegistryKey<T> of(String registryId) {
        return new RegistryKey<>(Identifier.of(registryId));
    }
}
