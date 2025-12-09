package dev.turtywurty.mysticfactories.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObservableProperty<T> {
    private final transient List<Consumer<T>> listeners = new ArrayList<>();
    private T value;

    public ObservableProperty(T initial) {
        this.value = initial;
    }

    public T get() {
        return this.value;
    }

    public void set(T newValue) {
        this.value = newValue;
        for (Consumer<T> listener : List.copyOf(this.listeners)) {
            listener.accept(newValue);
        }
    }

    public void addListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Consumer<T> listener) {
        this.listeners.remove(listener);
    }
}
