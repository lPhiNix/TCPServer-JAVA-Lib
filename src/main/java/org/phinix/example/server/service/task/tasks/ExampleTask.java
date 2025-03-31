package org.phinix.example.server.service.task.tasks;

import org.phinix.lib.server.core.task.tasks.LoopTask;

public class ExampleTask extends LoopTask {
    public ExampleTask(long millis) {
        super(millis);
    }

    @Override
    protected void task() {
        System.out.println("no");
    }
}
