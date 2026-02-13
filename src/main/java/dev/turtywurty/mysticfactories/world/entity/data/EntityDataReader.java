package dev.turtywurty.mysticfactories.world.entity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import dev.turtywurty.mysticfactories.util.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Generic, codec-backed reader for entity data. Uses {@link DynamicOps} to read values
 * from any supported tree format.
 */
public class EntityDataReader<T> {
    private final DynamicOps<T> ops;
    private final T root;
    private final MapLike<T> values;

    public EntityDataReader(DynamicOps<T> ops, T root) {
        this.ops = Objects.requireNonNull(ops, "ops");
        this.root = Objects.requireNonNull(root, "root");
        this.values = ops.getMap(root).getOrThrow(asError("Expected entity data to be a map"));
    }

    public DynamicOps<T> ops() {
        return this.ops;
    }

    public T root() {
        return this.root;
    }

    public boolean has(String key) {
        return this.values.get(key) != null;
    }

    public <V> DataResult<V> read(String key, Codec<V> codec) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(codec, "codec");

        T element = this.values.get(key);
        if (element == null)
            return DataResult.error(() -> "Missing required field '" + key + "'");

        return codec.parse(this.ops, element);
    }

    public <V> Optional<V> readOptional(String key, Codec<V> codec) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(codec, "codec");

        T element = this.values.get(key);
        if (element == null)
            return Optional.empty();

        return codec.parse(this.ops, element).result();
    }

    public <V> V readOrThrow(String key, Codec<V> codec) {
        return read(key, codec).getOrThrow(asError("Failed to decode field '" + key + "'"));
    }

    public <V> V readOrDefault(String key, Codec<V> codec, V defaultValue) {
        return readOptional(key, codec).orElse(defaultValue);
    }

    @Nullable
    public <V> V readNullable(String key, Codec<V> codec) {
        return readOptional(key, codec).orElse(null);
    }

    public boolean readBoolean(String key) {
        return readOrThrow(key, Codec.BOOL);
    }

    public boolean readBooleanOrDefault(String key, boolean defaultValue) {
        return readOrDefault(key, Codec.BOOL, defaultValue);
    }

    public Optional<Boolean> readOptionalBoolean(String key) {
        return readOptional(key, Codec.BOOL);
    }

    public byte readByte(String key) {
        return readOrThrow(key, Codec.BYTE);
    }

    public byte readByteOrDefault(String key, byte defaultValue) {
        return readOrDefault(key, Codec.BYTE, defaultValue);
    }

    public Optional<Byte> readOptionalByte(String key) {
        return readOptional(key, Codec.BYTE);
    }

    public short readShort(String key) {
        return readOrThrow(key, Codec.SHORT);
    }

    public short readShortOrDefault(String key, short defaultValue) {
        return readOrDefault(key, Codec.SHORT, defaultValue);
    }

    public Optional<Short> readOptionalShort(String key) {
        return readOptional(key, Codec.SHORT);
    }

    public int readInt(String key) {
        return readOrThrow(key, Codec.INT);
    }

    public int readIntOrDefault(String key, int defaultValue) {
        return readOrDefault(key, Codec.INT, defaultValue);
    }

    public Optional<Integer> readOptionalInt(String key) {
        return readOptional(key, Codec.INT);
    }

    public long readLong(String key) {
        return readOrThrow(key, Codec.LONG);
    }

    public long readLongOrDefault(String key, long defaultValue) {
        return readOrDefault(key, Codec.LONG, defaultValue);
    }

    public Optional<Long> readOptionalLong(String key) {
        return readOptional(key, Codec.LONG);
    }

    public float readFloat(String key) {
        return readOrThrow(key, Codec.FLOAT);
    }

    public float readFloatOrDefault(String key, float defaultValue) {
        return readOrDefault(key, Codec.FLOAT, defaultValue);
    }

    public Optional<Float> readOptionalFloat(String key) {
        return readOptional(key, Codec.FLOAT);
    }

    public double readDouble(String key) {
        return readOrThrow(key, Codec.DOUBLE);
    }

    public double readDoubleOrDefault(String key, double defaultValue) {
        return readOrDefault(key, Codec.DOUBLE, defaultValue);
    }

    public Optional<Double> readOptionalDouble(String key) {
        return readOptional(key, Codec.DOUBLE);
    }

    public String readString(String key) {
        return readOrThrow(key, Codec.STRING);
    }

    public String readStringOrDefault(String key, String defaultValue) {
        return readOrDefault(key, Codec.STRING, defaultValue);
    }

    public Optional<String> readOptionalString(String key) {
        return readOptional(key, Codec.STRING);
    }

    public UUID readUuid(String key) {
        return readOrThrow(key, Codecs.UUID);
    }

    public UUID readUuidOrDefault(String key, UUID defaultValue) {
        return readOrDefault(key, Codecs.UUID, defaultValue);
    }

    public Optional<UUID> readOptionalUuid(String key) {
        return readOptional(key, Codecs.UUID);
    }

    /**
     * Reads a value and spreads the result into the provided consumer if successful, otherwise throws an exception.
     */
    public <V> void readAndConsume(String key, Codec<V> codec, Consumer<V> consumer) {
        read(key, codec).ifSuccess(consumer).ifError(error -> {
            throw asError("Failed to decode field '" + key + "'").apply(error.message());
        });
    }

    private Function<String, RuntimeException> asError(String prefix) {
        return message -> new IllegalStateException(prefix + ": " + message);
    }
}
