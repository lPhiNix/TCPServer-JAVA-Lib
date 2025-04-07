package org.phinix.example.server.core.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.example.common.model.Player;
import org.phinix.example.server.core.MathGameServerContext;
import org.phinix.example.server.core.thread.task.ClientTaskExecutor;
import org.phinix.example.server.service.ServiceManager;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.service.services.CommandProcessor;
import org.phinix.lib.server.core.worker.AbstractWorker;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends AbstractWorker {
    private static final Logger logger = LogManager.getLogger();

    private final MathGameServerContext serverContext;
    private Player user = null;

    public ClientHandler(Socket socket, MathGameServerContext serverContext) throws IOException {
        super(socket, serverContext, new ServiceManager(), new ClientTaskExecutor(new TaskQueue<>()));

        this.serverContext = serverContext;
    }

    @Override @SuppressWarnings("unchecked")
    public void listen(String message) {
        CommandProcessor<ClientHandler> commandProcessor = getServiceRegister().getService(CommandProcessor.class);

        if (!commandProcessor.processCommand(message, this)) {
            getMessagesManager().sendMessage(getClientAddress() + ": " + message);
        }
    }

    @Override
    public String getClientAddress() {
        if (user != null) {
            return user.getUsername();
        }

        return super.getClientAddress();
    }

    @Override
    public MathGameServerContext getServerContext() {
        return serverContext;
    }

    public Player getCurrentUser() {
        return user;
    }

    public void setCurrentUser(Player user) {
        this.user = user;
    }
}