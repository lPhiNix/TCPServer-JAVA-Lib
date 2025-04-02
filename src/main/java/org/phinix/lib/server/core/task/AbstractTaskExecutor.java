package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.context.Context;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractTaskExecutor<C extends Context> {
    private static final Logger logger = LogManager.getLogger();

    private final TaskQueue<C> taskQueue;
    private final List<Task<C>> taskThreads;
    private boolean running;

    public AbstractTaskExecutor(TaskQueue<C> taskQueue) {
        this.taskQueue = taskQueue;

        this.taskThreads = new ArrayList<>();
        this.running = false;

        int amountRegisteredTasks = initTasks();
        logger.log(Level.INFO, "{} tasks registered in server", amountRegisteredTasks);
    }

    protected abstract int initTasks();

    protected void registerTasks(Task<C> task) {
        taskQueue.addTask(task);
    }

    public void start(C serverContext) {
        running = true;
        try {
            while (taskQueue.hasTasks()) {
                Task<C> task = taskQueue.getNextTask();
                if (task != null) {
                    taskThreads.add(task);
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

    private void startAllInQueue(C serverContext) {
        for (Task<C> task : taskThreads) {
            task.start(serverContext);
        }
    }

    public void stop() {
        running = false;
        for (Task<C> task : taskThreads) {
            logger.log(Level.DEBUG, "Stopping task: {}:{}" ,
                    new Object[]{task.getClass().getSimpleName(), task.getName()});
            task.stop();
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