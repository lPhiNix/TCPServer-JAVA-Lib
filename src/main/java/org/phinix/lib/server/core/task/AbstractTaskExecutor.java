package org.phinix.lib.server.core.task;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.server.core.Manageable;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractTaskExecutor<M extends Manageable> {
    private static final Logger logger = LogManager.getLogger();

    private final TaskQueue<M> taskQueue;
    private final List<Task<M>> taskThreads;
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

    public void start(M serverContext) {
        running = true;
        try {
            while (taskQueue.hasTasks()) {
                Task<M> task = taskQueue.getNextTask();
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

    private void startAllInQueue(M serverContext) {
        for (Task<M> task : taskThreads) {
            task.start(serverContext);
        }
    }

    public void stop() {
        running = false;
        for (Task<M> task : taskThreads) {
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