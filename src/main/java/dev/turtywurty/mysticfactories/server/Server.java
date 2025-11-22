package dev.turtywurty.mysticfactories.server;

import dev.turtywurty.mysticfactories.world.WorldType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Server {
    protected final Map<WorldType, ServerWorld> worlds = new HashMap<>();

    public void addWorld(ServerWorld world) {
        this.worlds.put(world.getWorldType(), world);
    }

    public Optional<ServerWorld> getWorld(WorldType type) {
        return Optional.ofNullable(this.worlds.get(type));
    }

    public Collection<ServerWorld> getWorlds() {
        return this.worlds.values();
    }

    public void tick(double delta) {
        this.worlds.values().forEach(world -> world.tick(delta));
    }
}
