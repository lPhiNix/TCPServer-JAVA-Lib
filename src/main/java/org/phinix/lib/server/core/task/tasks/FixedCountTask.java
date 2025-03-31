package org.phinix.lib.server.core.task.tasks;

import org.phinix.lib.server.core.task.Task;

public abstract class FixedCountTask extends Task {
    private final long millis;

    protected final int maxExecutions;
    protected int currentExecutions;
    
    public FixedCountTask(int maxExecutions, long millis) {
        this.maxExecutions = maxExecutions;
        this.millis = millis;
    }

    @Override
    protected void execute() {
        running = true;
        while (running && currentExecutions < maxExecutions) {
            if (paused) {
                pauseDelay();
                continue;
            }
            task();
            delay(millis);
        }
        running = false;
    }
}