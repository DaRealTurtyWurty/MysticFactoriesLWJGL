package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class DefaultedRegistry<T extends Registerable> implements Registry<T> {
    private final Map<Identifier, RegistryObject<T>> entries = new ConcurrentHashMap<>();
    private final Identifier defaultId;

    public DefaultedRegistry(Identifier defaultId) {
        this.defaultId = defaultId;
    }

    @Override
    public RegistryObject<T> register(Identifier id, Supplier<T> entry) {
        return this.entries.computeIfAbsent(id, key -> RegistryObject.of(key, entry));
    }

    @Override
    public RegistryObject<T> get(Identifier id) {
        return this.entries.getOrDefault(id, this.entries.get(this.defaultId));
    }

    @Override
    public RegistryObject<T> getOrThrow(Identifier id) {
        RegistryObject<T> obj = this.entries.get(id);
        if (obj == null)
            throw new IllegalArgumentException("No entry found for id: " + id);

        return obj;
    }

    @Override
    public Optional<RegistryObject<T>> getOptional(Identifier id) {
        return Optional.ofNullable(this.entries.get(id));
    }

    @Override
    public boolean isRegistered(Identifier id) {
        return this.entries.containsKey(id);
    }

    @Override
    public boolean isRegistered(T entry) {
        return this.entries.values().stream().anyMatch(obj -> obj.get() == entry);
    }

    @Override
    public Iterable<RegistryObject<T>> getAll() {
        return this.entries.values();
    }

    @Override
    public Map<Identifier, RegistryObject<T>> getEntries() {
        return Map.copyOf(this.entries);
    }

    @Override
    public Set<Identifier> getIds() {
        return this.entries.keySet();
    }

    @Override
    public Set<RegistryObject<T>> getValues() {
        return Set.copyOf(this.entries.values());
    }

    @Override
    public int size() {
        return this.entries.size();
    }
}
