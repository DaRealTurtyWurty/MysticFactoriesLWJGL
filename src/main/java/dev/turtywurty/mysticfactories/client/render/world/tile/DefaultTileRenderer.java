package dev.turtywurty.mysticfactories.client.render.world.tile;

import dev.turtywurty.mysticfactories.client.texture.Texture;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.Map;

public class DefaultTileRenderer implements TileRenderer {
    private final Map<String, Texture> textureCache = new HashMap<>();

    @Override
    public void render(TileRenderContext context, TileType tileType, Matrix4f modelMatrix) {
        Texture texture = this.textureCache.computeIfAbsent(tileType.getId().toString(), this::loadTextureForTile);

        context.shader().setUniform("uModel", modelMatrix);
        context.shader().setUniform("uTexture", 0);

        texture.bind(0);
        GL30.glBindVertexArray(context.quadVao());
        GL11.glDrawElements(GL11.GL_TRIANGLES, context.indexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
        texture.unbind();
    }

    private Texture loadTextureForTile(String tileId) {
        String[] parts = tileId.split(":", 2);
        String textureName = parts.length == 2 ? parts[1] : tileId;
        return new Texture("textures/" + textureName + ".png");
    }

    @Override
    public void cleanup() {
        this.textureCache.values().forEach(Texture::cleanup);
        this.textureCache.clear();
    }
}
