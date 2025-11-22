package dev.turtywurty.mysticfactories.world.entity.attribs;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Attribute<T> {
    private final AttributeKey<T> key;
    @Setter
    private T baseValue;

    public Attribute(AttributeKey<T> key, T baseValue) {
        this.key = key;
        this.baseValue = baseValue;
    }
}
