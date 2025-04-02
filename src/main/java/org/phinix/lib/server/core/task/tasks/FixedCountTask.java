package org.phinix.lib.server.core.task.tasks;

import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.Task;

public abstract class FixedCountTask<C extends Context> extends Task<C> {
    private final long millis;

    protected final int maxExecutions;
    protected int currentExecutions;
    
    public FixedCountTask(int maxExecutions, long millis) {
        this.maxExecutions = maxExecutions;
        this.millis = millis;
    }

    @Override
    public void executeAsync(C serverContext) {
        running = true;
        while (running && currentExecutions < maxExecutions) {
            if (paused) {
                pauseDelay();
                continue;
            }
            process(serverContext);
            delay(millis);
        }
        running = false;
    }
}