package org.phinix.lib.server.core.task.tasks;

import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.Task;

public abstract class LoopTask<C extends Context> extends Task<C> {
    protected final long millis;

    public LoopTask(long millis) {
        this.millis = millis;
    }

    @Override
    public void executeAsync(C serverContext) {
        running = true;
        while (running) {
            if (paused) {
                pauseDelay();
                continue;
            }
            process(serverContext);
            delay(millis);
        }
    }
}
