package dev.turtywurty.mysticfactories.world;

import dev.turtywurty.mysticfactories.init.TileEntityTypes;
import dev.turtywurty.mysticfactories.world.biome.Biome;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.entity.RemovalReason;
import dev.turtywurty.mysticfactories.world.gen.WorldGenerator;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.world.tileentity.StackedTileEntity;
import dev.turtywurty.mysticfactories.world.tileentity.TileEntity;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class World implements WorldView {
    @Getter
    protected final Random random = new Random();
    protected final Map<ChunkPos, Chunk> chunks = new HashMap<>();
    protected final List<Entity> entities = new ArrayList<>();
    protected final List<Entity> tickingEntities = new ArrayList<>();
    protected final Map<TilePos, TileEntity> tileEntitiesMap = new HashMap<>();
    protected final Map<Entity, RemovalReason> pendingEntityRemovals = new LinkedHashMap<>();
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

    public void setTileEntity(TilePos pos, TileEntity tileEntity) {
        TileEntity existing = this.tileEntitiesMap.remove(pos);
        if (existing != null) {
            untrackEntity(existing);
        }

        if (tileEntity == null)
            return;

        this.tileEntitiesMap.put(pos, tileEntity);
        trackEntity(tileEntity);
        tileEntity.setWorld(this);
        tileEntity.setPosition(pos.x, pos.y);
    }

    public void pushTileEntity(TilePos pos, TileEntity tileEntity) {
        if (tileEntity == null)
            throw new IllegalArgumentException("tileEntity cannot be null");

        TileEntity existing = this.tileEntitiesMap.get(pos);
        if (existing == null) {
            this.tileEntitiesMap.put(pos, tileEntity);
            trackEntity(tileEntity);
            tileEntity.setWorld(this);
            tileEntity.setPosition(pos.x, pos.y);
            return;
        }

        StackedTileEntity stacked;
        if (existing instanceof StackedTileEntity existingStacked) {
            stacked = existingStacked;
        } else {
            stacked = TileEntityTypes.STACKED.create(this);
            stacked.setPosition(pos.x, pos.y);
            stacked.push(existing);

            untrackEntity(existing);
            this.tileEntitiesMap.put(pos, stacked);
            trackEntity(stacked);
        }

        stacked.push(tileEntity);
    }

    public Optional<TileEntity> popTileEntity(TilePos pos) {
        TileEntity existing = this.tileEntitiesMap.get(pos);
        if (existing == null)
            return Optional.empty();

        if (!(existing instanceof StackedTileEntity stacked)) {
            this.tileEntitiesMap.remove(pos);
            untrackEntity(existing);
            return Optional.of(existing);
        }

        Optional<TileEntity> popped = stacked.pop();
        if (popped.isEmpty())
            return Optional.empty();

        if (stacked.isEmpty()) {
            this.tileEntitiesMap.remove(pos);
            untrackEntity(stacked);
        } else if (stacked.size() == 1) {
            TileEntity remaining = stacked.pop().orElseThrow();
            untrackEntity(stacked);
            this.tileEntitiesMap.put(pos, remaining);
            trackEntity(remaining);
            remaining.setWorld(this);
            remaining.setPosition(pos.x, pos.y);
        }

        return popped;
    }

    public List<TileEntity> getTileEntityStack(TilePos pos) {
        TileEntity tileEntity = this.tileEntitiesMap.get(pos);
        if (tileEntity == null)
            return List.of();

        if (tileEntity instanceof StackedTileEntity stacked)
            return stacked.getEntries();

        return List.of(tileEntity);
    }

    public Optional<TileType> getTile(TilePos pos) {
        ChunkPos chunkPos = ChunkPos.fromTilePos(pos);
        Optional<Chunk> chunkOpt = getChunk(chunkPos);
        return chunkOpt.flatMap(chunk -> chunk.getTile(pos));
    }

    public Optional<TileEntity> getTileEntity(TilePos pos) {
        TileEntity tileEntity = this.tileEntitiesMap.get(pos);
        if (tileEntity == null)
            return Optional.empty();

        if (tileEntity instanceof StackedTileEntity stacked)
            return stacked.peek();

        return Optional.of(tileEntity);
    }

    public Optional<Biome> getBiome(TilePos pos) {
        ChunkPos chunkPos = ChunkPos.fromTilePos(pos);
        Optional<Chunk> chunkOpt = getChunk(chunkPos);
        Optional<Biome> storedBiome = chunkOpt.flatMap(chunk -> chunk.getBiome(pos));
        if (storedBiome.isPresent())
            return storedBiome;

        if (this.generator == null)
            return Optional.empty();

        return Optional.ofNullable(this.generator.getBiome(pos.x, pos.y));
    }

    public Optional<Chunk> getChunk(ChunkPos pos) {
        return Optional.ofNullable(this.chunks.get(pos));
    }

    public Optional<Chunk> getChunk(int chunkX, int chunkZ) {
        return Optional.ofNullable(this.chunks.get(new ChunkPos(chunkX, chunkZ)));
    }

    public void addChunk(ChunkPos pos) {
        if (this.chunks.containsKey(pos))
            return;

        var chunk = new Chunk(pos);
        // Insert before generation so placement rules can query the in-progress chunk through WorldView.
        this.chunks.put(pos, chunk);

        if (generator != null) {
            generator.generate(this, chunk);
        }
    }

    @Override
    public Map<ChunkPos, Chunk> getChunks() {
        return Collections.unmodifiableMap(this.chunks);
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(this.entities);
    }

    public WorldSnapshot createSnapshot() {
        return new WorldSnapshot(this.chunks, this.entities);
    }

    public void addEntity(Entity entity) {
        trackEntity(entity);
        entity.setWorld(this);
    }

    public void removeEntity(Entity entity) {
        markEntityForRemoval(entity, RemovalReason.DISCARDED);
    }

    public void markEntityForRemoval(Entity entity, RemovalReason reason) {
        if (entity == null)
            return;

        if (entity.getWorld() != this)
            return;

        this.pendingEntityRemovals.putIfAbsent(entity, reason == null ? RemovalReason.DISCARDED : reason);
    }

    @Override
    public WorldType getWorldType() {
        return this.worldType;
    }

    private void trackEntity(Entity entity) {
        if (!this.entities.contains(entity)) {
            this.entities.add(entity);
        }

        if (entity.getType().isShouldTick() && !this.tickingEntities.contains(entity)) {
            this.tickingEntities.add(entity);
        }
    }

    private void untrackEntity(Entity entity) {
        this.entities.remove(entity);
        this.tickingEntities.remove(entity);
    }

    protected void processPendingEntityRemovals() {
        if (this.pendingEntityRemovals.isEmpty())
            return;

        var removals = new LinkedHashMap<>(this.pendingEntityRemovals);
        this.pendingEntityRemovals.clear();
        for (var entry : removals.entrySet()) {
            Entity entity = entry.getKey();
            RemovalReason reason = entry.getValue();

            untrackEntity(entity);

            if (entity instanceof TileEntity tileEntity) {
                this.tileEntitiesMap.values().removeIf(existing -> existing == tileEntity);
            }

            entity.finalizeRemoval(this, reason);
        }
    }

    public abstract void tick(double delta);
}
