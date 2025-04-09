package org.phinix.lib.server.core.task.tasks;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;

/**
 * {@code LoopTask} abstract class representing a task that executes repeatedly with a delay between each execution.
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
public abstract class LoopTask<M extends Manageable> extends Task<M> {
    private static final Logger logger = LogManager.getLogger();

    protected final long millis; // Delay between each task execution

    /**
     * Constructs a new LoopTask with the specified delay.
     *
     * @param millis the delay between each task execution in milliseconds
     */
    public LoopTask(long millis) {
        this.millis = millis; // Set the delay between task executions
    }

    /**
     * Executes the task asynchronously in a loop with a delay between each execution.
     *
     * @param manageable the manageable component
     */
    @Override
    public void executeAsync(M manageable) {
        running = true; // Set task as running
        while (running) { // Keep executing while the task is running
            if (paused) {
                pauseDelay(); // Pause if the task is paused
                logger.log(Level.DEBUG, "Task paused for: {} ms", PAUSE_DELAY_MILLIS); // Log pause delay
                continue; // Skip the rest of the loop iteration if paused
            }
            process(manageable); // Execute the task's work
            delay(millis); // Wait for the specified delay before the next execution
            logger.log(Level.DEBUG, "Task delayed between executions: {} ms", millis); // Log the delay
        }
    }
}