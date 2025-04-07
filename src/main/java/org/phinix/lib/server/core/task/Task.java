package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;

/**
 * Abstract class representing a task that can be executed asynchronously.
 *
 * @param <M> the type of manageable component associated with the task
 * @see Manageable
 */
public abstract class Task<M extends Manageable> {
    private static final Logger logger = LogManager.getLogger();

    protected static final long PAUSE_DELAY_MILLIS = 100; // Delay time for pausing

    protected Thread threadTask; // The thread in which the task runs
    protected boolean running; // Flag indicating whether the task is running
    protected boolean paused; // Flag indicating whether the task is paused

    /**
     * Constructs a new Task.
     */
    public Task() {
        this.running = false;
        this.paused = false;
    }

    /**
     * Processes the task with the given manageable component.
     *
     * @param manageable the manageable component
     */
    public abstract void process(M manageable);

    /**
     * Executes the task asynchronously with the given manageable component.
     *
     * @param manageable the manageable component
     */
    protected abstract void executeAsync(M manageable);

    /**
     * Delays the task for the given number of milliseconds.
     *
     * @param millis the delay time in milliseconds
     */
    protected void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Pauses the task for a predefined delay time.
     */
    protected void pauseDelay() {
        delay(PAUSE_DELAY_MILLIS);
    }

    /**
     * Starts the task with the given server context.
     *
     * @param serverContext the server context
     */
    public void start(M serverContext) {
        if (!running) {
            threadTask = new Thread(() -> this.executeAsync(serverContext));
            threadTask.start();
            logger.log(Level.DEBUG, "Task started: {}", getName());
        }
    }

    /**
     * Pauses the task.
     */
    public void pause() {
        paused = true;
        logger.log(Level.DEBUG, "Task paused: {}", getName());
    }

    /**
     * Resumes the task.
     */
    public void resumeTask() {
        paused = false;
        logger.log(Level.DEBUG, "Task resumed: {}", getName());
    }

    /**
     * Stops the task.
     */
    public void stop() {
        running = false;
        if (threadTask != null) {
            threadTask.interrupt();
        }
        logger.log(Level.DEBUG, "Task stopped: {}", getName());
    }

    /**
     * Returns whether the task is running.
     *
     * @return {@code true} if the task is running, {@code false} otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the name of the task.
     *
     * @return the name of the task
     */
    public String getName() {
        return threadTask.getName();
    }
}