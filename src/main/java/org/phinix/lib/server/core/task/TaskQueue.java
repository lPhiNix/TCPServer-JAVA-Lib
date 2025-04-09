package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * {@code TaskQueue} class representing a queue of tasks to be executed.
 * <p>
 * This class is used to manage a queue of tasks. Tasks can be added to the queue using {@link #addTask(Task)} and
 * retrieved and removed with {@link #getNextTask()}. It also provides methods to check if there are tasks in the queue
 * and to retrieve a copy of the queue.
 *
 * @param <M> the type of manageable component associated with the tasks
 * @see Manageable
 */
public class TaskQueue<M extends Manageable> {
    private static final Logger logger = LogManager.getLogger();

    private final Queue<Task<M>> taskQueue; // Queue to store tasks

    /**
     * Constructs a new TaskQueue.
     * <p>
     * This constructor initializes a new {@link ConcurrentLinkedQueue}, which is thread-safe and allows concurrent access.
     * It ensures that the queue can handle multiple threads performing task operations.
     */
    public TaskQueue() {
        taskQueue = new ConcurrentLinkedQueue<>(); // Initialize the queue
        logger.log(Level.DEBUG, "TaskQueue initialized."); // Log the initialization of the TaskQueue
    }

    /**
     * Adds a task to the queue.
     * <p>
     * This method adds the given task to the queue using the {@link ConcurrentLinkedQueue#offer(Object)} method,
     * which is a thread-safe operation.
     *
     * @param task the task to be added
     */
    public void addTask(Task<M> task) {
        taskQueue.offer(task); // Add the task to the queue
        logger.log(Level.DEBUG, "Task added to queue: {}", task.getName()); // Log the task addition
    }

    /**
     * Retrieves and removes the next task from the queue.
     * <p>
     * This method removes and returns the next task in the queue using {@link ConcurrentLinkedQueue#poll()},
     * which returns {@code null} if the queue is empty.
     *
     * @return the next task, or {@code null} if the queue is empty
     */
    public Task<M> getNextTask() {
        Task<M> task = taskQueue.poll(); // Retrieve and remove the next task from the queue
        if (task == null) {
            logger.log(Level.DEBUG, "No tasks in the queue."); // Log when the queue is empty
        } else {
            logger.log(Level.DEBUG, "Retrieved task from queue: {}", task.getName()); // Log the task retrieval
        }
        return task;
    }

    /**
     * Returns whether the queue has tasks.
     * <p>
     * This method checks if the queue is not empty by calling {@link ConcurrentLinkedQueue#isEmpty()} and
     * returns {@code true} if there are tasks in the queue.
     *
     * @return {@code true} if the queue has tasks, {@code false} otherwise
     */
    public boolean hasTasks() {
        boolean hasTasks = !taskQueue.isEmpty(); // Check if the queue is not empty
        logger.log(Level.DEBUG, "Queue has tasks: {}", hasTasks); // Log if there are tasks in the queue
        return hasTasks;
    }

    /**
     * Returns an unmodifiable copy of the task queue.
     * <p>
     * This method returns a copy of the task queue that cannot be modified directly, ensuring thread safety.
     *
     * @return an unmodifiable copy of the task queue
     */
    public Queue<Task<M>> getTaskQueue() {
        Queue<Task<M>> copyQueue = new ConcurrentLinkedQueue<>(taskQueue); // Create a copy of the task queue
        logger.log(Level.DEBUG, "Task queue copy created with {} tasks.", copyQueue.size()); // Log the creation of the queue copy
        return copyQueue;
    }
}