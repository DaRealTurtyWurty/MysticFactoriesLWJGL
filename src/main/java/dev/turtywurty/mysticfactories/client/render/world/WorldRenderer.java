package dev.turtywurty.mysticfactories.client.render.world;

import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderContext;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRenderer;
import dev.turtywurty.mysticfactories.client.render.world.entity.EntityRendererRegistry;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileAtlas;
import dev.turtywurty.mysticfactories.client.render.world.tile.TileRenderContext;
import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileRegistry;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldRenderer {
    private final EntityRendererRegistry entityRendererRegistry;
    private final Matrix4f modelMatrix = new Matrix4f();
    private final Map<ChunkPos, ChunkRenderData> chunkMeshes = new HashMap<>();
    private final TileAtlas tileAtlas;

    public WorldRenderer(EntityRendererRegistry entityRendererRegistry, TileRegistry tileRegistry) {
        this.entityRendererRegistry = entityRendererRegistry;
        this.tileAtlas = TileAtlas.build(tileRegistry);
    }

    public void render(ClientWorld world, TileRenderContext tileRenderContext, EntityRenderContext entityRenderContext) {
        float tileSize = world.getTileSize();
        cullMissingChunks(world);

        for (Map.Entry<ChunkPos, Chunk> chunkEntry : world.getChunks().entrySet()) {
            ChunkPos chunkPos = chunkEntry.getKey();
            if (!isChunkVisible(tileRenderContext, chunkPos, tileSize))
                continue;

            ChunkRenderData renderData = this.chunkMeshes.compute(chunkPos, (pos, existing) -> {
                int modificationCount = chunkEntry.getValue().getModificationCount();
                if (existing == null || existing.modificationCount != modificationCount) {
                    if (existing != null) {
                        existing.cleanup();
                    }

                    return buildChunkRenderData(chunkEntry.getValue(), tileSize, modificationCount);
                }

                return existing;
            });

            renderData.render(tileRenderContext, this.modelMatrix, this.tileAtlas);
        }

        renderVisibleEntities(world, entityRenderContext, tileSize);
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> void renderEntity(EntityRenderContext context, T entity) {
        EntityRenderer<T> renderer = (EntityRenderer<T>) this.entityRendererRegistry.getRendererFor(entity.getType());
        if (renderer == null)
            return;

        renderer.render(context, entity, this.modelMatrix);
    }

    private void renderVisibleEntities(ClientWorld world, EntityRenderContext context, float tileSize) {
        var camera = context.camera();
        float camX = camera.getPosition().x;
        float camY = camera.getPosition().y;
        float viewLeft = camX + camera.getOrthoLeft() - tileSize;
        float viewRight = camX + camera.getOrthoRight() + tileSize;
        float viewBottom = camY + camera.getOrthoBottom() - tileSize;
        float viewTop = camY + camera.getOrthoTop() + tileSize;

        for (Entity entity : world.getEntities()) {
            var pos = entity.getPosition();
            float x = (float) pos.x;
            float y = (float) pos.y;
            if (x < viewLeft || x > viewRight || y < viewBottom || y > viewTop)
                continue;

            renderEntity(context, entity);
        }
    }

    public void cleanup() {
        this.chunkMeshes.values().forEach(ChunkRenderData::cleanup);
        this.chunkMeshes.clear();
        this.tileAtlas.cleanup();
    }

    private boolean isChunkVisible(TileRenderContext context, ChunkPos chunkPos, float tileSize) {
        var camera = context.camera();
        float camX = camera.getPosition().x;
        float camY = camera.getPosition().y;
        float viewLeft = camX + camera.getOrthoLeft();
        float viewRight = camX + camera.getOrthoRight();
        float viewBottom = camY + camera.getOrthoBottom();
        float viewTop = camY + camera.getOrthoTop();

        float halfTile = tileSize * 0.5f;
        float chunkMinX = chunkPos.x * ChunkPos.SIZE * tileSize - halfTile;
        float chunkMinY = chunkPos.y * ChunkPos.SIZE * tileSize - halfTile;
        float chunkMaxX = chunkMinX + ChunkPos.SIZE * tileSize;
        float chunkMaxY = chunkMinY + ChunkPos.SIZE * tileSize;

        boolean xOverlap = chunkMaxX >= viewLeft && chunkMinX <= viewRight;
        boolean yOverlap = chunkMaxY >= viewBottom && chunkMinY <= viewTop;
        return xOverlap && yOverlap;
    }

    private void cullMissingChunks(ClientWorld world) {
        this.chunkMeshes.entrySet().removeIf(entry -> {
            boolean missing = !world.getChunks().containsKey(entry.getKey());
            if (missing) {
                entry.getValue().cleanup();
            }

            return missing;
        });
    }

    private ChunkRenderData buildChunkRenderData(Chunk chunk, float tileSize, int modificationCount) {
        Map<TileType, List<TilePos>> tilesByType = new HashMap<>();
        for (Map.Entry<TilePos, TileType> entry : chunk.getTiles().entrySet()) {
            tilesByType.computeIfAbsent(entry.getValue(), ignored -> new ArrayList<>()).add(entry.getKey());
        }

        Map<TileType, TileBatchMesh> meshes = new HashMap<>();
        for (Map.Entry<TileType, List<TilePos>> entry : tilesByType.entrySet()) {
            TileBatchMesh mesh = createMeshForTileType(entry.getKey(), entry.getValue(), tileSize);
            if (mesh != null) {
                meshes.put(entry.getKey(), mesh);
            }
        }

        return new ChunkRenderData(meshes, modificationCount);
    }

    private TileBatchMesh createMeshForTileType(TileType tileType, List<TilePos> positions, float tileSize) {
        if (positions.isEmpty())
            return null;

        TileAtlas.UV uv = this.tileAtlas.getUv(tileType);
        if (uv == null)
            return null;

        int tileCount = positions.size();
        float[] vertices = new float[tileCount * 4 * 4]; // 4 vertices, each 4 floats (pos + uv)
        int[] indices = new int[tileCount * 6]; // 2 triangles per tile

        float halfTile = tileSize * 0.5f;
        for (int i = 0; i < tileCount; i++) {
            TilePos pos = positions.get(i);
            float centerX = pos.x() * tileSize;
            float centerY = pos.y() * tileSize;

            float left = centerX - halfTile;
            float right = centerX + halfTile;
            float bottom = centerY - halfTile;
            float top = centerY + halfTile;

            int vertexOffset = i * 16;
            // position      // uv
            vertices[vertexOffset] = left;
            vertices[vertexOffset + 1] = bottom;
            vertices[vertexOffset + 2] = uv.u0();
            vertices[vertexOffset + 3] = uv.v0();

            vertices[vertexOffset + 4] = right;
            vertices[vertexOffset + 5] = bottom;
            vertices[vertexOffset + 6] = uv.u1();
            vertices[vertexOffset + 7] = uv.v0();

            vertices[vertexOffset + 8] = right;
            vertices[vertexOffset + 9] = top;
            vertices[vertexOffset + 10] = uv.u1();
            vertices[vertexOffset + 11] = uv.v1();

            vertices[vertexOffset + 12] = left;
            vertices[vertexOffset + 13] = top;
            vertices[vertexOffset + 14] = uv.u0();
            vertices[vertexOffset + 15] = uv.v1();

            int baseIndex = i * 6;
            int baseVertex = i * 4;
            indices[baseIndex] = baseVertex;
            indices[baseIndex + 1] = baseVertex + 1;
            indices[baseIndex + 2] = baseVertex + 2;
            indices[baseIndex + 3] = baseVertex + 2;
            indices[baseIndex + 4] = baseVertex + 3;
            indices[baseIndex + 5] = baseVertex;
        }

        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        int ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        int stride = 4 * Float.BYTES;
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 2L * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);

        return new TileBatchMesh(vao, vbo, ebo, indices.length);
    }

    private record ChunkRenderData(Map<TileType, TileBatchMesh> meshes, int modificationCount) {
        void render(TileRenderContext context, Matrix4f modelMatrix, TileAtlas atlas) {
            context.shader().setUniform("uModel", modelMatrix.identity());
            atlas.bind(0);
            for (TileBatchMesh mesh : this.meshes.values()) {
                mesh.render(context);
            }

            atlas.unbind();
        }

        void cleanup() {
            this.meshes.values().forEach(TileBatchMesh::cleanup);
            this.meshes.clear();
        }
    }

    private record TileBatchMesh(int vao, int vbo, int ebo, int indexCount) {
        void render(TileRenderContext context) {
            context.shader().setUniform("uUseTexture", true);
            context.shader().setUniform("uColor", new Vector4f(1f, 1f, 1f, 1f));
            context.shader().setUniform("uUVMin", new Vector2f(0f, 0f));
            context.shader().setUniform("uUVMax", new Vector2f(1f, 1f));
            context.shader().setUniform("uTexture", 0);
            GL30.glBindVertexArray(this.vao);
            GL11.glDrawElements(GL11.GL_TRIANGLES, this.indexCount, GL11.GL_UNSIGNED_INT, 0);
            GL30.glBindVertexArray(0);
        }

        void cleanup() {
            GL15.glDeleteBuffers(this.vbo);
            GL15.glDeleteBuffers(this.ebo);
            GL30.glDeleteVertexArrays(this.vao);
        }
    }
}
