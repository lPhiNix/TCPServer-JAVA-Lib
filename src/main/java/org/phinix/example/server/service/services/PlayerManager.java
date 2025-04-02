package org.phinix.example.server.service.services;

import org.phinix.example.common.model.Player;
import org.phinix.lib.server.service.services.AbstractUserManager;

public class PlayerManager extends AbstractUserManager<Player> {

    private static final String FILE_NAME = "users.txt";

    @Override
    protected void initCastFieldType() {
        registerFieldType(int.class, Integer::parseInt);
        registerFieldType(long.class, Long::parseLong);
        registerFieldType(double.class, Double::parseDouble);
        registerFieldType(float.class, Float::parseFloat);
        registerFieldType(boolean.class, Boolean::parseBoolean);
        registerFieldType(String.class, String::valueOf);
    }

    public PlayerManager() {
        super(Player.class, FILE_NAME);
    }
}
