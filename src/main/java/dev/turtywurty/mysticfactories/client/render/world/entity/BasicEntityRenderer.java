package dev.turtywurty.mysticfactories.client.render.world.entity;

import dev.turtywurty.mysticfactories.client.render.mesh.QuadMesh;
import dev.turtywurty.mysticfactories.client.texture.Texture;
import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Simple textured quad renderer that can be reused/extended for 2D entities.
 */
public class BasicEntityRenderer<T extends Entity> implements EntityRenderer<T> {
    private final Texture texture;
    private final QuadMesh quadMesh = new QuadMesh();
    private final float tileSize;

    public BasicEntityRenderer(Identifier textureId, float tileSize) {
        this.texture = new Texture("textures/" + textureId.path() + ".png");
        this.tileSize = tileSize;
    }

    @Override
    public void render(EntityRenderContext context, T entity, Matrix4f modelMatrix) {
        var pos = entity.getPosition();
        modelMatrix.identity()
                .translation((float) pos.x, (float) pos.y, 0.0f)
                .scale(this.tileSize, this.tileSize, 1.0f);

        context.shader().setUniform("uModel", modelMatrix);
        context.shader().setUniform("uTexture", 0);

        this.texture.bind(0);
        GL30.glBindVertexArray(this.quadMesh.getVao());
        GL11.glDrawElements(GL11.GL_TRIANGLES, this.quadMesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
        this.texture.unbind();
    }

    @Override
    public void cleanup() {
        this.texture.cleanup();
        this.quadMesh.cleanup();
    }
}
