package org.phinix.lib.server.core.task.tasks;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.task.Task;

/**
 * Abstract class representing a task that executes once with delays before and after processing.
 * <p>
 * This class extends from {@link Task} and can be executed by {@link AbstractTaskExecutor}.
 * <p>
 * This class provides an implementation of the {@link #executeAsync(M)} method, which handles the execution of the
 * task with a delay before and after the actual work is done. The {@link #process(M)} method is still abstract and
 * must be overridden by subclasses to define the specific work that needs to be performed.
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
        this.beginMillis = beginMillis; // Set delay before task execution
        this.afterMillis = afterMillis; // Set delay after task execution
    }

    /**
     * Executes the task asynchronously with delays before and after processing.
     *
     * @param manageable the manageable component
     */
    @Override
    protected void executeAsync(M manageable) {
        delay(beginMillis); // Wait for the initial delay before executing the task
        logger.log(Level.DEBUG, "Task delayed before execution: {} ms", beginMillis); // Log the delay before execution
        process(manageable); // Execute the task's work
        delay(afterMillis); // Wait for the delay after executing the task
        logger.log(Level.DEBUG, "Task delayed after execution: {} ms", afterMillis); // Log the delay after execution
    }
}