package dev.turtywurty.mysticfactories.init;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.WorldType;
import dev.turtywurty.mysticfactories.world.WorldTypeRegistry;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldTypes {
    public static WorldType OVERWORLD;
    private static final Map<Long, FastNoise> OVERWORLD_NOISE_CACHE = new ConcurrentHashMap<>();

    private WorldTypes() {
    }

    public static void register(WorldTypeRegistry registry) {
        WorldGenerator overworldGenerator = (world, chunk) -> {
            long seed = world.getWorldData().getSeed();
            FastNoise overworldNoise = OVERWORLD_NOISE_CACHE.computeIfAbsent(seed, WorldTypes::createOverworldNoise);

            for (int x = 0; x < Chunk.SIZE; x++) {
                for (int z = 0; z < Chunk.SIZE; z++) {
                    var pos = new TilePos(
                            chunk.getPos().x * Chunk.SIZE + x,
                            chunk.getPos().y * Chunk.SIZE + z
                    );

                    TileType grass = TileTypes.GRASS;
                    TileType water = TileTypes.WATER;
                    TileType sand = TileTypes.SAND;
                    float noiseValue = overworldNoise.getNoise(pos.x, pos.y);
                    if (noiseValue < -0.2f) {
                        chunk.setTile(pos, water);
                    } else if (noiseValue < 0.0f) {
                        chunk.setTile(pos, sand);
                    } else {
                        chunk.setTile(pos, grass);
                    }
                }
            }
        };

        OVERWORLD = registry.register(new WorldType(Identifier.of("overworld"), overworldGenerator));
    }

    private static FastNoise createOverworldNoise(long seed) {
        int intSeed = Long.hashCode(seed); // fold long seed into int
        return FastNoise.builder()
                .seed(intSeed)
                .type(NoiseType.SIMPLEX2)
                .fractal(FractalType.FBM)
                .frequency(0.01f)
                .octaves(4)
                .build();
    }
}
