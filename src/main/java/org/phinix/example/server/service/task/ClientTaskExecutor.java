package org.phinix.example.server.service.task;

import org.phinix.example.server.service.task.tasks.ExampleTask;
import org.phinix.example.server.thread.ClientHandler;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.service.services.AbstractTaskExecutor;

public class ClientTaskExecutor extends AbstractTaskExecutor<ClientHandler> {

    public ClientTaskExecutor(TaskQueue<ClientHandler> taskQueue) {
        super(taskQueue);
    }

    @Override
    protected int initTasks() {
        registerTasks(new ExampleTask(1000));

        return getAmountRegisteredTasks();
    }
}
