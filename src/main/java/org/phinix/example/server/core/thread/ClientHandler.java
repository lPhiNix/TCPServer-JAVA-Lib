package org.phinix.example.server.core.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.example.common.game.MathGameRoom;
import org.phinix.example.common.model.Player;
import org.phinix.example.server.core.MathGameServerContext;
import org.phinix.example.server.core.thread.task.ClientTaskExecutor;
import org.phinix.example.server.service.ServiceManager;
import org.phinix.lib.common.model.room.RoomImpl;
import org.phinix.lib.server.core.task.TaskQueue;
import org.phinix.lib.server.service.services.CommandProcessor;
import org.phinix.lib.server.core.worker.AbstractWorker;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends AbstractWorker {
    private static final Logger logger = LogManager.getLogger();

    private final MathGameServerContext serverContext;
    private final ServiceManager serviceRegister;
    private Player user = null;

    public ClientHandler(Socket socket, MathGameServerContext serverContext, ServiceManager serviceManager) throws IOException {
        super(socket, serverContext, serviceManager, new ClientTaskExecutor(new TaskQueue<>()));

        this.serverContext = serverContext;
        this.serviceRegister = serviceManager;
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

    @Override
    public ServiceManager getServiceRegister() {
        return serviceRegister;
    }

    @Override
    public MathGameRoom getCurrentRoom() {
        return (MathGameRoom) super.getCurrentRoom();
    }

    @Override
    public void setCurrentRoom(RoomImpl roomImpl) {
        super.setCurrentRoom(roomImpl);
    }

    public Player getCurrentUser() {
        return user;
    }

    public void setCurrentUser(Player user) {
        this.user = user;
    }
}