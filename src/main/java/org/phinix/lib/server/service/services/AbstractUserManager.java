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

/**
 * {@code AbstractUserManager} is an abstract class that provides core functionalities
 * for managing user accounts in a server environment. This includes registering users,
 * authenticating them, updating user details, and persisting user data to a file.
 * <p>
 * The class uses a {@link ConcurrentHashMap} to store user data, ensuring thread-safe
 * operations in a multithreaded environment. It also supports dynamic field type casting
 * for user attributes via a customizable casting map.
 * <p>
 * Use example:
 * <pre>{@code
 * public class MyUserManager extends AbstractUserManager<MyUser> {
 *
 *     private static final String FILE_NAME = "users.txt";
 *
 *     @Override
 *     protected void initCastFieldType() {
 *         registerFieldType(int.class, Integer::parseInt);
 *         registerFieldType(long.class, Long::parseLong);
 *         registerFieldType(double.class, Double::parseDouble);
 *         registerFieldType(float.class, Float::parseFloat);
 *         registerFieldType(boolean.class, Boolean::parseBoolean);
 *         registerFieldType(String.class, String::valueOf);
 *     }
 *
 *     public MyUserManager() {
 *         super(MyUser.class, FILE_NAME);
 *     }
 * }
 * }
 *
 * @param <U> the type of user being managed, extending {@link User}
 * @see User
 * @see Service
 */
public abstract class AbstractUserManager<U extends User> implements Service {
    private static final Logger logger = LogManager.getLogger();

    private final Map<Class<?>, Function<String, Object>> castMap; // Maps field types to their casting functions
    private final String filePath; // Path to the file used for persisting user data
    private final ConcurrentHashMap<String, U> users; // Thread-safe map storing users by their usernames
    private final Class<U> userType; // The type of user being managed

    /**
     * Constructs an {@code AbstractUserManager} for the specified user type and file path.
     *
     * @param userType the class type of the user
     * @param filePath the path to the file used for persisting user data
     */
    public AbstractUserManager(Class<U> userType, String filePath) {
        logger.log(Level.DEBUG, "Initializing AbstractUserManager with userType: {} and filePath: {}", userType.getName(), filePath);

        this.castMap = new ConcurrentHashMap<>();
        initCastFieldType(); // Initialize field type casting logic

        this.filePath = filePath;
        this.users = new ConcurrentHashMap<>();
        this.userType = userType;

        // Ensure the file exists or create a new one
        if (!FileUtil.fileExists(filePath)) {
            FileUtil.createFile(filePath);
            logger.log(Level.DEBUG, "Created new file at: {}", filePath); // Log creation of new file
        }

        // Load users from the file
        loadUsersFromFile();
    }

    /**
     * Initializes the casting map for field types.
     * Subclasses must implement this method to define specific casting logic.
     */
    protected abstract void initCastFieldType();

    /**
     * Registers a casting function for a specific field type.
     *
     * @param fieldType the class type of the field
     * @param cast      the function to cast a string value to the field type
     */
    protected void registerFieldType(Class<?> fieldType, Function<String, Object> cast) {
        logger.log(Level.DEBUG, "Registering field type: {}", fieldType.getName());
        castMap.put(fieldType, cast);
    }

    /**
     * Casts a string value to the specified field type using the casting map.
     *
     * @param fieldType the class type of the field
     * @param value     the string value to cast
     * @return the casted object
     * @throws IllegalArgumentException if the field type is unsupported
     */
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

    /**
     * Registers a new user.
     * If the username is already registered, the registration fails.
     *
     * @param user the user to register
     * @return {@code true} if the user was successfully registered, {@code false} otherwise
     */
    public boolean registerUser(U user) {
        logger.log(Level.DEBUG, "Registering user: {}", user.getUsername());
        if (isUserAlreadyRegistered(user)) {
            logger.log(Level.INFO, "User '{}' is already registered", user.getUsername()); // Log if user is already registered
            return false;
        }
        users.put(user.getUsername(), user);
        saveUsersToFile(); // Persist the new user data to the file
        logger.log(Level.INFO, "User '{}' registered successfully", user.getUsername()); // Log successful registration
        return true;
    }

    /**
     * Checks if a user is already registered based on their username.
     *
     * @param user the user to check
     * @return {@code true} if the user is already registered, {@code false} otherwise
     */
    private boolean isUserAlreadyRegistered(U user) {
        return users.containsKey(user.getUsername());
    }

    /**
     * Authenticates a user based on their username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the authenticated user, or {@code null} if authentication fails
     */
    public U authenticate(String username, String password) {
        logger.log(Level.DEBUG, "Authenticating user: {}", username);
        U user = users.get(username);
        if (isAuthenticated(user, password)) {
            logger.log(Level.INFO, "User '{}' authenticated successfully", username); // Log successful authentication
            return user;
        }
        logger.log(Level.INFO, "Authentication failed for user '{}'", username); // Log authentication failure
        return null;
    }

    /**
     * Checks if the provided password matches the user's password.
     *
     * @param user     the user to authenticate
     * @param password the password to verify
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    private boolean isAuthenticated(U user, String password) {
        return user != null && user.getPassword().equals(password);
    }

    /**
     * Updates the details of an existing user and persists the changes.
     *
     * @param newUser the updated user details
     */
    public void updateUser(U newUser) {
        logger.log(Level.DEBUG, "Updating user: {}", newUser.getUsername());
        users.put(newUser.getUsername(), newUser);
        saveUsersToFile(); // Persist the updated user data to the file
        logger.log(Level.INFO, "User '{}' updated successfully", newUser.getUsername()); // Log successful update
    }

    /**
     * Returns a list of all registered usernames.
     *
     * @return a list of registered usernames
     */
    public List<String> getRunningUsers() {
        return Collections.list(users.keys());
    }

    /**
     * Loads user data from the file and populates the user map.
     */
    private void loadUsersFromFile() {
        logger.log(Level.DEBUG, "Loading users from file: {}", filePath);
        FileUtil.readFile(filePath, this::processFileLine);
    }

    /**
     * Processes a single line from the user data file and adds the user to the map.
     *
     * @param line the line from the file
     */
    private void processFileLine(String line) {
        U user = stringToUser(line);
        if (user != null) {
            logger.log(Level.DEBUG, "Loaded user: {}", user.getUsername());
            users.put(user.getUsername(), user);
        }
    }

    /**
     * Saves all user data to the file by serializing each user to a string.
     */
    private void saveUsersToFile() {
        logger.log(Level.DEBUG, "Saving users to file: {}", filePath);
        FileUtil.writeFile(filePath, users.values(), this::userToString);
    }

    /**
     * Converts a string from the file into a user object.
     *
     * @param line the string representation of the user
     * @return the user object, or {@code null} if conversion fails
     */
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

    /**
     * Converts an array of string values into a user object by mapping them to fields.
     *
     * @param parts the string values representing user fields
     * @return the user object
     * @throws Exception if an error occurs during conversion
     */
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

    /**
     * Sets the value of a field in a user object.
     *
     * @param user  the user object
     * @param field the field to set
     * @param value the value to assign
     * @throws IllegalAccessException if the field is inaccessible
     */
    private void setFieldValue(U user, Field field, Object value) throws IllegalAccessException {
        if (value != null) {
            field.set(user, value);
        } else {
            logger.log(Level.WARN, "Failed to cast value for field {}", field.getName());
        }
    }

    /**
     * Converts a user object into a string representation for file storage.
     *
     * @param user the user to convert
     * @return the string representation of the user
     */
    private String userToString(U user) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = getAllFields(userType);

        logger.log(Level.DEBUG, "Converting user to string. User class: {}", user.getClass().getName());

        try {
            appendFieldsToStringBuilder(user, fields, stringBuilder);
        } catch (IllegalAccessException e) {
            logger.log(Level.ERROR, "Error converting user fields to string: ", e);
        }

        return stringBuilder.toString();
    }

    /**
     * Appends the field values of a user object to a StringBuilder.
     *
     * @param user          the user object
     * @param fields        the fields of the user
     * @param stringBuilder the StringBuilder to append to
     * @throws IllegalAccessException if a field is inaccessible
     */
    private void appendFieldsToStringBuilder(U user, Field[] fields, StringBuilder stringBuilder) throws IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(user);
            if (value != null) {
                stringBuilder.append(value.toString()).append(",");
            } else {
                logger.log(Level.WARN, "Null value found for field {}", field.getName());
            }
        }
        stringBuilder.setLength(stringBuilder.length() - 1); // Remove the last comma
    }

    /**
     * Retrieves all declared fields of the given user class, including those from superclasses.
     *
     * @param clazz the class of the user
     * @return an array of fields declared in the class
     */
    private Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
}
