package dev.turtywurty.mysticfactories.client.render.world.tile;

import dev.turtywurty.mysticfactories.world.tile.TileType;
import org.joml.Matrix4f;

public interface TileRenderer {
    void render(TileRenderContext context, TileType tileType, Matrix4f modelMatrix);

    default void cleanup() {
        // optional override
    }
}
