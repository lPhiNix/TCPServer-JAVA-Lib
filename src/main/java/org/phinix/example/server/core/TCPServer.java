package org.phinix.example.server.core;

import org.phinix.example.server.core.task.GlobalTaskExecutor;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.task.TaskQueue;

public class TCPServer extends AbstractServer {
    public TCPServer(int port, int maxUsers) {
        super(
                port,
                maxUsers,
                ClientHandler::new,
                server ->
                        new TCPServerContext((TCPServer) server),
                new GlobalTaskExecutor(new TaskQueue<>()));
    }

    public void nose() {
        System.out.println("nose");
    }
}