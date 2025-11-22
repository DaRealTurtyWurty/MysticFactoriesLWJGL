package dev.turtywurty.mysticfactories.client.render.pipeline;

import java.util.ArrayList;
import java.util.List;

public class RenderPipeline {
    private final List<RenderPass> passes = new ArrayList<>();

    public RenderPipeline addPass(RenderPass pass) {
        this.passes.add(pass);
        return this;
    }

    public void render(RenderContext context) {
        for (RenderPass pass : this.passes) {
            pass.render(context);
        }
    }

    public void cleanup() {
        this.passes.forEach(RenderPass::cleanup);
        this.passes.clear();
    }
}
