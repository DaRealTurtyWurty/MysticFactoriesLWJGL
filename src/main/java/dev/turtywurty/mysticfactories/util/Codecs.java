package dev.turtywurty.mysticfactories.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.joml.Vector2d;

import java.util.List;

public final class Codecs {
    private Codecs() {
    }

    public static final Codec<AABB> AABB = Codec.DOUBLE.listOf().comapFlatMap(list -> {
        if (list.size() != 4) {
            return DataResult.error(() -> "Expected list of 4 doubles for AABB, got " + list.size());
        }
        return DataResult.success(new AABB(list.get(0), list.get(1), list.get(2), list.get(3)));
    }, aabb -> List.of(aabb.minX(), aabb.minY(), aabb.maxX(), aabb.maxY()));

    public static final Codec<java.util.UUID> UUID = Codec.STRING.comapFlatMap(str -> {
        try {
            return DataResult.success(java.util.UUID.fromString(str));
        } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Invalid UUID string: " + str);
        }
    }, java.util.UUID::toString);

    public static final Codec<Vector2d> VECTOR2D = Codec.DOUBLE.listOf().comapFlatMap(list -> {
        if (list.size() != 2) {
            return DataResult.error(() -> "Expected list of 2 doubles for Vector2d, got " + list.size());
        }
        return DataResult.success(new Vector2d(list.get(0), list.get(1)));
    }, vec -> List.of(vec.x, vec.y));
}
