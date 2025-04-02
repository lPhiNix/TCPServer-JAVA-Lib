package org.phinix.example.server.core.thread.task.tasks;

import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.tasks.LoopTask;

public class ExampleClientTask extends LoopTask<Context> {

    public ExampleClientTask(long millis) {
        super(millis);
    }

    @Override
    public void process(Context serverContext) {
        System.out.println("nose");
    }
}
