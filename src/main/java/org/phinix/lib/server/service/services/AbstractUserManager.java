package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.phinix.lib.common.model.User;
import org.phinix.lib.server.service.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractUserManager<U extends User> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final Map<Class<?>, Function<String, Object>> castMap;

    protected abstract void initCastFieldType();

    protected void registerFieldType(Class<?> fieldType, Function<String, Object> cast) {
        logger.log(Level.DEBUG, "Registering field type: " + fieldType.getName());
        castMap.put(fieldType, cast);
    }

    public Object cast(Class<?> fieldType, String value) {
        logger.log(Level.DEBUG, "Casting value '" + value + "' to type " + fieldType.getName());
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
        logger.log(Level.DEBUG, "Initializing AbstractUserManager with userType: " + userType.getName() + " and filePath: " + filePath);

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
        logger.log(Level.DEBUG, "Registering user: {}", user.getUsername());
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        saveUsersToFile();
        return true;
    }

    public U authenticate(String username, String password) {
        logger.log(Level.DEBUG, "Authenticating user: {}", username);
        U user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void updateUser(U newUser) {
        logger.log(Level.DEBUG, "Updating user: {}", newUser.getUsername());
        users.put(newUser.getUsername(), newUser);
        saveUsersToFile();
    }

    public List<String> getRunningUsers() {
        return Collections.list(users.keys());
    }

    private void loadUsersFromFile() {
        logger.log(Level.DEBUG, "Loading users from file: {}", filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                U user = stringToUser(line);
                if (user != null) {
                    logger.log(Level.DEBUG, "Loaded user: {}", user.getUsername());
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
        logger.log(Level.DEBUG, "Checking if user file exists: {}", filePath);

        if (!userFile.exists()) {
            createUserFile(userFile);
            return true;
        }

        return false;
    }

    private void createUserFile(File userFile) {
        logger.log(Level.DEBUG, "Creating user file: {}", filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            writer.write("");
        } catch (IOException e) {
            logger.log(Level.FATAL, "Error creating user file: ", e);
        }
    }

    private void saveUsersToFile() {
        logger.log(Level.DEBUG, "Saving users to file: {}", filePath);
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
        if (line.isEmpty()) return null;

        logger.log(Level.DEBUG, "Converting string to user: {}", line);
        try {
            String[] parts = line.split(",");

            if (parts.length != 2) {
                logger.log(Level.WARN, "Incorrect number of fields in line: {}", line);
                return null;
            }

            U user = userType.getDeclaredConstructor().newInstance();
            Field[] fields = getAllFields(userType);

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                logger.log(Level.DEBUG, "Setting field {} with value: {}", new Object[]{fields[i].getName(), parts[i]});

                Object value = cast(fields[i].getType(), parts[i]);
                if (value != null) {
                    fields[i].set(user, value);
                } else {
                    logger.log(Level.WARN, "Failed to cast value for field {}", fields[i].getName());
                }
            }
            return user;
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error converting string to user: ", e);
        }
        return null;
    }


    private String userToString(U user) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = getAllFields(userType);

        for (Field field : fields) {
            System.out.println(field.getName());
        }

        logger.log(Level.DEBUG, "Converting user to string. User class: {}", user.getClass().getName());

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(user);
                logger.log(Level.DEBUG , "Field:  {} - Value: {}", new Object[]{field.getName(), value});

                stringBuilder.append(value).append(",");
            }

            logger.log(Level.DEBUG, "Intermediate string (before trimming last comma): {}", stringBuilder);

            if (!stringBuilder.isEmpty()) {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
            logger.log(Level.DEBUG, "Final string representation of the user: {}", stringBuilder);

        } catch (IllegalAccessException e) {
            logger.log(Level.ERROR, "Error converting user to string: ", e);
        }

        return stringBuilder.toString();
    }

    private Field[] getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();

        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));

            clazz = clazz.getSuperclass();
        }

        return allFields.toArray(new Field[0]);
    }
}
