package org.phinix.example.server.core.thread.task;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.task.TaskQueue;

public class ClientTaskExecutor extends AbstractTaskExecutor<ClientHandler> {

    public ClientTaskExecutor(TaskQueue<ClientHandler> taskQueue) {
        super(taskQueue);
    }

    @Override
    protected int initTasks() {

        return getAmountRegisteredTasks();
    }
}
