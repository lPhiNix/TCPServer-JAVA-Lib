package org.phinix.lib.server.core.task;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskQueue {
    private final Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    public void addTask(Task task) {
        taskQueue.offer(task);
    }

    public Task getNextTask() {
        return taskQueue.poll();
    }

    public boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    public Queue<Task> getTaskQueue() {
        return new ConcurrentLinkedQueue<>(taskQueue);
    }
}
