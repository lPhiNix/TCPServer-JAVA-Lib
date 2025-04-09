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
        this.running = false; // Task is not running initially
        this.paused = false; // Task is not paused initially
    }

    /**
     * Execute this task.
     * <p>
     * This method should be implemented by subclasses to define the specific work that the task performs.
     * It is the core function of the task that gets executed when the task is running.
     *
     * @param manageable the manageable component
     */
    public abstract void process(M manageable);

    /**
     * Executes the task asynchronously with the given manageable component.
     * <p>
     * This method is responsible for running the task in a separate thread. It handles the logic for pausing,
     * delaying, and executing the task in loops if necessary.
     *
     * @param manageable the manageable component
     */
    protected abstract void executeAsync(M manageable);

    /**
     * Delays the task for the given number of milliseconds.
     * <p>
     * This method is used to introduce a delay before continuing with the next task execution.
     *
     * @param millis the delay time in milliseconds
     */
    protected void delay(long millis) {
        try {
            Thread.sleep(millis); // Sleep the current thread for the specified time
            logger.log(Level.DEBUG, "Task delayed for {} milliseconds.", millis); // Log delay for debugging purposes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Interrupt current thread if interrupted during sleep
            logger.log(Level.DEBUG, "Task was interrupted during delay."); // Log interruption for debugging purposes
        }
    }

    /**
     * Pauses the task for a predefined delay time.
     * <p>
     * This method introduces a fixed delay (PAUSE_DELAY_MILLIS) before the task can resume.
     */
    protected void pauseDelay() {
        delay(PAUSE_DELAY_MILLIS); // Calls delay method with predefined pause delay time
    }

    /**
     * Starts the task with the given server context.
     * <p>
     * This method creates a new thread for the task and starts it.
     * It will log that the task has started.
     *
     * @param serverContext the server context
     */
    public void start(M serverContext) {
        if (!running) { // If the task is not already running
            threadTask = new Thread(() -> this.executeAsync(serverContext)); // Create a new thread to execute the task asynchronously
            threadTask.start(); // Start the thread
            logger.log(Level.DEBUG, "Task started: {}", getName()); // Log that the task has started
        }
    }

    /**
     * Pauses the task.
     * <p>
     * This method sets the paused flag to true and logs that the task has been paused.
     */
    public void pause() {
        paused = true; // Set paused flag to true
        logger.log(Level.DEBUG, "Task paused: {}", getName()); // Log that the task has been paused
    }

    /**
     * Resumes the task.
     * <p>
     * This method sets the paused flag to false and logs that the task has been resumed.
     */
    public void resumeTask() {
        paused = false; // Set paused flag to false
        logger.log(Level.DEBUG, "Task resumed: {}", getName()); // Log that the task has been resumed
    }

    /**
     * Stops the task.
     * <p>
     * This method stops the task by setting the running flag to false.
     * It interrupts the task thread and logs the task stop event.
     */
    public void stop() {
        running = false; // Set running flag to false
        if (threadTask != null) {
            threadTask.interrupt(); // Interrupt the task thread
        }
        logger.log(Level.DEBUG, "Task stopped: {}", getName()); // Log that the task has been stopped
    }

    /**
     * Returns whether the task is running.
     * <p>
     * This method checks the running flag to determine if the task is currently running.
     *
     * @return {@code true} if the task is running, {@code false} otherwise
     */
    public boolean isRunning() {
        return running; // Return the running status of the task
    }

    /**
     * Returns the name of the task.
     * <p>
     * This method retrieves the name of the thread running the task.
     *
     * @return the name of the task
     */
    public String getName() {
        return threadTask.getName(); // Return the name of the thread task
    }
}