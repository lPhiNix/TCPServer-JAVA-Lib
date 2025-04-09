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

        int amountRegisteredTasks = initTasks();
        logger.log(Level.INFO, "{} tasks registered in server", amountRegisteredTasks);
    }

    /**
     * Initializes the tasks.
     *
     * @return the number of registered tasks
     */
    protected abstract int initTasks();

    /**
     * Registers a task to the task queue.
     *
     * @param task the task to be registered
     */
    protected void registerTasks(Task<M> task) {
        taskQueue.addTask(task);
        logger.log(Level.DEBUG, "Task registered: {}", task.getName());
    }

    /**
     * Starts the task executor with the given server context.
     *
     * @param serverContext the server context
     */
    public void start(M serverContext) {
        running = true;
        try {
            while (taskQueue.hasTasks()) {
                Task<M> task = taskQueue.getNextTask();
                if (task != null) {
                    taskThreads.add(task);
                    logger.log(Level.DEBUG, "Task added to execution list: {}", task.getName());
                }
            }

            startAllInQueue(serverContext);

            if (!taskQueue.hasTasks()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Starts all tasks in the queue with the given server context.
     *
     * @param serverContext the server context
     */
    private void startAllInQueue(M serverContext) {
        for (Task<M> task : taskThreads) {
            task.start(serverContext);
        }
    }

    /**
     * Stops all running tasks.
     */
    public void stop() {
        running = false;
        for (Task<M> task : taskThreads) {
            logger.log(Level.DEBUG, "Stopping task: {}:{}", task.getClass().getSimpleName(), task.getName());
            task.stop();
        }
        taskThreads.clear();
    }

    /**
     * Returns the number of registered tasks.
     *
     * @return the number of registered tasks
     */
    public int getAmountRegisteredTasks() {
        return taskQueue.getTaskQueue().size();
    }

    /**
     * Returns the number of running tasks.
     *
     * @return the number of running tasks
     */
    public int getAmountRunningTasks() {
        return taskThreads.size();
    }

    /**
     * Returns whether the task executor is running.
     *
     * @return {@code true} if the task executor is running, {@code false} otherwise
     */
    public boolean isRunning() {
        return running;
    }
}