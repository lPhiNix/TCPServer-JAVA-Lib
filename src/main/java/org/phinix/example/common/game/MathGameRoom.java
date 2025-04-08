package org.phinix.example.common.game;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.example.server.service.ServiceManager;
import org.phinix.lib.common.model.room.RoomImpl;
import org.phinix.lib.server.core.worker.Worker;

import java.util.ArrayList;
import java.util.List;

public class MathGameRoom extends RoomImpl {
    private final int maxPlayers;
    private MathGame session;

    public MathGameRoom(String roomName, ClientHandler player, int maxPlayers) {
        super(roomName, player, maxPlayers);

        this.maxPlayers = maxPlayers;
    }

    @Override
    public void startSession() {
        List<ClientHandler> players = castClientsList(clients, ClientHandler.class);

        session = new MathGame(players, (ServiceManager) clients.getFirst().getServiceRegister());

        try {
            session.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void tryGuessRoot(String mathExpression, ClientHandler client) {
        if (session != null) {
            session.tryGuessRoot(mathExpression, client);
        }
    }

    @Override
    public MathGame getSession() {
        return session;
    }

    public int getMaxUsers() {
        return maxPlayers;
    }
}
