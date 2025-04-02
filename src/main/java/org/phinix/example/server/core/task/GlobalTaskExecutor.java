package org.phinix.example.server.core.task;

import org.phinix.example.server.core.TCPServer;
import org.phinix.example.server.core.task.tasks.GlobalExampleTask;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;

public class GlobalTaskExecutor extends AbstractTaskExecutor<TCPServer> {

    public GlobalTaskExecutor(TaskQueue<TCPServer> taskQueue) {
        super(taskQueue);
    }

    @Override
    protected int initTasks() {
        registerTasks(new GlobalExampleTask(1000));

        return getAmountRegisteredTasks();
    }
}
