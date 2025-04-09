package org.phinix.lib.server.core.task.tasks;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.task.Task;

/**
 * Abstract class representing a task that executes a fixed number of times with a delay between each execution.
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
        this.maxExecutions = maxExecutions; // Set maximum executions
        this.millis = millis; // Set the delay between executions
        this.currentExecutions = 0; // Initialize current executions to 0
    }

    /**
     * Executes the task asynchronously a fixed number of times with a delay between each execution.
     *
     * @param manageable the manageable component
     */
    @Override
    public void executeAsync(M manageable) {
        running = true; // Set task as running
        while (running && currentExecutions < maxExecutions) { // Continue until the max executions are reached
            if (paused) {
                pauseDelay(); // Pause if the task is paused
                logger.log(Level.DEBUG, "Task paused for: {} ms", PAUSE_DELAY_MILLIS); // Log the pause
                continue; // Skip the rest of the loop iteration if paused
            }
            process(manageable); // Execute the task's work
            currentExecutions++; // Increment the execution count
            delay(millis); // Wait for the specified delay before the next execution
            logger.log(Level.DEBUG, "Task executed {} out of {} times with delay: {} ms", currentExecutions, maxExecutions, millis); // Log execution count and delay
        }
        running = false; // Stop running when the task is completed
        logger.log(Level.DEBUG, "Task execution completed: {} times", currentExecutions); // Log task completion
    }
}