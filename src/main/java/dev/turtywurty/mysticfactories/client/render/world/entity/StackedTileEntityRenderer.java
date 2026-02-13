package dev.turtywurty.mysticfactories.client.render.world.entity;

import dev.turtywurty.mysticfactories.world.entity.EntityType;
import dev.turtywurty.mysticfactories.world.tileentity.StackedTileEntity;
import dev.turtywurty.mysticfactories.world.tileentity.TileEntity;
import org.joml.Matrix4f;
import org.joml.Vector2d;

public class StackedTileEntityRenderer implements EntityRenderer<StackedTileEntity> {
    private final EntityRendererRegistry entityRendererRegistry;

    public StackedTileEntityRenderer(EntityRendererRegistry entityRendererRegistry) {
        this.entityRendererRegistry = entityRendererRegistry;
    }

    @Override
    public void render(EntityRenderContext context, StackedTileEntity entity, Matrix4f modelMatrix) {
        Vector2d basePos = entity.getPosition();
        int layer = 0;
        for (TileEntity entry : entity.getEntries()) {
            double oldX = entry.getPosition().x;
            double oldY = entry.getPosition().y;
            entry.setPosition(basePos.x, basePos.y + layer);

            // noinspection unchecked
            EntityRenderer<TileEntity> renderer = this.entityRendererRegistry.getRendererFor((EntityType<TileEntity>) entry.getType());
            if (renderer != null) {
                renderer.render(context, entry, modelMatrix);
            }

            entry.setPosition(oldX, oldY);
            layer++;
        }
    }
}
