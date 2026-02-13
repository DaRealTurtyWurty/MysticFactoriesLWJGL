package dev.turtywurty.mysticfactories.world.feature.shape;

import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.WorldView;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PoissonDiskPlacementShape implements PlacementShape {
    private final double minDistance;
    private final int maxCandidates;

    public PoissonDiskPlacementShape(double minDistance, int maxCandidates) {
        this.minDistance = minDistance;
        this.maxCandidates = maxCandidates;
    }

    @Override
    public List<TilePos> getPositions(WorldView world, Random random, int chunkX, int chunkY, int attempts) {
        if (attempts <= 0)
            return Collections.emptyList();

        Chunk chunk = world.getChunk(chunkX, chunkY).orElse(null);
        if (chunk == null)
            return Collections.emptyList();

        int chunkStartX = chunkX * ChunkPos.SIZE;
        int chunkStartY = chunkY * ChunkPos.SIZE;
        int chunkEndXExclusive = chunkStartX + ChunkPos.SIZE;
        int chunkEndYExclusive = chunkStartY + ChunkPos.SIZE;

        List<TilePos> samples = new ArrayList<>();
        List<TilePos> active = new ArrayList<>();

        // Seed: pick a random starting position in this chunk
        int sx = chunkStartX + random.nextInt(ChunkPos.SIZE);
        int sy = chunkStartY + random.nextInt(ChunkPos.SIZE);
        TilePos first = new TilePos(sx, sy);

        if (!chunk.contains(first))
            return Collections.emptyList();

        samples.add(first);
        active.add(first);

        double minDistSq = minDistance * minDistance;
        while (!active.isEmpty() && samples.size() < attempts) {
            // Pick a random active point
            int activeIndex = random.nextInt(active.size());
            TilePos base = active.get(activeIndex);

            boolean foundNewSample = false;

            for (int i = 0; i < maxCandidates; i++) {
                // Random angle [0, 2π)
                double angle = random.nextDouble() * 2.0 * Math.PI;

                // Random radius in [minDistance, 2 * minDistance)
                double radius = minDistance * (1.0 + random.nextDouble());

                // Compute candidate point
                int cx = base.x() + (int) Math.round(Math.cos(angle) * radius);
                int cy = base.y() + (int) Math.round(Math.sin(angle) * radius);

                var candidate = new TilePos(cx, cy);
                if (!chunk.contains(candidate))
                    continue;

                // Check distance to all existing samples
                if (!isFarEnough(candidate, samples, minDistSq))
                    continue;

                samples.add(candidate);
                active.add(candidate);
                foundNewSample = true;
                break;
            }

            // If we failed to spawn from this active point, remove it
            if (!foundNewSample) {
                active.remove(activeIndex);
            }
        }

        return samples;
    }

    private boolean isFarEnough(TilePos candidate, List<TilePos> samples, double minDistSq) {
        for (TilePos sample : samples) {
            double dx = candidate.x() - sample.x();
            double dy = candidate.y() - sample.y();
            double distSq = dx * dx + dy * dy;
            if (distSq < minDistSq)
                return false;
        }

        return true;
    }
}
