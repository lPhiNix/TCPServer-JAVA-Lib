package org.phinix.lib.server.core.task.tasks;

import org.phinix.lib.server.core.task.Task;

public abstract class LoopTask extends Task {
    protected final long millis;

    public LoopTask(long millis) {
        this.millis = millis;
    }

    @Override
    protected void execute() {
        running = true;
        while (running) {
            if (paused) {
                pauseDelay();
                continue;
            }
            task();
            delay(millis);
        }
    }
}
