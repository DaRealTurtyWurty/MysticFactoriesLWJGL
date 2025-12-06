package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Registry<T extends Registerable> {
    <U extends T> U register(Identifier id, U entry);

    T get(Identifier id);

    T getOrThrow(Identifier id);

    Optional<T> getOptional(Identifier id);

    void freeze();

    boolean isFrozen();

    boolean isRegistered(Identifier id);

    boolean isRegistered(T entry);

    Iterable<T> getAll();

    Map<Identifier, T> getEntries();

    Set<Identifier> getIds();

    Set<T> getValues();

    int size();
}
