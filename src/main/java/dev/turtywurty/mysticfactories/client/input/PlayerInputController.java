package dev.turtywurty.mysticfactories.client.input;

import dev.turtywurty.mysticfactories.client.world.ClientWorld;
import dev.turtywurty.mysticfactories.world.entity.impl.PlayerEntity;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

/**
 * Listens to input and applies movement to the local player on the client side.
 */
public record PlayerInputController(ClientWorld world) implements InputListener {
    @Override
    public void onUpdate(double deltaTime) {
        Optional<PlayerEntity> playerOpt = world.getLocalPlayer()
                .filter(PlayerEntity.class::isInstance)
                .map(PlayerEntity.class::cast);
        if (playerOpt.isEmpty())
            return;

        PlayerEntity player = playerOpt.get();
        double dx = 0;
        double dy = 0;

        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_W)) dy += 1;
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_S)) dy -= 1;
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_D)) dx += 1;
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_A)) dx -= 1;

        double movementSpeedTilesPerSecond = player.getMovementSpeed();
        if (dx != 0 && dy != 0) {
            double invSqrt2 = 1 / Math.sqrt(2);
            dx *= invSqrt2;
            dy *= invSqrt2;
        }

        double worldUnitsPerSecond = movementSpeedTilesPerSecond * world.getTileSize();

        player.getVelocity().set(dx * worldUnitsPerSecond, dy * worldUnitsPerSecond);
        player.getPosition().fma(deltaTime, player.getVelocity());
    }
}
