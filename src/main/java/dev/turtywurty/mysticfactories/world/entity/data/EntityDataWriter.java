package dev.turtywurty.mysticfactories.world.entity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Generic, codec-backed writer for entity data. Uses {@link DynamicOps} so that the same
 * writer works for any tree format supported by DataFixerUpper (JSON, NBT, etc).
 */
public class EntityDataWriter<T> {
    private final DynamicOps<T> ops;
    private final RecordBuilder<T> builder;

    public EntityDataWriter(DynamicOps<T> ops) {
        this.ops = Objects.requireNonNull(ops, "ops");
        this.builder = ops.mapBuilder();
    }

    public DynamicOps<T> ops() {
        return this.ops;
    }

    /**
     * Encode a value into the backing data structure using the provided codec.
     */
    public <V> EntityDataWriter<T> write(String key, Codec<V> codec, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(codec, "codec");
        Objects.requireNonNull(value, "value");

        T encoded = encodeOrThrow(codec, value, key);
        this.builder.add(key, encoded);
        return this;
    }

    public EntityDataWriter<T> writeBoolean(String key, boolean value) {
        return write(key, Codec.BOOL, value);
    }

    public EntityDataWriter<T> writeByte(String key, byte value) {
        return write(key, Codec.BYTE, value);
    }

    public EntityDataWriter<T> writeShort(String key, short value) {
        return write(key, Codec.SHORT, value);
    }

    public EntityDataWriter<T> writeInt(String key, int value) {
        return write(key, Codec.INT, value);
    }

    public EntityDataWriter<T> writeLong(String key, long value) {
        return write(key, Codec.LONG, value);
    }

    public EntityDataWriter<T> writeFloat(String key, float value) {
        return write(key, Codec.FLOAT, value);
    }

    public EntityDataWriter<T> writeDouble(String key, double value) {
        return write(key, Codec.DOUBLE, value);
    }

    public EntityDataWriter<T> writeString(String key, String value) {
        return write(key, Codec.STRING, value);
    }

    /**
     * Encode a nullable value only when present.
     */
    public <V> EntityDataWriter<T> writeNullable(String key, Codec<V> codec, @Nullable V value) {
        if (value != null) {
            write(key, codec, value);
        }
        return this;
    }

    /**
     * Encode an optional value when present.
     */
    public <V> EntityDataWriter<T> writeOptional(String key, Codec<V> codec, Optional<V> value) {
        value.ifPresent(v -> write(key, codec, v));
        return this;
    }

    public DataResult<T> build() {
        return this.builder.build(this.ops.emptyMap());
    }

    public T buildOrThrow() {
        return build().getOrThrow(asError("Failed to finalize entity data"));
    }

    private <V> T encodeOrThrow(Codec<V> codec, V value, String key) {
        return codec.encodeStart(this.ops, value)
                .getOrThrow(asError("Failed to encode field '" + key + "'"));
    }

    private Function<String, RuntimeException> asError(String prefix) {
        return message -> new IllegalStateException(prefix + ": " + message);
    }
}
