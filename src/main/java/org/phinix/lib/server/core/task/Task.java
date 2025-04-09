package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.Server;
import org.phinix.lib.server.core.worker.Worker;
import org.phinix.lib.server.session.Session;

/**
 * {@code Task} abstract class representing a task that can be executed asynchronously.
 * <p>
 * This task is handled by an {@link AbstractTaskExecutor} instance, and they can be run by
 * {@link Manageable} subclasses like {@link Server}, {@link Worker}, and {@link Session}, living as
 * long as the lifecycle of these types permits.
 * <p>
 * There are more specific abstract Task implementations in the {@link org.phinix.lib.server.core.task.tasks} package
 * where the {@link #executeAsync(Manageable)} method is already implemented.
 * <p>
 * The method {@link #process(M)} is intended to be overridden by subclasses to define the specific task's logic.
 * It is responsible for performing the actual work of the task when it is executed.
 * <p>
 * The method {@link #executeAsync(M)} is the entry point for running the task asynchronously. It handles the logic
 * for running the task in a separate thread, including pausing, delaying, and repeatedly executing the task
 * if applicable (in the case of looping tasks).
 * <p>
 * Example use:
 *
 * <pre>{@code
 * public class MyTask extends Task<MyServer> {
 *     @Override
 *     public void process(MyServer server) {
 *         // Here we define the specific work for this task
 *         System.out.println(
 *              "Executing task with component: " + server.getName()
 *         );
 *     }
 *
 *     @Override
 *     protected void executeAsync(MyServer server) {
 *         running = true;
 *         while (running) {
 *             if (paused) {
 *                 pauseDelay();
 *                 continue;
 *             }
 *             process(server);
 *             delay(1000); // Delay of 1 second between executions
 *         }
 *     }
 * }}
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

    public Task() {
        this.running = false;
        this.paused = false;
    }

    /**
     * Execute this task.
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