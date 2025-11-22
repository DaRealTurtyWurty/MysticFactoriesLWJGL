package dev.turtywurty.mysticfactories.world.entity.attribs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.turtywurty.mysticfactories.util.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AttributeMap {
    private final Map<AttributeKey<?>, Attribute<?>> attributes = new HashMap<>();

    public static Builder builder() {
        return new Builder();
    }

    private static <T> DataResult<T> encodeAttributes(AttributeMap map, DynamicOps<T> ops, T prefix) {
        RecordBuilder<T> builder = ops.mapBuilder();
        for (Map.Entry<AttributeKey<?>, Attribute<?>> entry : map.entries().entrySet()) {
            AttributeKey<?> key = entry.getKey();
            Attribute<?> attribute = entry.getValue();

            @SuppressWarnings("unchecked")
            Codec<Object> codec = (Codec<Object>) key.codec();
            Object value = attribute.getBaseValue();

            DataResult<T> encoded = codec.encodeStart(ops, value);
            builder.add(key.id().toString(), encoded);
        }

        return builder.build(prefix);
    }

    private static <T> DataResult<Pair<AttributeMap, T>> decodeAttributes(DynamicOps<T> ops, T input, Function<Identifier, AttributeKey<?>>
            keyResolver) {
        DataResult<MapLike<T>> mapResult = ops.getMap(input);
        if (mapResult.error().isPresent())
            return mapResult.map(map -> Pair.of(new AttributeMap(), input));

        MapLike<T> values = mapResult.result().orElseThrow();
        AttributeMap attributeMap = new AttributeMap();

        var entries = values.entries().toList();
        for (var entry : entries) {
            DataResult<String> keyResult = ops.getStringValue(entry.getFirst());
            if (keyResult.error().isPresent())
                return keyResult.map(str -> Pair.of(attributeMap, input));

            String keyString = keyResult.result().orElse("");
            Identifier id;
            try {
                id = Identifier.parse(keyString);
            } catch (IllegalArgumentException ex) {
                return DataResult.error(ex::getMessage);
            }

            AttributeKey<?> key = keyResolver.apply(id);
            if (key == null)
                return DataResult.error(() -> "Unknown attribute key '" + keyString + "'");

            @SuppressWarnings("unchecked")
            Codec<Object> codec = (Codec<Object>) key.codec();
            DataResult<Object> valueResult = codec.parse(ops, entry.getSecond());
            if (valueResult.error().isPresent())
                return valueResult.map(v -> Pair.of(attributeMap, input));

            Object value = valueResult.result().orElse(null);
            @SuppressWarnings("unchecked")
            AttributeKey<Object> typedKey = (AttributeKey<Object>) key;
            attributeMap.putUnchecked(typedKey, new Attribute<>(typedKey, value));
        }

        return DataResult.success(Pair.of(attributeMap, input));
    }

    public <T> void put(AttributeKey<T> key, Attribute<T> attribute) {
        this.attributes.put(key, attribute);
    }

    public <T> Attribute<T> get(AttributeKey<T> key) {
        @SuppressWarnings("unchecked")
        Attribute<T> attribute = (Attribute<T>) this.attributes.get(key);
        return attribute;
    }

    public boolean has(AttributeKey<?> key) {
        return this.attributes.containsKey(key);
    }

    Map<AttributeKey<?>, Attribute<?>> entries() {
        return Collections.unmodifiableMap(this.attributes);
    }

    void putUnchecked(AttributeKey<?> key, Attribute<?> attribute) {
        this.attributes.put(key, attribute);
    }

    public Map<AttributeKey<?>, Attribute<?>> asMapView() {
        return Collections.unmodifiableMap(this.attributes);
    }

    public void copyFrom(AttributeMap other) {
        this.attributes.clear();
        this.attributes.putAll(other.attributes);
    }

    public void clear() {
        this.attributes.clear();
    }

    public Codec<AttributeMap> codec() {
        Encoder<AttributeMap> encoder = AttributeMap::encodeAttributes;
        Decoder<AttributeMap> decoder = new Decoder<>() {
            @Override
            public <T> DataResult<Pair<AttributeMap, T>> decode(DynamicOps<T> ops, T input) {
                return decodeAttributes(ops, input, identifier -> getKeyFromId(identifier));
            }
        };
        return Codec.of(encoder, decoder);
    }

    public AttributeKey<?> getKeyFromId(Identifier id) {
        return entries().keySet().stream()
                .filter(key -> key.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public <T> T getOrThrow(AttributeKey<T> key, Class<T> clazz) {
        Attribute<T> attribute = get(key);
        if (attribute == null)
            throw new IllegalArgumentException("AttributeMap does not contain attribute for key: " + key.id());

        T value = attribute.getBaseValue();
        if (!clazz.isInstance(value))
            throw new IllegalArgumentException("Attribute value for key: " + key.id() + " is not of type: " + clazz.getSimpleName());

        return value;
    }

    public double getDouble(AttributeKey<Double> key) {
        return getOrThrow(key, Double.class);
    }

    public float getFloat(AttributeKey<Float> key) {
        return getOrThrow(key, Float.class);
    }

    public int getInt(AttributeKey<Integer> key) {
        return getOrThrow(key, Integer.class);
    }

    public boolean getBoolean(AttributeKey<Boolean> key) {
        return getOrThrow(key, Boolean.class);
    }

    public static class Builder {
        private final AttributeMap attributeMap = new AttributeMap();

        public <T> Builder put(AttributeKey<T> key, T baseValue) {
            this.attributeMap.put(key, new Attribute<>(key, baseValue));
            return this;
        }

        public AttributeMap build() {
            return this.attributeMap;
        }

        public boolean hasAttribute(AttributeKey<?> key) {
            return this.attributeMap.has(key);
        }
    }
}
