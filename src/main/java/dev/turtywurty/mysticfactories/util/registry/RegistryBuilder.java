package dev.turtywurty.mysticfactories.util.registry;

import dev.turtywurty.mysticfactories.util.Identifier;

public abstract class RegistryBuilder<T extends Registerable> {
    public static <T extends Registerable> RegistryBuilder<T> defaulted(RegistryKey<T> key) {
        return new Default<>(key);
    }

    public abstract Registry<T> build(Class<T> type);

    public static class Default<T extends Registerable> extends RegistryBuilder<T> {
        private final RegistryKey<T> key;
        private Identifier defaultId;

        public Default(RegistryKey<T> key) {
            this.key = key;
        }

        public Default<T> defaultId(Identifier defaultId) {
            this.defaultId = defaultId;
            return this;
        }

        @Override
        public Registry<T> build(Class<T> type) {
            return Registries.createRegistry(this.key, this.defaultId);
        }
    }
}
