package dev.turtywurty.mysticfactories.client.render.world.entity;

import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import org.joml.Matrix4f;

public interface EntityRenderer<T extends Entity> {
    void render(EntityRenderContext context, T entity, Matrix4f modelMatrix);

    default void cleanup() {
        // optional override
    }
}
