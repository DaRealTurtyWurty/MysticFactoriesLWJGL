package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;
import lombok.Getter;

import java.util.function.Supplier;

public class RegistryObject<T extends Registerable> implements Supplier<T> {
    @Getter
    private final Identifier id;
    private final Supplier<T> supplier;

    private RegistryObject(Identifier id, Supplier<T> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return supplier.get();
    }

    protected static <T extends Registerable> RegistryObject<T> of(Identifier id, Supplier<T> supplier) {
        return new RegistryObject<>(id, supplier);
    }
}
