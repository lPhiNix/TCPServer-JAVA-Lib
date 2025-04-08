package org.phinix.example.common.game;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.common.model.room.RoomImpl;

public class MathGameRoom extends RoomImpl {
    private final int maxPlayers;
    private MathGame session;

    public MathGameRoom(String roomName, ClientHandler player, int maxPlayers) {
        super(roomName, player, maxPlayers);

        this.maxPlayers = maxPlayers;
    }

    @Override
    public void startSession() {
       // session = new MathGame(clients, clients.get(0).get);
        System.out.println("nose");
        try {
            session.start();
        } catch (Exception e) {
            e.printStackTrace();
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
