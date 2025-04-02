package org.phinix.example.server.core.task;

import org.phinix.example.server.core.task.tasks.ExampleTask;
import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;

public class GlobalTaskExecutor extends AbstractTaskExecutor<Context> {

    public GlobalTaskExecutor(TaskQueue<Context> taskQueue) {
        super(taskQueue);
    }

    @Override
    protected int initTasks() {
        registerTasks(new ExampleTask(1000));

        return getAmountRegisteredTasks();
    }
}
