package org.phinix.example.server.core.thread.task.tasks;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.core.task.tasks.LoopTask;

public class ExampleClientTask extends LoopTask<ClientHandler> {

    public ExampleClientTask(long millis) {
        super(millis);
    }

    @Override
    public void process(ClientHandler serverContext) {
        System.out.println("nose");
    }
}
