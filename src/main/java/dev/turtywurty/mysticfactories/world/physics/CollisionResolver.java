package dev.turtywurty.mysticfactories.world.physics;

import dev.turtywurty.mysticfactories.util.AABB;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.entity.Entity;
import dev.turtywurty.mysticfactories.world.tile.TilePos;
import dev.turtywurty.mysticfactories.world.tile.TileType;
import dev.turtywurty.mysticfactories.world.tileentity.StackedTileEntity;

import java.util.List;

public final class CollisionResolver {
    private static final double EPSILON = 1e-10;
    private static final double PUSH_OUT_SLOP = 1e-4;
    private static final int MAX_PUSH_OUT_ITERATIONS = 6;

    private CollisionResolver() {
    }

    public static void moveEntity(World world, Entity entity, double deltaTime) {
        if (isImmovable(entity)) {
            entity.getVelocity().set(0, 0);
            return;
        }

        double deltaX = entity.getVelocity().x() * deltaTime;
        double deltaY = entity.getVelocity().y() * deltaTime;
        double requestedDeltaX = deltaX;
        double requestedDeltaY = deltaY;
        entity.setOnGround(false);
        boolean collidesWithTiles = entity.shouldCollideWithTiles();

        AABB box = entity.getAABB();
        if (collidesWithTiles) {
            deltaX = clipAgainstSolidTiles(world, box, deltaX, 0);
        }
        deltaX = clipAgainstEntities(world, entity, box, deltaX, 0);
        box = box.offset(deltaX, 0);

        if (collidesWithTiles) {
            deltaY = clipAgainstSolidTiles(world, box, 0, deltaY);
        }
        deltaY = clipAgainstEntities(world, entity, box, 0, deltaY);
        box = box.offset(0, deltaY);

        entity.setPosition(box.minX(), box.minY());
        if (deltaX != requestedDeltaX) {
            entity.getVelocity().set(0, entity.getVelocity().y());
        }

        if (deltaY != requestedDeltaY) {
            entity.getVelocity().set(entity.getVelocity().x(), 0);
        }

        resolveOverlaps(world, entity);

        if (collidesWithTiles && requestedDeltaY < 0 && deltaY != requestedDeltaY) {
            entity.setOnGround(true);
        }
    }

    private static double clipAgainstSolidTiles(World world, AABB box, double deltaX, double deltaY) {
        if (deltaX == 0 && deltaY == 0)
            return 0;

        double clippedDelta = deltaX != 0 ? deltaX : deltaY;
        AABB sweptBox = box.union(box.offset(deltaX, deltaY));

        int minTileX = (int) Math.floor(sweptBox.minX());
        int maxTileX = (int) Math.floor(sweptBox.maxX() - EPSILON);
        int minTileY = (int) Math.floor(sweptBox.minY());
        int maxTileY = (int) Math.floor(sweptBox.maxY() - EPSILON);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
                boolean isSolid = world.getTile(new TilePos(tileX, tileY))
                        .map(TileType::isSolid)
                        .orElse(false);
                if (!isSolid)
                    continue;

                double tileMinX = tileX;
                double tileMinY = tileY;
                double tileMaxX = tileX + 1.0;
                double tileMaxY = tileY + 1.0;

                if (deltaX != 0) {
                    boolean yOverlaps = box.maxY() > tileMinY && box.minY() < tileMaxY;
                    if (!yOverlaps)
                        continue;

                    if (clippedDelta > 0 && box.maxX() <= tileMinX) {
                        double maxMove = tileMinX - box.maxX() - EPSILON;
                        if (clippedDelta > maxMove) {
                            clippedDelta = maxMove;
                        }
                    } else if (clippedDelta < 0 && box.minX() >= tileMaxX) {
                        double minMove = tileMaxX - box.minX() + EPSILON;
                        if (clippedDelta < minMove) {
                            clippedDelta = minMove;
                        }
                    }
                } else {
                    boolean xOverlaps = box.maxX() > tileMinX && box.minX() < tileMaxX;
                    if (!xOverlaps)
                        continue;

                    if (clippedDelta > 0 && box.maxY() <= tileMinY) {
                        double maxMove = tileMinY - box.maxY() - EPSILON;
                        if (clippedDelta > maxMove) {
                            clippedDelta = maxMove;
                        }
                    } else if (clippedDelta < 0 && box.minY() >= tileMaxY) {
                        double minMove = tileMaxY - box.minY() + EPSILON;
                        if (clippedDelta < minMove) {
                            clippedDelta = minMove;
                        }
                    }
                }
            }
        }

        return clippedDelta;
    }

    private static double clipAgainstEntities(World world, Entity movingEntity, AABB box, double deltaX, double deltaY) {
        if (deltaX == 0 && deltaY == 0)
            return 0;

        double clippedDelta = deltaX != 0 ? deltaX : deltaY;
        AABB sweptBox = box.union(box.offset(deltaX, deltaY));

        for (Entity other : world.getEntities()) {
            if (other == movingEntity || other.isRemoved())
                continue;

            List<Entity> collisionTargets = getCollisionTargets(other);
            if (collisionTargets.isEmpty())
                continue;

            AABB otherBox = other.getAABB();
            if (!sweptBox.intersects(otherBox))
                continue;

            if (deltaX != 0) {
                boolean yOverlaps = box.maxY() > otherBox.minY() && box.minY() < otherBox.maxY();
                if (!yOverlaps)
                    continue;

                if (clippedDelta > 0 && box.maxX() <= otherBox.minX()) {
                    double maxMove = otherBox.minX() - box.maxX() - EPSILON;
                    if (clippedDelta > maxMove) {
                        clippedDelta = maxMove;
                        notifyEntityCollision(movingEntity, collisionTargets);
                    }
                } else if (clippedDelta < 0 && box.minX() >= otherBox.maxX()) {
                    double minMove = otherBox.maxX() - box.minX() + EPSILON;
                    if (clippedDelta < minMove) {
                        clippedDelta = minMove;
                        notifyEntityCollision(movingEntity, collisionTargets);
                    }
                }
            } else {
                boolean xOverlaps = box.maxX() > otherBox.minX() && box.minX() < otherBox.maxX();
                if (!xOverlaps)
                    continue;

                if (clippedDelta > 0 && box.maxY() <= otherBox.minY()) {
                    double maxMove = otherBox.minY() - box.maxY() - EPSILON;
                    if (clippedDelta > maxMove) {
                        clippedDelta = maxMove;
                        notifyEntityCollision(movingEntity, collisionTargets);
                    }
                } else if (clippedDelta < 0 && box.minY() >= otherBox.maxY()) {
                    double minMove = otherBox.maxY() - box.minY() + EPSILON;
                    if (clippedDelta < minMove) {
                        clippedDelta = minMove;
                        notifyEntityCollision(movingEntity, collisionTargets);
                    }
                }
            }
        }

        return clippedDelta;
    }

    private static void resolveOverlaps(World world, Entity entity) {
        for (int i = 0; i < MAX_PUSH_OUT_ITERATIONS; i++) {
            boolean changed = false;
            AABB entityBox = entity.getAABB();

            for (Entity other : world.getEntities()) {
                if (other == entity || other.isRemoved())
                    continue;

                List<Entity> collisionTargets = getCollisionTargets(other);
                if (collisionTargets.isEmpty())
                    continue;

                AABB otherBox = other.getAABB();
                if (!entityBox.intersects(otherBox))
                    continue;

                double overlapX = Math.min(entityBox.maxX(), otherBox.maxX()) - Math.max(entityBox.minX(), otherBox.minX());
                double overlapY = Math.min(entityBox.maxY(), otherBox.maxY()) - Math.max(entityBox.minY(), otherBox.minY());
                if (overlapX <= 0 || overlapY <= 0)
                    continue;

                boolean separateOnX = overlapX < overlapY;
                boolean otherImmovable = collisionTargets.stream().anyMatch(CollisionResolver::isImmovable);
                double separation = (separateOnX ? overlapX : overlapY) + PUSH_OUT_SLOP;

                if (separateOnX) {
                    double direction = entityBox.centerX() < otherBox.centerX() ? -1 : 1;
                    if (otherImmovable) {
                        applyOffset(entity, direction * separation, 0);
                    } else {
                        double half = separation * 0.5;
                        applyOffset(entity, direction * half, 0);
                        applyOffset(other, -direction * half, 0);
                    }
                    entity.getVelocity().set(0, entity.getVelocity().y());
                } else {
                    double direction = entityBox.centerY() < otherBox.centerY() ? -1 : 1;
                    if (otherImmovable) {
                        applyOffset(entity, 0, direction * separation);
                    } else {
                        double half = separation * 0.5;
                        applyOffset(entity, 0, direction * half);
                        applyOffset(other, 0, -direction * half);
                    }
                    entity.getVelocity().set(entity.getVelocity().x(), 0);
                }

                notifyEntityCollision(entity, collisionTargets);
                changed = true;
                entityBox = entity.getAABB();
            }

            if (!changed)
                return;
        }
    }

    private static void applyOffset(Entity entity, double offsetX, double offsetY) {
        if (offsetX == 0 && offsetY == 0)
            return;

        entity.setPosition(entity.getPosition().x + offsetX, entity.getPosition().y + offsetY);
    }

    private static boolean isImmovable(Entity entity) {
        return entity.getType().isImmovable();
    }

    private static List<Entity> getCollisionTargets(Entity entity) {
        if (entity instanceof StackedTileEntity stackedTileEntity)
            return List.copyOf(stackedTileEntity.getCollisionEntries());

        return List.of(entity);
    }

    private static void notifyEntityCollision(Entity entity, List<Entity> others) {
        for (Entity other : others) {
            entity.onEntityCollision(other);
            other.onEntityCollision(entity);
        }
    }
}
