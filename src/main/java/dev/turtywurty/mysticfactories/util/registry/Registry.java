package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Registry<T extends Registerable> {
    RegistryObject<T> register(Identifier id, Supplier<T> entry);

    RegistryObject<T> get(Identifier id);
    RegistryObject<T> getOrThrow(Identifier id);
    Optional<RegistryObject<T>> getOptional(Identifier id);

    boolean isRegistered(Identifier id);
    boolean isRegistered(T entry);

    Iterable<RegistryObject<T>> getAll();
    Map<Identifier, RegistryObject<T>> getEntries();
    Set<Identifier> getIds();
    Set<RegistryObject<T>> getValues();

    int size();
}
