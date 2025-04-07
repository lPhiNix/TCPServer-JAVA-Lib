package org.phinix.example.common.game;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.common.model.AbstractRoom;

public class MathGameRoom extends AbstractRoom {
    private static final int MAX_USERS = 2;

    private MathGame session;

    public MathGameRoom(String roomName, ClientHandler player) {
        super(roomName, player);

        maxUsers = MAX_USERS;
    }

    @Override
    public void startSession() {
        //session = new MathGame(clients, );

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
}
