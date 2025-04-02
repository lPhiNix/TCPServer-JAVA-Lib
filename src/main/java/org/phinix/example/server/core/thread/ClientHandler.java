package org.phinix.example.server.core.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.example.server.core.thread.task.ClientTaskExecutor;
import org.phinix.example.server.service.ServiceManager;
import org.phinix.lib.server.context.Context;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.service.services.CommandProcessor;
import org.phinix.lib.server.core.worker.AbstractWorker;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends AbstractWorker {
    private static final Logger logger = LogManager.getLogger();

    public ClientHandler(Socket socket, Context serverContext) throws IOException {
        super(socket, serverContext, new ServiceManager(), new ClientTaskExecutor(new TaskQueue<>()));
    }

    @Override @SuppressWarnings("unchecked")
    public void listen(String message) {
        CommandProcessor<ClientHandler> commandProcessor = getServiceRegister().getService(CommandProcessor.class);

        if (!commandProcessor.processCommand(message, this)) {
            getMessagesManager().sendMessage(socket.getInetAddress() + ": " + message);
        }
    }

    public void clientHandler() {
        System.out.println("clientHandler");
    }
}