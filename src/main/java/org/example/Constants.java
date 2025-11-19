package org.example;

/**
 * Application-wide constants for file paths, validation rules, and other configuration values.
 */
public final class Constants {
    
    // File paths
    public static final String PERSONS_FILE_PATH = "src/main/resources/persoane.txt";
    public static final String DUCKS_FILE_PATH = "src/main/resources/rate.txt";
    public static final String FRIENDSHIP_FILE_PATH = "src/main/resources/friendship.txt";
    public static final String EVENTS_FILE_PATH = "event.txt";
    
    // Validation constants
    public static final int MIN_USERNAME_LENGTH = 2;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_NAME_LENGTH = 4;
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class cannot be instantiated");
    }
}
