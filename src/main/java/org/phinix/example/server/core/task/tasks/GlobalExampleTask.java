package org.phinix.example.server.core.task.tasks;

import org.phinix.example.server.core.TCPServer;
import org.phinix.lib.server.core.task.tasks.LoopTask;

public class GlobalExampleTask extends LoopTask<TCPServer> {
    public GlobalExampleTask(long millis) {
        super(millis);
    }

    @Override
    public void process(TCPServer server) {
       System.out.println("no.");
    }
}
