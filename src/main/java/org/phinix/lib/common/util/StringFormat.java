package org.phinix.lib.common.util;

/**
 * {@code StringFormat} utility class for string formatting operations.
 */
public class StringFormat {

    /**
     * Hides all characters of a password except the first character.
     *
     * @param password the password to hide
     * @return the hidden password
     */
    public static String hidePassword(String password) {
        if (password == null || password.length() <= 1) {
            return password;
        }

        return password.charAt(0) + "*".repeat(password.length() - 1);
    }
}