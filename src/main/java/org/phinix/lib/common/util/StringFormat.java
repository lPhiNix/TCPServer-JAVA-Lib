package org.phinix.lib.common.util;

public class StringFormat {
    public static String hidePassword(String password) {
        if (password == null || password.length() <= 1) {
            return password;
        }

        return password.charAt(0) + "*".repeat(password.length() - 1);
    }
}
