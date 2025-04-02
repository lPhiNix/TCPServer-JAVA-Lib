package org.phinix.example.server.core.thread.task;

import org.phinix.example.server.core.thread.task.tasks.ExampleClientTask;
import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;
import org.phinix.lib.server.core.task.TaskQueue;

public class ClientTaskExecutor extends AbstractTaskExecutor<Context> {

    public ClientTaskExecutor(TaskQueue<Context> taskQueue) {
        super(taskQueue);
    }

    @Override
    protected int initTasks() {
        registerTasks(new ExampleClientTask(1000));

        return getAmountRegisteredTasks();
    }
}
