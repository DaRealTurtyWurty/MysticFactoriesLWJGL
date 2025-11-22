package dev.turtywurty.mysticfactories.server;

public class ServerEntrypoint {
    public static void main(String[] args) {
        DedicatedServer server = new DedicatedServer();
        server.start();
    }
}
