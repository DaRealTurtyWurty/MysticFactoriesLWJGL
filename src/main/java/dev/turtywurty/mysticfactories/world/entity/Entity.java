package dev.turtywurty.mysticfactories.world.entity;

import dev.turtywurty.mysticfactories.util.AABB;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.data.EntityDataReader;
import dev.turtywurty.mysticfactories.world.entity.data.EntityDataWriter;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.UUID;

@Getter
public abstract class Entity {
    private final EntityType<?> type;
    private final Vector2d position = new Vector2d(0, 0);
    private final Vector2d velocity = new Vector2d(0, 0);
    private UUID uuid = UUID.randomUUID();
    private World world;
    private float rotation = 0f;
    private boolean onGround = true;
    private boolean removed = false;
    @Setter
    private boolean silent = false;

    public Entity(@NotNull EntityType<?> type) {
        this.type = type;
    }

    public Entity(@NotNull EntityType<?> type, @Nullable World world) {
        this(type);
        this.world = world;
    }

    public void setWorld(@Nullable World world) {
        if (world == null) {
            remove(RemovalReason.CHANGED_DIMENSION);
            return;
        }

        this.world = world;
        onAddedToWorld();
    }

    public void setRotation(float rotation) {
        this.rotation = rotation % 360f;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void addVelocity(double x, double y) {
        this.velocity.add(x, y);
    }

    public void baseTick(double delta) {
        this.position.add(this.velocity.x * delta, this.velocity.y * delta);
        tick(delta);
    }

    public void remove(RemovalReason reason) {
        if (this.world != null) {
            this.world.removeEntity(this);
            World oldWorld = this.world;
            this.world = null;
            this.removed = true;
            onRemovedFromWorld(oldWorld, reason);
        }
    }

    public boolean hasWorld() {
        return this.world != null;
    }

    public abstract AABB getAABB();

    public abstract void tick(double delta);
    public abstract void onAddedToWorld();
    public abstract void onRemovedFromWorld(World world, RemovalReason reason);

    public abstract void writeData(EntityDataWriter<?> writer);
    public abstract void readData(EntityDataReader<?> reader);
}
