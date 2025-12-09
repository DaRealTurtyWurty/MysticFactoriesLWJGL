package dev.turtywurty.mysticfactories.logging;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Rolls the active log file once during application startup so each run gets its own archive.
 * Further time-based rollovers are disabled.
 */
@NoAutoStart
public class StartupTimeBasedTriggeringPolicy<E> extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> {
    private final AtomicBoolean rolledOver = new AtomicBoolean(false);

    @Override
    public void start() {
        super.start();

        // Ensure the very first event triggers a rollover
        atomicNextCheck.set(0L);
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        // Roll once on the first log event, then disable further time-based checks
        if (rolledOver.get())
            return false;

        boolean shouldTrigger = super.isTriggeringEvent(activeFile, event);
        if (shouldTrigger && rolledOver.compareAndSet(false, true)) {
            atomicNextCheck.set(Long.MAX_VALUE);
        }

        return shouldTrigger;
    }
}
