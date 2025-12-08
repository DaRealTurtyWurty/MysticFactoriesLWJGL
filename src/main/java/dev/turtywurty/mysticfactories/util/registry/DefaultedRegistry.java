package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultedRegistry<T extends Registerable> implements Registry<T> {
    private final Map<Identifier, T> entries = new ConcurrentHashMap<>();
    private final Identifier defaultId;
    private volatile boolean frozen = false;

    public DefaultedRegistry(Identifier defaultId) {
        this.defaultId = defaultId;
    }

    @Override
    public <U extends T> U register(Identifier id, U entry) {
        if (this.frozen)
            throw new RegistryFrozenException("Cannot register entry '" + id + "' because the registry is frozen");

        if (this.entries.containsKey(id))
            throw new IllegalStateException("Entry already registered for id: " + id);

        entry.setId(id);

        this.entries.put(id, Objects.requireNonNull(entry, "Registry entry cannot be null"));
        return entry;
    }

    @Override
    public T get(Identifier id) {
        return this.entries.getOrDefault(id, this.entries.get(this.defaultId));
    }

    @Override
    public T getOrThrow(Identifier id) {
        T obj = this.entries.get(id);
        if (obj == null)
            throw new IllegalArgumentException("No entry found for id: " + id);

        return obj;
    }

    @Override
    public Optional<T> getOptional(Identifier id) {
        return Optional.ofNullable(this.entries.get(id));
    }

    @Override
    public void freeze() {
        this.frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return this.frozen;
    }

    @Override
    public boolean isRegistered(Identifier id) {
        return this.entries.containsKey(id);
    }

    @Override
    public boolean isRegistered(T entry) {
        return this.entries.values().stream().anyMatch(obj -> obj == entry);
    }

    @Override
    public Iterable<T> getAll() {
        return this.entries.values();
    }

    @Override
    public Map<Identifier, T> getEntries() {
        return Map.copyOf(this.entries);
    }

    @Override
    public Set<Identifier> getIds() {
        return this.entries.keySet();
    }

    @Override
    public Set<T> getValues() {
        return Set.copyOf(this.entries.values());
    }

    @Override
    public int size() {
        return this.entries.size();
    }
}
