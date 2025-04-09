package org.phinix.lib.common.model;

import org.phinix.lib.common.util.StringFormat;
import org.phinix.lib.server.service.services.AbstractUserManager;
import org.phinix.lib.server.service.AbstractServiceRegister;

import java.util.Objects;

/**
 * {@code User} abstract class representing a user as account in server.
 * <p>
 * This class has been implemented for jobbing with {@link AbstractUserManager} service instance.
 *
 * @see AbstractUserManager
 * @see AbstractServiceRegister
 */
public abstract class User {
    protected String username; // The username of the user
    protected String password; // The password of the user

    /**
     * Constructs a new User with the specified username and password.
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
     */
    public User() {}

    /**
     * Returns the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if this user is equal to another object.
     *
     * @param o the object to compare with
     * @return {@code true} if this user is equal to the object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    /**
     * Returns the hash code of this user.
     *
     * @return the hash code of this user
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    /**
     * Returns a string representation of this user.
     *
     * @return a string representation of this user
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + StringFormat.hidePassword(password) + '\'' +
                '}';
    }
}