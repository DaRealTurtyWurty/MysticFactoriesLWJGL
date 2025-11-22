package dev.turtywurty.mysticfactories.world.entity.impl;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.*;
import dev.turtywurty.mysticfactories.world.entity.attribs.AttributeKeys;
import dev.turtywurty.mysticfactories.world.entity.attribs.AttributeMap;
import dev.turtywurty.mysticfactories.world.entity.data.EntityDataReader;
import dev.turtywurty.mysticfactories.world.entity.data.EntityDataWriter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LivingEntity extends Entity {
    protected final AttributeMap attributes;
    protected boolean noAI = false;
    @Getter
    protected float health;

    public LivingEntity(@NotNull EntityType<? extends LivingEntity> type) {
        super(type);
        this.attributes = createAttributes().build();
    }

    public LivingEntity(@NotNull EntityType<? extends LivingEntity> type, @Nullable World world) {
        super(type, world);
        this.attributes = createAttributes().build();
    }

    private AttributeMap requireLivingAttributes(AttributeMap.Builder builder) {
        if (builder == null)
            throw new IllegalStateException("AttributeMap.Builder cannot be null");

        if (!builder.hasAttribute(AttributeKeys.MAX_HEALTH))
            throw new IllegalStateException(getClass().getSimpleName() + " must have MAX_HEALTH attribute");
        if (!builder.hasAttribute(AttributeKeys.MOVEMENT_SPEED))
            throw new IllegalStateException(getClass().getSimpleName() + " must have MOVEMENT_SPEED attribute");

        return builder.build();
    }

    @Override
    public void onAddedToWorld() {
        this.health = this.attributes.getFloat(AttributeKeys.MAX_HEALTH);
    }

    @Override
    public void onRemovedFromWorld(World world, RemovalReason reason) {

    }

    public void onDeath() {
        remove(RemovalReason.KILLED);
    }

    public void heal(float amount) {
        this.health = Math.min(this.health + amount, this.attributes.getFloat(AttributeKeys.MAX_HEALTH));
    }

    public void damage(float amount) {
        this.health = Math.max(this.health - amount, 0);

        boolean died = this.health <= 0;
        onHurt(amount, died);
        if (died) {
            onDeath();
        }
    }

    public float getMaxHealth() {
        return this.attributes.getFloat(AttributeKeys.MAX_HEALTH);
    }

    public void onHurt(float amount, boolean died) {}

    public abstract AttributeMap.Builder createAttributes();

    @Override
    public void writeData(EntityDataWriter<?> writer) {
        writer.writeBoolean("NoAI", this.noAI);
        writer.write("Attributes", this.attributes.codec(), this.attributes);
    }

    @Override
    public void readData(EntityDataReader<?> reader) {
        this.noAI = reader.readBooleanOrDefault("NoAI", false);
        reader.readOptional("Attributes", this.attributes.codec()).ifPresent(this.attributes::copyFrom);
    }
}
