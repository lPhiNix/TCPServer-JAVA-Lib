package org.phinix.lib.server.service.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phinix.lib.common.model.User;
import org.phinix.lib.common.util.FileUtil;
import org.phinix.lib.server.service.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractUserManager<U extends User> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final Map<Class<?>, Function<String, Object>> castMap;

    protected abstract void initCastFieldType();

    protected void registerFieldType(Class<?> fieldType, Function<String, Object> cast) {
        logger.log(Level.DEBUG, "Registering field type: {}", fieldType.getName());
        castMap.put(fieldType, cast);
    }

    private Object cast(Class<?> fieldType, String value) {
        logger.log(Level.DEBUG, "Casting value '{}' to type {}", value, fieldType.getName());
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
        logger.log(Level.DEBUG, "Initializing AbstractUserManager with userType: {} and filePath: {}", userType.getName(), filePath);

        this.castMap = new ConcurrentHashMap<>();
        initCastFieldType();

        this.filePath = filePath;
        this.users = new ConcurrentHashMap<>();
        this.userType = userType;

        if (!FileUtil.fileExists(filePath)) {
            FileUtil.createFile(filePath);
        }

        loadUsersFromFile();
    }

    public boolean registerUser(U user) {
        logger.log(Level.DEBUG, "Registering user: {}", user.getUsername());
        if (isUserAlreadyRegistered(user)) {
            return false;
        }
        users.put(user.getUsername(), user);
        saveUsersToFile();
        return true;
    }

    private boolean isUserAlreadyRegistered(U user) {
        return users.containsKey(user.getUsername());
    }

    public U authenticate(String username, String password) {
        logger.log(Level.DEBUG, "Authenticating user: {}", username);
        U user = users.get(username);
        return isAuthenticated(user, password) ? user : null;
    }

    private boolean isAuthenticated(U user, String password) {
        return user != null && user.getPassword().equals(password);
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
        FileUtil.readFile(filePath, this::processFileLine);
    }

    private void processFileLine(String line) {
        U user = stringToUser(line);
        if (user != null) {
            logger.log(Level.DEBUG, "Loaded user: {}", user.getUsername());
            users.put(user.getUsername(), user);
        }
    }

    private void saveUsersToFile() {
        logger.log(Level.DEBUG, "Saving users to file: {}", filePath);
        FileUtil.writeFile(filePath, users.values(), this::userToString);
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

            return convertStringToUser(parts);
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error converting string to user: ", e);
        }
        return null;
    }

    private U convertStringToUser(String[] parts) throws Exception {
        U user = userType.getDeclaredConstructor().newInstance();
        Field[] fields = getAllFields(userType);

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            logger.log(Level.DEBUG, "Setting field {} with value: {}", fields[i].getName(), parts[i]);

            Object value = cast(fields[i].getType(), parts[i]);
            setFieldValue(user, fields[i], value);
        }
        return user;
    }

    private void setFieldValue(U user, Field field, Object value) throws IllegalAccessException {
        if (value != null) {
            field.set(user, value);
        } else {
            logger.log(Level.WARN, "Failed to cast value for field {}", field.getName());
        }
    }

    private String userToString(U user) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = getAllFields(userType);

        logger.log(Level.DEBUG, "Converting user to string. User class: {}", user.getClass().getName());

        try {
            appendFieldsToStringBuilder(user, fields, stringBuilder);
        } catch (IllegalAccessException e) {
            logger.log(Level.ERROR, "Error converting user to string: ", e);
        }

        return stringBuilder.toString();
    }

    private void appendFieldsToStringBuilder(U user, Field[] fields, StringBuilder stringBuilder) throws IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(user);
            logger.log(Level.DEBUG, "Field: {} - Value: {}", field.getName(), value);
            stringBuilder.append(value).append(",");
        }

        if (!stringBuilder.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        logger.log(Level.DEBUG, "Final string representation of the user: {}", stringBuilder);
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