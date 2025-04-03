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
                server -> new TCPServerContext((TCPServer) server),
                (socket, serverContext) ->
                        new ClientHandler(socket, (TCPServerContext) serverContext),
                new GlobalTaskExecutor(new TaskQueue<>())
        );
    }

    public void nose() {
        System.out.println("nose");
    }
}