package org.phinix.example.server.core.task.tasks;

import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.tasks.LoopTask;

public class ExampleTask extends LoopTask<Context> {
    public ExampleTask(long millis) {
        super(millis);
    }

    @Override
    public void process(Context context) {
       System.out.println("no.");
    }
}
