package org.phinix.example.server.service.task.tasks;

import org.phinix.example.server.thread.ClientHandler;
import org.phinix.lib.server.core.task.tasks.LoopTask;

public class ExampleTask extends LoopTask<ClientHandler> {
    public ExampleTask(long millis) {
        super(millis);
    }

    @Override
    public void process(ClientHandler manageable) {
       manageable.jijija();
    }
}
