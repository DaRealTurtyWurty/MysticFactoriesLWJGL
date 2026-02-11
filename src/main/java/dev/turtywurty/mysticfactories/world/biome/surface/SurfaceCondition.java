package dev.turtywurty.mysticfactories.world.biome.surface;

@FunctionalInterface
public interface SurfaceCondition {
    boolean test(SurfaceContext context);
}
