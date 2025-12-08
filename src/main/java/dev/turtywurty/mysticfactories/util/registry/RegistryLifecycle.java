package dev.turtywurty.mysticfactories.util.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RegistryLifecycle {
    private final List<RegistryKey<?>> order = new ArrayList<>();

    public RegistryLifecycle add(RegistryKey<?> key) {
        Objects.requireNonNull(key, "Registry key cannot be null");
        this.order.remove(key);
        this.order.add(key);
        return this;
    }

    public RegistryLifecycle addBefore(RegistryKey<?> target, RegistryKey<?> key) {
        Objects.requireNonNull(target, "Target registry key cannot be null");
        Objects.requireNonNull(key, "Registry key cannot be null");

        int targetIndex = this.order.indexOf(target);
        if (targetIndex < 0)
            throw new IllegalArgumentException("Target registry key is not in the lifecycle: " + target.getRegistryId());

        this.order.remove(key);
        this.order.add(targetIndex, key);
        return this;
    }

    public RegistryLifecycle addAfter(RegistryKey<?> target, RegistryKey<?> key) {
        Objects.requireNonNull(target, "Target registry key cannot be null");
        Objects.requireNonNull(key, "Registry key cannot be null");

        int targetIndex = this.order.indexOf(target);
        if (targetIndex < 0)
            throw new IllegalArgumentException("Target registry key is not in the lifecycle: " + target.getRegistryId());

        this.order.remove(key);
        this.order.add(targetIndex + 1, key);
        return this;
    }

    public List<RegistryKey<?>> getOrdered() {
        return Collections.unmodifiableList(this.order);
    }

    public boolean contains(RegistryKey<?> key) {
        return this.order.contains(key);
    }

    public int size() {
        return this.order.size();
    }

    /**
     * Freeze registries in the explicit order provided. This prevents further registrations.
     */
    public void freeze() {
        for (RegistryKey<?> key : this.order) {
            Registry<?> registry = Registries.getRegistry(key);
            registry.freeze();
        }
    }
}
