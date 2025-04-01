package org.phinix.lib.server.core.task;

import org.phinix.lib.server.core.Manageable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue<M extends Manageable> {
    private final Queue<Task<M>> taskQueue;

    public TaskQueue() {
        taskQueue = new ConcurrentLinkedQueue<>();
    }

    public void addTask(Task<M> task) {
        taskQueue.offer(task);
    }

    public Task<M> getNextTask() {
        return taskQueue.poll();
    }

    public boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    public Queue<Task<M>> getTaskQueue() {
        return new ConcurrentLinkedQueue<>(taskQueue);
    }
}
