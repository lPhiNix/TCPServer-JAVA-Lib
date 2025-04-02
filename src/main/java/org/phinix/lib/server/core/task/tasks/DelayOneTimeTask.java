package org.phinix.lib.server.core.task.tasks;

import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;

public abstract class DelayOneTimeTask<M extends Manageable> extends Task<M> {
    private final int beginMillis;
    private final int afterMillis;

    public DelayOneTimeTask(int beginMillis, int afterMillis) {
        this.beginMillis = beginMillis;
        this.afterMillis = afterMillis;
    }

    @Override
    protected void executeAsync(M manageable) {
        delay(beginMillis);
        process(manageable);
        delay(afterMillis);
    }
}
