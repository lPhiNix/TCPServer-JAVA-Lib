package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractTaskExecutor {

    private static final Logger logger = LogManager.getLogger();

    private final TaskQueue taskQueue;
    private final List<Thread> taskThreads;

    public AbstractTaskExecutor() {
        this.taskQueue = new TaskQueue();
        this.taskThreads = new ArrayList<>();

        int amountRegisteredTasks = initTasks();
        logger.log(Level.INFO, "{} tasks registered in server", amountRegisteredTasks);
    }

    protected abstract int initTasks();

    public void start() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (taskQueue.hasTasks()) {
                    Task task = taskQueue.getNextTask();
                    if (task != null) {
                        task.start();
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        for (Thread thread : taskThreads) {
            thread.interrupt();
        }
        taskThreads.clear();
    }

    public void addTask(Task task) {
        taskQueue.addTask(task);
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }
}
