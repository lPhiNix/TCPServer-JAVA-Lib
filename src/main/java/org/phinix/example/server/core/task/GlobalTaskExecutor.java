package org.phinix.example.server.core.task;

import org.phinix.example.server.core.MathGameServer;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.core.task.AbstractTaskExecutor;

public class GlobalTaskExecutor extends AbstractTaskExecutor<MathGameServer> {

    public GlobalTaskExecutor(TaskQueue<MathGameServer> taskQueue) {
        super(taskQueue);
    }

    @Override
    protected int initTasks() {

        return getAmountRegisteredTasks();
    }
}
