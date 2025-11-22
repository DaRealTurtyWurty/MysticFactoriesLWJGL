package dev.turtywurty.mysticfactories.world.entity.attribs;

import com.mojang.serialization.Codec;
import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.Objects;

public record AttributeKey<T>(Identifier id, Class<T> dataClass, T defaultValue, Codec<T> codec) {
    public AttributeKey {
        Objects.requireNonNull(id, "name");
        Objects.requireNonNull(dataClass, "dataClass");
        Objects.requireNonNull(codec, "codec");
    }
}
