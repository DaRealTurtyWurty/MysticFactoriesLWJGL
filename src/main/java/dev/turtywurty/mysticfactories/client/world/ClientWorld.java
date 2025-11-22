package dev.turtywurty.mysticfactories.client.world;

import dev.turtywurty.mysticfactories.client.render.world.WorldRendererBase;
import dev.turtywurty.mysticfactories.world.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
public class ClientWorld extends World {
    @Getter
    private WorldRendererBase renderer;

    public ClientWorld(WorldType worldType, long seed) {
        super(worldType, new WorldData(seed));
    }

    public void applyFullState(Map<ChunkPos, Chunk> chunks) {
        this.chunks.clear();
        this.chunks.putAll(chunks);
    }

    public float getTileSize() {
        return 16f;
    }

    @Override
    public void tick(double delta) {
        // Client-side effects or interpolation could go here later.
    }
}
