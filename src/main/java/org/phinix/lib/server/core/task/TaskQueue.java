package org.phinix.lib.server.core.task;

import org.phinix.lib.server.context.Context;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue<C extends Context> {
    private final Queue<Task<C>> taskQueue;

    public TaskQueue() {
        taskQueue = new ConcurrentLinkedQueue<>();
    }

    public void addTask(Task<C> task) {
        taskQueue.offer(task);
    }

    public Task<C> getNextTask() {
        return taskQueue.poll();
    }

    public boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    public Queue<Task<C>> getTaskQueue() {
        return new ConcurrentLinkedQueue<>(taskQueue);
    }
}
