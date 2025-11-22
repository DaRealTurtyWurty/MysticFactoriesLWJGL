package dev.turtywurty.mysticfactories.client.render.world;

import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderer;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRendererRegistry;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderer;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRendererRegistry;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import org.joml.Matrix4f;

import java.util.Map;

public class WorldRenderer implements WorldRendererBase {
    private final TileRendererRegistry tileRendererRegistry;
    private final EntityRendererRegistry entityRendererRegistry;
    private final Matrix4f modelMatrix = new Matrix4f();

    public WorldRenderer(TileRendererRegistry tileRendererRegistry, EntityRendererRegistry entityRendererRegistry) {
        this.tileRendererRegistry = tileRendererRegistry;
        this.entityRendererRegistry = entityRendererRegistry;
    }

    @Override
    public void render(ClientWorld world, TileRenderContext tileRenderContext, EntityRenderContext entityRenderContext) {
        float tileSize = world.getTileSize();
        for (Map.Entry<ChunkPos, Chunk> chunkEntry : world.getChunks().entrySet()) {
            for (Map.Entry<TilePos, TileType> entry : chunkEntry.getValue().getTiles().entrySet()) {
                TilePos pos = entry.getKey();
                TileType tileType = entry.getValue();

                Matrix4f model = this.modelMatrix.identity()
                        .translation(pos.x() * tileSize, pos.y() * tileSize, 0.0f)
                        .scale(tileSize);

                renderTile(tileRenderContext, tileType, model);
            }
        }

        for (Entity entity : world.getEntities()) {
            renderEntity(entityRenderContext, entity);
        }
    }

    private void renderTile(TileRenderContext tileRenderContext, TileType tileType, Matrix4f model) {
        TileRenderer renderer = this.tileRendererRegistry.getRendererFor(tileType);
        renderer.render(tileRenderContext, tileType, model);
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> void renderEntity(EntityRenderContext context, T entity) {
        EntityRenderer<T> renderer = (EntityRenderer<T>) this.entityRendererRegistry.getRendererFor(entity.getType());
        renderer.render(context, entity, this.modelMatrix);
    }

    public void cleanup() {
        // TODO: Cleanup resources if needed
    }
}
