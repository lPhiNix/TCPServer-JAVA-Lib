package org.phinix.example.server.service.services;

import org.phinix.lib.common.model.User;
import org.phinix.lib.server.service.services.AbstractUserManager;

public class UserManager extends AbstractUserManager<User> {

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

    public UserManager() {
        super(User.class, FILE_NAME);
    }
}
