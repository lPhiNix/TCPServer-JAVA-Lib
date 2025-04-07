package org.phinix.lib.server.core.task.tasks;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;

/**
 * Abstract class representing a task that executes repeatedly with a delay between each execution.
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
        this.millis = millis;
    }

    /**
     * Executes the task asynchronously in a loop with a delay between each execution.
     *
     * @param manageable the manageable component
     */
    @Override
    public void executeAsync(M manageable) {
        running = true;
        while (running) {
            if (paused) {
                pauseDelay();
                logger.log(Level.DEBUG, "Task paused for: {} ms", PAUSE_DELAY_MILLIS);
                continue;
            }
            process(manageable);
            delay(millis);
            logger.log(Level.DEBUG, "Task delayed between executions: {} ms", millis);
        }
    }
}