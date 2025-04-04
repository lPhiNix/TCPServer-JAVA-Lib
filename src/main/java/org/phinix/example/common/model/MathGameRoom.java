package org.phinix.example.common.model;

import org.phinix.example.server.core.thread.ClientHandler;
import org.phinix.lib.common.model.AbstractRoom;

public class MathGameRoom extends AbstractRoom {
    private static final int MAX_USERS = 2;
    public MathGameRoom(String roomName, ClientHandler player) {
        super(roomName, player);

        maxUsers = MAX_USERS;
    }


}
