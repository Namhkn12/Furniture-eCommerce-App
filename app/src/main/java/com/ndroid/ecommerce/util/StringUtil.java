package com.ndroid.ecommerce.util;

public class StringUtil {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Check length (e.g., 2 to 50 characters)
        if (name.length() < 2 || name.length() > 50) {
            return false;
        }
        return true;
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        // Check length (e.g., 3 to 20 characters)
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }

        // Check for allowed characters (alphanumeric and underscore only)
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return false;
        }

        // Check for no leading/trailing underscores
        if (username.startsWith("_") || username.endsWith("_")) {
            return false;
        }

        // Check for no consecutive underscores
        if (username.contains("__")) {
            return false;
        }

        return true;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.matches("^[a-zA-Z0-9@#$%^&+=!._-]{6,20}$");
    }
}
