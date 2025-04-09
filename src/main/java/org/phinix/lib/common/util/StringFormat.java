package org.phinix.lib.common.util;

/**
 * {@code StringFormat} utility class for string formatting operations.
 * This class provides utility methods for formatting strings, such as hiding passwords.
 */
public class StringFormat {

    /**
     * Hides all characters of a password except the first character.
     * <p>
     * This method ensures that sensitive information, like passwords, is partially hidden
     * to improve security. It leaves the first character visible and replaces the rest with asterisks (*).
     *
     * @param password the password to hide
     * @return the hidden password with only the first character visible, or the original password if it's too short or null
     */
    public static String hidePassword(String password) {
        // Checks if the password is null or has only one character, in which case it returns it as is.
        if (password == null || password.length() <= 1) {
            return password;
        }

        // Returns the password with the first character visible and the rest hidden as asterisks (*).
        return password.charAt(0) + "*".repeat(password.length() - 1);
    }
}
