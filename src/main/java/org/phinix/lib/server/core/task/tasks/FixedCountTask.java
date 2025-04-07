package org.phinix.lib.server.core.task.tasks;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;

/**
 * Abstract class representing a task that executes a fixed number of times with a delay between each execution.
 *
 * @param <M> the type of manageable component associated with the task
 * @see Manageable
 * @see Task
 */
public abstract class FixedCountTask<M extends Manageable> extends Task<M> {
    private static final Logger logger = LogManager.getLogger();

    private final long millis; // Delay between each task execution

    protected final int maxExecutions; // Maximum number of task executions
    protected int currentExecutions; // Current number of task executions

    /**
     * Constructs a new FixedCountTask with the specified maximum executions and delay.
     *
     * @param maxExecutions the maximum number of task executions
     * @param millis the delay between each task execution in milliseconds
     */
    public FixedCountTask(int maxExecutions, long millis) {
        this.maxExecutions = maxExecutions;
        this.millis = millis;
        this.currentExecutions = 0;
    }

    /**
     * Executes the task asynchronously a fixed number of times with a delay between each execution.
     *
     * @param manageable the manageable component
     */
    @Override
    public void executeAsync(M manageable) {
        running = true;
        while (running && currentExecutions < maxExecutions) {
            if (paused) {
                pauseDelay();
                logger.log(Level.DEBUG, "Task paused for: {} ms", PAUSE_DELAY_MILLIS);
                continue;
            }
            process(manageable);
            currentExecutions++;
            delay(millis);
            logger.log(Level.DEBUG, "Task executed {} out of {} times with delay: {} ms", currentExecutions, maxExecutions, millis);
        }
        running = false;
        logger.log(Level.DEBUG, "Task execution completed: {} times", currentExecutions);
    }
}