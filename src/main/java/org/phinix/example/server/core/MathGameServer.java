package org.phinix.example.server.core;

import org.phinix.example.server.core.task.GlobalTaskExecutor;
import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.server.core.AbstractServer;
import org.phinix.lib.server.core.task.TaskQueue;

public class MathGameServer extends AbstractServer {
    public MathGameServer(int port, int maxUsers) {
        super(
                port,
                maxUsers,
                server -> new MathGameServerContext((MathGameServer) server),
                (socket, serverContext) ->
                        new ClientHandler(socket, (MathGameServerContext) serverContext),
                new GlobalTaskExecutor(new TaskQueue<>())
        );
    }

    public void nose() {
        System.out.println("nose");
    }
}