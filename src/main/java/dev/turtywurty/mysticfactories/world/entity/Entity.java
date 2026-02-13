package dev.turtywurty.mysticfactories.world.entity;

import dev.turtywurty.mysticfactories.util.AABB;
import dev.turtywurty.mysticfactories.util.Codecs;
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
    @Setter
    private UUID uuid = UUID.randomUUID();
    private World world;
    private float rotation = 0f;
    @Setter
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

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void addVelocity(double x, double y) {
        this.velocity.add(x, y);
    }

    public void baseTick(double delta) {
        this.position.add(this.velocity.x * delta, this.velocity.y * delta);
        tick(delta);
    }

    public void remove(RemovalReason reason) {
        if (this.removed)
            return;

        this.removed = true;
        if (this.world != null) {
            this.world.markEntityForRemoval(this, reason);
        }
    }

    public void finalizeRemoval(World oldWorld, RemovalReason reason) {
        if (oldWorld == null)
            return;

        if (this.world != oldWorld)
            return;

        this.world = null;
        onRemovedFromWorld(oldWorld, reason);
    }

    public boolean hasWorld() {
        return this.world != null;
    }

    public abstract AABB getAABB();

    public abstract void tick(double delta);

    public void onAddedToWorld() {
    }

    public void onRemovedFromWorld(World world, RemovalReason reason) {
    }

    public void onEntityCollision(Entity other) {
    }

    public boolean shouldCollideWithTiles() {
        return true;
    }

    public void writeData(EntityDataWriter<?> writer) {
        writer.writeUuid("UUID", this.uuid);
        writer.write("Position", Codecs.VECTOR2D, this.position);
        writer.write("Velocity", Codecs.VECTOR2D, this.velocity);
        writer.writeFloat("Rotation", this.rotation);
        writer.writeBoolean("OnGround", this.onGround);
        writer.writeBoolean("Silent", this.silent);
    }

    public void readData(EntityDataReader<?> reader) {
        this.uuid = reader.readUuid("UUID");
        reader.readAndConsume("Position", Codecs.VECTOR2D, this.position::set);
        reader.readAndConsume("Velocity", Codecs.VECTOR2D, this.velocity::set);
        this.rotation = reader.readFloat("Rotation");
        this.onGround = reader.readBoolean("OnGround");
        this.silent = reader.readBoolean("Silent");
    }
}
