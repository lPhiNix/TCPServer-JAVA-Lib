package org.phinix.example.common.model;

import org.phinix.lib.common.model.User;

public class Player extends User {
    public Player(String username, String password) {
        super(username, password);
    }

    public Player() {}

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }
}
