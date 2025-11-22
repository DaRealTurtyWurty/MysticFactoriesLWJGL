package dev.turtywurty.mysticfactories.client.render.world.tile;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.shader.Shader;

public record TileRenderContext(Shader shader, Camera camera, int quadVao, int indexCount) {
}
