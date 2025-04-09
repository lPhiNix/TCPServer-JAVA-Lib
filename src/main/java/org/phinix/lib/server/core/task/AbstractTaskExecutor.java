package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.server.core.Manageable;

import java.util.List;
import java.util.ArrayList;

/**
 * {@code AbstractTaskExecutor} abstract class for managing the execution of multiple tasks.
 * <p>
 * The key method in this class is {@link #start(M)}, which begins executing tasks asynchronously by pulling them from the
 * task queue and starting them in their own threads. Tasks can be registered through the {@link #registerTasks(Task)} method.
 * Additionally, the task executor can be stopped, and the number of tasks registered and running can be queried using
 * {@link #getAmountRegisteredTasks()} and {@link #getAmountRunningTasks()}.
 * <p>
 * Example use:
 * <pre>{@code
 * public class MyTaskExecutor extends AbstractTaskExecutor<MyServer> {
 *     @Override
 *     protected int initTasks() {
 *         registerTasks(new Task1<>());
 *         registerTasks(new Task2<>());
 *         registerTasks(new Task3<>());
 *         registerTasks(new Task4<>());
 *
 *         // Return the number of registered tasks
 *         return getAmountRegisteredTasks();
 *     }
 * }
 * }
 *
 * @param <M> the type of manageable component associated with the tasks
 * @see Manageable
 */
public abstract class AbstractTaskExecutor<M extends Manageable> {
    private static final Logger logger = LogManager.getLogger();

    private final TaskQueue<M> taskQueue; // Queue of tasks to be executed
    private final List<Task<M>> taskThreads; // List of running task threads
    private boolean running; // Flag indicating whether the task executor is running

    /**
     * Constructs an AbstractTaskExecutor with the specified task queue.
     *
     * @param taskQueue the task queue
     */
    public AbstractTaskExecutor(TaskQueue<M> taskQueue) {
        this.taskQueue = taskQueue;

        this.taskThreads = new ArrayList<>();
        this.running = false;

        // Initialize tasks and log the number of tasks registered
        int amountRegisteredTasks = initTasks();
        logger.log(Level.INFO, "{} tasks registered in server", amountRegisteredTasks);
    }

    /**
     * Initializes the tasks.
     * <p>
     * This method should be overridden by subclasses to register the specific tasks.
     * It returns the number of registered tasks, which will be logged.
     *
     * @return the number of registered tasks
     */
    protected abstract int initTasks();

    /**
     * Registers a task to the task queue.
     * <p>
     * This method adds the task to the task queue and logs the registration.
     *
     * @param task the task to be registered
     */
    protected void registerTasks(Task<M> task) {
        taskQueue.addTask(task); // Add the task to the task queue
        logger.log(Level.DEBUG, "Task registered: {}", task.getName()); // Log the registration of the task
    }

    /**
     * Starts the task executor with the given server context.
     * <p>
     * This method pulls tasks from the task queue and starts them in separate threads.
     * It logs the task being added to the execution list.
     *
     * @param serverContext the server context
     */
    public void start(M serverContext) {
        running = true; // Set the running flag to true
        try {
            // Process tasks in the task queue
            while (taskQueue.hasTasks()) {
                Task<M> task = taskQueue.getNextTask();
                if (task != null) {
                    taskThreads.add(task); // Add the task to the task execution list
                    logger.log(Level.DEBUG, "Task added to execution list: {}", task.getName()); // Log the addition
                }
            }

            // Start all tasks in the queue
            startAllInQueue(serverContext);

            // If there are no tasks, sleep for a while
            if (!taskQueue.hasTasks()) {
                Thread.sleep(100); // Sleep for 100 milliseconds before checking again
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt(); // Handle any interruptions
            logger.log(Level.DEBUG, "Task executor interrupted during start process."); // Log interruption
        }
    }

    /**
     * Starts all tasks in the queue with the given server context.
     * <p>
     * This method iterates over all tasks in the execution list and starts them.
     *
     * @param serverContext the server context
     */
    private void startAllInQueue(M serverContext) {
        for (Task<M> task : taskThreads) {
            task.start(serverContext); // Start each task
            logger.log(Level.DEBUG, "Task started: {}", task.getName()); // Log the start of the task
        }
    }

    /**
     * Stops all running tasks.
     * <p>
     * This method sets the running flag to false and stops all tasks in the task execution list.
     * It clears the list after stopping all tasks.
     */
    public void stop() {
        running = false; // Set the running flag to false
        for (Task<M> task : taskThreads) {
            logger.log(Level.DEBUG, "Stopping task: {}:{}", task.getClass().getSimpleName(), task.getName()); // Log the task stop
            task.stop(); // Stop the task
        }
        taskThreads.clear(); // Clear the list of task threads
        logger.log(Level.DEBUG, "All tasks stopped and execution list cleared."); // Log clearing of task list
    }

    /**
     * Returns the number of registered tasks.
     * <p>
     * This method returns the size of the task queue, which represents the number of tasks registered.
     *
     * @return the number of registered tasks
     */
    public int getAmountRegisteredTasks() {
        return taskQueue.getTaskQueue().size(); // Return the size of the task queue
    }

    /**
     * Returns the number of running tasks.
     * <p>
     * This method returns the size of the task execution list, which represents the number of tasks currently running.
     *
     * @return the number of running tasks
     */
    public int getAmountRunningTasks() {
        return taskThreads.size(); // Return the size of the task execution list
    }

    /**
     * Returns whether the task executor is running.
     * <p>
     * This method checks the running flag to determine if the executor is currently active.
     *
     * @return {@code true} if the task executor is running, {@code false} otherwise
     */
    public boolean isRunning() {
        return running; // Return the running status of the executor
    }
}