package org.phinix.lib.server.core.task.tasks;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;

/**
 * Abstract class representing a task that executes once with delays before and after processing.
 *
 * @param <M> the type of manageable component associated with the task
 * @see Manageable
 * @see Task
 */
public abstract class DelayOneTimeTask<M extends Manageable> extends Task<M> {
    private static final Logger logger = LogManager.getLogger();

    private final int beginMillis; // Delay before task execution
    private final int afterMillis; // Delay after task execution

    /**
     * Constructs a new DelayOneTimeTask with the specified delays.
     *
     * @param beginMillis the delay before task execution in milliseconds
     * @param afterMillis the delay after task execution in milliseconds
     */
    public DelayOneTimeTask(int beginMillis, int afterMillis) {
        this.beginMillis = beginMillis;
        this.afterMillis = afterMillis;
    }

    /**
     * Executes the task asynchronously with delays before and after processing.
     *
     * @param manageable the manageable component
     */
    @Override
    protected void executeAsync(M manageable) {
        delay(beginMillis);
        logger.log(Level.DEBUG, "Task delayed before execution: {} ms", beginMillis);
        process(manageable);
        delay(afterMillis);
        logger.log(Level.DEBUG, "Task delayed after execution: {} ms", afterMillis);
    }
}