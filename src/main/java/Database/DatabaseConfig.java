package Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Database configuration class that loads database connection properties
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Properties properties;
    
    private DatabaseConfig() {
        properties = new Properties();
        loadDefaultProperties();
    }
    
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Load default database configuration properties
     */
    private void loadDefaultProperties() {
        // Default configuration
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/ducknetwork");
        properties.setProperty("db.username", "postgres");
        properties.setProperty("db.password", "postgres");
        
        // Try to load from external configuration file if it exists
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            // If file doesn't exist, use default properties
            System.out.println("Using default database configuration. Create 'db.properties' to customize.");
        }
    }
    
    public String getUrl() {
        return properties.getProperty("db.url");
    }
    
    public String getUsername() {
        return properties.getProperty("db.username");
    }
    
    public String getPassword() {
        return properties.getProperty("db.password");
    }
}
