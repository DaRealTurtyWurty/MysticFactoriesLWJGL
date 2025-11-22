package dev.turtywurty.mysticfactories.client.render.world.entity;

import dev.turtywurty.mysticfactories.client.camera.Camera;
import dev.turtywurty.mysticfactories.client.shader.Shader;

public record EntityRenderContext(Shader shader, Camera camera) {
}
