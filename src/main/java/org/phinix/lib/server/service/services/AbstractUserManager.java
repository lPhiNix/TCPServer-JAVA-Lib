package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.common.model.User;
import org.phinix.lib.server.service.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractUserManager<U extends User> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final Map<Class<?>, Function<String, Object>> castMap;

    protected abstract void initCastFieldType();

    protected void registerFieldType(Class<?> fieldType, Function<String, Object> cast) {
        castMap.put(fieldType, cast);
    }

    public Object cast(Class<?> fieldType, String value) {
        Function<String, Object> caster = castMap.get(fieldType);
        if (caster != null) {
            return caster.apply(value);
        } else {
            logger.log(Level.FATAL, "Error casting user field type: ");
            throw new IllegalArgumentException("Unsupported field type: " + fieldType);
        }
    }


    private final String filePath;
    private final ConcurrentHashMap<String, U> users;
    private final Class<U> userType;

    public AbstractUserManager(Class<U> userType, String filePath) {
        logger.log(Level.DEBUG, "Initializing");

        castMap = new HashMap<>();
        initCastFieldType();

        this.filePath = filePath;
        this.users = new ConcurrentHashMap<>();
        this.userType = userType;

        if (!findOutUserFile()) {
            logger.log(Level.INFO, "{} created successfully!", filePath);
        }

        loadUsersFromFile();
    }

    public boolean registerUser(U user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        saveUsersToFile();
        return true;
    }

    public U authenticate(String username, String password) {
        U user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void updateUser(U newUser) {
        users.put(newUser.getUsername(), newUser);
        saveUsersToFile();
    }

    private void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                U user = stringToUser(line);
                if (user != null) {
                    users.put(user.getUsername(), user);
                }
            }
            logger.log(Level.INFO, "Users loaded successfully from file!");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error loading users from file: ", e);
        }
    }

    private boolean findOutUserFile() {
        File userFile = new File(filePath);

        if (!userFile.exists()) {
            createUserFile(userFile);
            return true;
        }

        return false;
    }

    private void createUserFile(File userFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            writer.write("");
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error creating user file: ", e);
        }
    }

    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (U user : users.values()) {
                writer.write(userToString(user));
                writer.newLine();
            }
            logger.log(Level.INFO, "Users file saved successfully!");
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error saving users file: ", e);
        }
    }

    private U stringToUser(String line) {
        try {
            String[] parts = line.split(",");
            U user = userType.getDeclaredConstructor().newInstance();
            Field[] fields = user.getClass().getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                fields[i].set(user, cast(fields[i].getType(), parts[i]));
            }
            return user;
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error converting string to user: ", e);
        }
        return null;
    }

    private String userToString(U user) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = user.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                stringBuilder.append(field.get(user)).append(",");
            }
            stringBuilder.setLength(stringBuilder.length());
        } catch (IllegalAccessException e) {
            logger.log(Level.ERROR, "Error converting user to string: ", e);
        }
        return stringBuilder.toString();
    }
}