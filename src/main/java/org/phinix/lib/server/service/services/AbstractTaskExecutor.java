package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;
import org.phinix.lib.server.core.task.Task;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.service.Service;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractTaskExecutor<M extends Manageable> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final TaskQueue<M> taskQueue;
    private final List<Thread> taskThreads;
    private boolean running;

    public AbstractTaskExecutor(TaskQueue<M> taskQueue) {
        this.taskQueue = taskQueue;

        this.taskThreads = new ArrayList<>();
        this.running = false;

        int amountRegisteredTasks = initTasks();
        logger.log(Level.INFO, "{} tasks registered in server", amountRegisteredTasks);
    }

    protected abstract int initTasks();

    protected void registerTasks(Task<M> task) {
        taskQueue.addTask(task);
    }

    public void start(M manageable) {
        running = true;
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                while (taskQueue.hasTasks()) {
                    Task<M> task = taskQueue.getNextTask();
                    if (task != null) {
                        taskThreads.add(new Thread(() -> {
                            task.executeAsync(manageable);
                        }));
                    }
                }

                for (Thread thread : taskThreads) {
                    thread.start();
                }

                if (!taskQueue.hasTasks()) {
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
        for (Thread thread : taskThreads) {
            thread.interrupt();
        }
        taskThreads.clear();
    }

    public int getAmountRegisteredTasks() {
        return taskQueue.getTaskQueue().size();
    }

    public int getAmountRunningTasks() {
        return taskThreads.size();
    }

    public boolean isRunning() {
        return running;
    }
}