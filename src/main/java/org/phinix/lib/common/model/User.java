package org.phinix.lib.common.model;

import org.phinix.lib.common.util.StringFormat;
import org.phinix.lib.server.service.services.AbstractUserManager;
import org.phinix.lib.server.service.AbstractServiceRegister;

import java.util.Objects;

/**
 * {@code User} abstract class representing a user account in the server.
 * <p>
 * This class has been implemented to work with the {@link AbstractUserManager} service instance.
 * It defines the basic structure for a user, including username and password, and methods for comparison and representation.
 *
 * @see AbstractUserManager
 * @see AbstractServiceRegister
 */
public abstract class User {
    protected String username; // The username of the user
    protected String password; // The password of the user

    /**
     * Constructs a new User with the specified username and password.
     * <p>
     * This constructor initializes the user with a unique username and a password.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Default constructor for User.
     * <p>
     * This constructor allows creating an empty user, which can be initialized later.
     */
    public User() {}

    /**
     * Returns the username of the user.
     * <p>
     * This method retrieves the username of the user, which is unique to each user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * <p>
     * This method sets a new username for the user.
     *
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of the user.
     * <p>
     * This method retrieves the password of the user. The password is generally stored securely.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * <p>
     * This method allows setting a new password for the user.
     *
     * @param password the password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if this user is equal to another object.
     * <p>
     * This method compares the current user object with another object to see if they are equal based on their username and password.
     *
     * @param o the object to compare with
     * @return {@code true} if this user is equal to the object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false; // Checks if the object is null or of a different class
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password); // Compares username and password
    }

    /**
     * Returns the hash code of this user.
     * <p>
     * This method generates a hash code for the user based on the username and password.
     *
     * @return the hash code of this user
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, password); // Creates a hash code using username and password
    }

    /**
     * Returns a string representation of this user.
     * <p>
     * This method returns a string that represents the user, hiding the actual password for security reasons.
     *
     * @return a string representation of this user
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + StringFormat.hidePassword(password) + '\'' + // Hides password for security
                '}';
    }
}
