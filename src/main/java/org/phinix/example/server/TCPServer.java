package org.phinix.example.server;

import org.phinix.example.server.service.task.TaskExecutor;
import org.phinix.example.server.thread.ClientHandler;
import org.phinix.lib.server.core.AbstractServer;

public class TCPServer extends AbstractServer {
    public TCPServer(int port, int maxUsers) {
        super(port, maxUsers, ClientHandler::new, TCPServerContext::new, new TaskExecutor());
    }
}