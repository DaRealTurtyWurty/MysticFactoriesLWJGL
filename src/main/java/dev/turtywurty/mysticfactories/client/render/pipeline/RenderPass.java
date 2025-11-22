package dev.turtywurty.mysticfactories.client.render.pipeline;

public interface RenderPass {
    void render(RenderContext context);

    default void cleanup() {
        // optional
    }
}
