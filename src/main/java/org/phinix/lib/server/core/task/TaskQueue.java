package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class representing a queue of tasks to be executed.
 *
 * @param <M> the type of manageable component associated with the tasks
 * @see Manageable
 */
public class TaskQueue<M extends Manageable> {
    private static final Logger logger = LogManager.getLogger();

    private final Queue<Task<M>> taskQueue; // Queue to store tasks

    /**
     * Constructs a new TaskQueue.
     */
    public TaskQueue() {
        taskQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Adds a task to the queue.
     *
     * @param task the task to be added
     */
    public void addTask(Task<M> task) {
        taskQueue.offer(task);
        logger.log(Level.DEBUG, "Task added to queue: {}", task.getName());
    }

    /**
     * Retrieves and removes the next task from the queue.
     *
     * @return the next task, or {@code null} if the queue is empty
     */
    public Task<M> getNextTask() {
        return taskQueue.poll();
    }

    /**
     * Returns whether the queue has tasks.
     *
     * @return {@code true} if the queue has tasks, {@code false} otherwise
     */
    public boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    /**
     * Returns an unmodifiable copy of the task queue.
     *
     * @return an unmodifiable copy of the task queue
     */
    public Queue<Task<M>> getTaskQueue() {
        return new ConcurrentLinkedQueue<>(taskQueue);
    }
}