package dev.turtywurty.mysticfactories.server;

public class DedicatedServer extends Server implements Runnable {
    private static final int TARGET_TPS = 30;
    private final Thread serverThread = new Thread(this, "dedicated-server");
    private volatile boolean running;

    public void start() {
        if (this.running) return;
        this.running = true;
        this.serverThread.start();
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        final double tps = 1.0 / TARGET_TPS;
        double lastTime = System.nanoTime() / 1_000_000_000.0;
        double accumulator = 0.0;

        while (this.running) {
            double now = System.nanoTime() / 1_000_000_000.0;
            double frame = now - lastTime;
            lastTime = now;
            accumulator += frame;

            while (accumulator >= tps) {
                tick(tps);
                accumulator -= tps;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
