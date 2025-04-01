package org.phinix.lib.server.core.task.tasks;

import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;

public abstract class FixedCountTask<M extends Manageable> extends Task<M> {
    private final long millis;

    protected final int maxExecutions;
    protected int currentExecutions;
    
    public FixedCountTask(int maxExecutions, long millis) {
        this.maxExecutions = maxExecutions;
        this.millis = millis;
    }

    @Override
    public void executeAsync(M manageable) {
        running = true;
        while (running && currentExecutions < maxExecutions) {
            if (paused) {
                pauseDelay();
                continue;
            }
            process(manageable);
            delay(millis);
        }
        running = false;
    }
}