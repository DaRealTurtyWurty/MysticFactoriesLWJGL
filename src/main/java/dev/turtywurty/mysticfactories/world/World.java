package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class World implements WorldView {
    @Getter
    protected final Random random = new Random();
    protected final Map<ChunkPos, Chunk> chunks = new HashMap<>();
    protected final List<Entity> entities = new ArrayList<>();
    protected final List<Entity> tickingEntities = new ArrayList<>();
    protected final WorldType worldType;
    private final WorldData worldData;
    private final WorldGenerator generator;

    protected World(WorldType worldType, WorldData worldData) {
        if (worldType == null)
            throw new IllegalArgumentException("WorldType cannot be null");

        if (worldData == null)
            throw new IllegalArgumentException("WorldData cannot be null");

        this.worldType = worldType;
        this.worldData = worldData;

        this.generator = this.worldType.getGenerator().get();
    }

    protected static long randomizeWorldSeed() {
        return ThreadLocalRandom.current().nextLong();
    }

    @Override
    public WorldData getWorldData() {
        return this.worldData;
    }

    public void setTile(TilePos pos, TileType type) {
        ChunkPos chunkPos = ChunkPos.fromTilePos(pos);
        Optional<Chunk> chunkOpt = getChunk(chunkPos);
        Chunk chunk = chunkOpt.orElseThrow(
                () -> new IllegalStateException("Chunk not loaded for position: " + pos + " (chunk " + chunkPos + ")"));
        chunk.setTile(pos, type);
    }

    public Optional<TileType> getTile(TilePos pos) {
        ChunkPos chunkPos = ChunkPos.fromTilePos(pos);
        Optional<Chunk> chunkOpt = getChunk(chunkPos);
        return chunkOpt.flatMap(chunk -> chunk.getTile(pos));
    }

    public Optional<Chunk> getChunk(ChunkPos pos) {
        return Optional.ofNullable(this.chunks.get(pos));
    }

    public Optional<Chunk> getChunk(int chunkX, int chunkZ) {
        return Optional.ofNullable(this.chunks.get(new ChunkPos(chunkX, chunkZ)));
    }

    public void addChunk(ChunkPos pos) {
        this.chunks.computeIfAbsent(pos, chunkPos -> {
            Chunk chunk = new Chunk(chunkPos);
            if (generator != null) {
                generator.generate(this, chunk);
            }

            return chunk;
        });
    }

    @Override
    public Map<ChunkPos, Chunk> getChunks() {
        return Collections.unmodifiableMap(this.chunks);
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(this.entities);
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
        if (entity.getType().shouldTick()) {
            this.tickingEntities.add(entity);
        }

        entity.setWorld(this);
    }

    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
        this.tickingEntities.remove(entity);
    }

    @Override
    public WorldType getWorldType() {
        return this.worldType;
    }

    public abstract void tick(double delta);
}
