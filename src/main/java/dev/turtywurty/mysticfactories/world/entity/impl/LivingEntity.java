package dev.turtywurty.mysticfactories.world.entity.impl;

import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.DamageSource;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.entity.RemovalReason;
import dev.turtywurty.mysticfactories.world.entity.attribs.AttributeKey;
import dev.turtywurty.mysticfactories.world.entity.attribs.AttributeKeys;
import dev.turtywurty.mysticfactories.world.entity.attribs.AttributeMap;
import dev.turtywurty.mysticfactories.world.entity.data.EntityDataReader;
import dev.turtywurty.mysticfactories.world.entity.data.EntityDataWriter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class LivingEntity extends Entity {
    @Getter
    protected final AttributeMap attributes;
    protected boolean noAI = false;
    @Getter
    protected float health;

    private final Map<DamageSource, Long> damageSourceCooldowns = new HashMap<>();

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
    public final void baseTick(double delta) {
        super.baseTick(delta);
    }

    @Override
    public void onAddedToWorld() {
        this.health = this.attributes.getFloat(AttributeKeys.MAX_HEALTH);
    }

    @Override
    public void onRemovedFromWorld(World world, RemovalReason reason) {
    }

    public void onDeath(DamageSource source) {
        remove(RemovalReason.KILLED);
    }

    public void heal(float amount) {
        this.health = Math.min(this.health + amount, this.attributes.getFloat(AttributeKeys.MAX_HEALTH));
    }

    public void damage(@NotNull DamageSource source, float amount) {
        if (source == null || amount <= 0)
            return;

        long currentTime = System.currentTimeMillis();
        if (damageSourceCooldowns.containsKey(source) && currentTime < damageSourceCooldowns.get(source))
            return;

        damageSourceCooldowns.put(source, currentTime + source.getCooldownMs());
        this.health = Math.max(this.health - amount, 0);

        boolean died = this.health <= 0;
        onHurt(source, amount, died);
        if (died) {
            onDeath(source);
        }
    }

    public float getMaxHealth() {
        return this.attributes.getFloat(AttributeKeys.MAX_HEALTH);
    }

    public double getMovementSpeed() {
        return this.attributes.getDouble(AttributeKeys.MOVEMENT_SPEED);
    }

    public <T> T getAttributeValue(AttributeKey<T> key) {
        return this.attributes.get(key).getBaseValue();
    }

    public void onHurt(DamageSource source, float amount, boolean died) {
    }

    public abstract AttributeMap.Builder createAttributes();

    @Override
    public void writeData(EntityDataWriter<?> writer) {
        super.writeData(writer);
        writer.writeBoolean("NoAI", this.noAI);
        writer.write("Attributes", this.attributes.codec(), this.attributes);
        writer.writeFloat("Health", this.health);
    }

    @Override
    public void readData(EntityDataReader<?> reader) {
        super.readData(reader);
        this.noAI = reader.readBooleanOrDefault("NoAI", false);
        reader.readOptional("Attributes", this.attributes.codec()).ifPresent(this.attributes::copyFrom);
        this.health = reader.readFloatOrDefault("Health", this.attributes.getFloat(AttributeKeys.MAX_HEALTH));
    }
}
