package dev.turtywurty.mysticfactories.world.biome.spawning;

public interface SpawnCondition {
    boolean canSpawn(SpawnContext context);
}