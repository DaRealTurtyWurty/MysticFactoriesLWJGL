package dev.turtywurty.mysticfactories.world.biome.feature.shape;

import dev.turtywurty.mysticfactories.util.Direction;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.biome.IntProvider;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomWalkPlacementShape implements PlacementShape {
    private final IntProvider steps;
    private final boolean crossChunks;

    public RandomWalkPlacementShape(IntProvider steps, boolean crossChunks) {
        this.steps = steps;
        this.crossChunks = crossChunks;
    }

    @Override
    public List<TilePos> getPositions(World world, Random random, int chunkX, int chunkY, int attempts) {
        Chunk chunk = world.getChunk(chunkX, chunkY).orElse(null);
        if (chunk == null)
            return Collections.emptyList();

        int stepCount = steps.get(random);
        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = chunkX * ChunkPos.SIZE + random.nextInt(ChunkPos.SIZE);
            int y = chunkY * ChunkPos.SIZE + random.nextInt(ChunkPos.SIZE);
            var startPos = new TilePos(x, y);

            TilePos lastPos = startPos;
            List<TilePos> positions = new ArrayList<>(stepCount);
            positions.add(startPos);

            boolean failed = false;
            for (int i = 0; i < stepCount; i++) {
                Direction direction = Direction.randomDirection(random);
                TilePos offset = lastPos.offset(direction);

                Chunk checkChunk = chunk;

                if (crossChunks) {
                    checkChunk = world.getChunk(offset.toChunkPos()).orElse(null);
                }

                if (checkChunk == null || !checkChunk.contains(offset)) {
                    failed = true;
                    break;
                }

                lastPos = offset;
                positions.add(offset);
            }

            if (failed)
                continue;

            return positions;
        }

        return Collections.emptyList();
    }
}
