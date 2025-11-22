package dev.turtywurty.mysticfactories.world.entity.attribs;

import com.mojang.serialization.Codec;
import dev.turtywurty.mysticfactories.util.Identifier;

public class AttributeKeys {
    public static final AttributeKey<Float> MAX_HEALTH = new AttributeKey<>(
            Identifier.of("max_health"),
            Float.class,
            20.0f,
            Codec.FLOAT
    );

    public static final AttributeKey<Double> MOVEMENT_SPEED = new AttributeKey<>(
            Identifier.of("movement_speed"),
            Double.class,
            0.1,
            Codec.DOUBLE
    );

    public static final AttributeKey<Float> ATTACK_DAMAGE = new AttributeKey<>(
            Identifier.of("attack_damage"),
            Float.class,
            2.0f,
            Codec.FLOAT
    );
}
