package org.phinix.lib.server.core.task.tasks;

import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;

public abstract class LoopTask<M extends Manageable> extends Task<M> {
    protected final long millis;

    public LoopTask(long millis) {
        this.millis = millis;
    }

    @Override
    public void executeAsync(M manageable) {
        running = true;
        while (running) {
            if (paused) {
                pauseDelay();
                continue;
            }
            process(manageable);
            delay(millis);
        }
    }
}
