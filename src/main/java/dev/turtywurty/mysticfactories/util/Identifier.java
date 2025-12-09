package dev.turtywurty.mysticfactories.util;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

public record Identifier(String namespace, String path) {
    public static final Codec<Identifier> CODEC = Codec.STRING.xmap(Identifier::parse, Identifier::toString);

    public Identifier {
        if (namespace == null || namespace.isBlank())
            throw new IllegalArgumentException("Namespace cannot be null/blank");

        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null/blank");
    }

    public static Identifier of(String path) {
        return new Identifier("mysticfactories", path);
    }

    public static Identifier parse(String value) {
        String[] split = value.split(":", 2);
        if (split.length != 2)
            throw new IllegalArgumentException("Identifier must be in the form 'namespace:path': " + value);

        return new Identifier(split[0], split[1]);
    }

    @NotNull
    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }
}
