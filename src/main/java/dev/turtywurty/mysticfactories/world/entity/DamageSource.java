package dev.turtywurty.mysticfactories.world.entity;

import lombok.Getter;

@Getter
public enum DamageSource {
    CACTUS(200);

    private final long cooldownMs;

    DamageSource(long cooldownMs) {
        this.cooldownMs = cooldownMs;
    }
}
