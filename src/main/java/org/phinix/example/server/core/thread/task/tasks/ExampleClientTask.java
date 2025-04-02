package org.phinix.example.server.core.thread.task.tasks;

import org.phinix.example.server.core.TCPServerContext;
import org.phinix.lib.server.core.task.tasks.LoopTask;

public class ExampleClientTask extends LoopTask<TCPServerContext> {

    public ExampleClientTask(long millis) {
        super(millis);
    }

    @Override
    public void process(TCPServerContext serverContext) {
        System.out.println("nose");
    }
}
