package dev.turtywurty.mysticfactories.world.entity.impl;

import dev.turtywurty.mysticfactories.init.EntityTypes;
import dev.turtywurty.mysticfactories.util.AABB;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.attribs.AttributeKeys;
import dev.turtywurty.mysticfactories.world.entity.attribs.AttributeMap;
import org.jetbrains.annotations.Nullable;

public class PlayerEntity extends LivingEntity {
    public PlayerEntity() {
        super(EntityTypes.PLAYER);
    }

    public PlayerEntity(@Nullable World world) {
        super(EntityTypes.PLAYER, world);
    }

    @Override
    public AABB getAABB() {
        return null;
    }

    @Override
    public void tick(double delta) {

    }

    @Override
    public AttributeMap.Builder createAttributes() {
        return AttributeMap.builder()
                .put(AttributeKeys.MAX_HEALTH, 20f)
                .put(AttributeKeys.MOVEMENT_SPEED, 0.1)
                .put(AttributeKeys.ATTACK_DAMAGE, 1f);
    }
}
