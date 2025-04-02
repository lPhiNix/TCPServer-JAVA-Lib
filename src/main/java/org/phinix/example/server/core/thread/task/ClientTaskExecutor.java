package org.phinix.example.server.core.thread.task;

import org.phinix.example.server.core.TCPServerContext;
import org.phinix.example.server.core.thread.task.tasks.ExampleClientTask;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.task.TaskQueue;

public class ClientTaskExecutor extends AbstractTaskExecutor<TCPServerContext> {

    public ClientTaskExecutor(TaskQueue<TCPServerContext> taskQueue) {
        super(taskQueue);
    }

    @Override
    protected int initTasks() {
        registerTasks(new ExampleClientTask(1000));

        return getAmountRegisteredTasks();
    }
}
