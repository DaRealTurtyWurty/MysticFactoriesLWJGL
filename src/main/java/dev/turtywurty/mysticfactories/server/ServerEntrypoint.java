package dev.turtywurty.mysticfactories.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerEntrypoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEntrypoint.class);

    public static void main(String[] args) {
        LOGGER.info("Starting MysticFactories dedicated server");

        DedicatedServer server = new DedicatedServer();
        server.start();
    }
}
