package org.phinix.example.common.game;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.example.server.service.ServiceManager;
import org.phinix.lib.common.model.room.RoomImpl;
import java.util.List;

public class MathGameRoom extends RoomImpl {
    private final int maxPlayers;
    private final int rounds;
    private MathGame session;

    public MathGameRoom(String roomName, ClientHandler player, int maxPlayers, int rounds) {
        super(roomName, player, maxPlayers);

        this.maxPlayers = maxPlayers;
        this.rounds = rounds;
    }

    @Override
    public void startSession() {
        List<ClientHandler> players = castClientsList(clients, ClientHandler.class);

        session = new MathGame(players, (ServiceManager) clients.getFirst().getServiceRegister(), rounds);

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
