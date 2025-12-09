package dev.turtywurty.mysticfactories.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEntrypoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientEntrypoint.class);

    public static void main(String[] args) {
        LOGGER.info("Starting MysticFactories client");
        new GameClient().start();
    }
}
