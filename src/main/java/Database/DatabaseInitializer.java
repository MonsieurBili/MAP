package Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the database schema by executing SQL scripts
 */
public class DatabaseInitializer {
    
    /**
     * Initialize database schema from SQL file
     * @param schemaFilePath Path to the SQL schema file
     * @throws SQLException if database operations fail
     */
    public static void initializeSchema(String schemaFilePath) throws SQLException {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        Connection connection = dbConnection.getConnection();
        
        try {
            String sqlScript = readSqlFile(schemaFilePath);
            executeSqlScript(connection, sqlScript);
            System.out.println("Database schema initialized successfully.");
        } catch (IOException e) {
            throw new SQLException("Failed to read schema file: " + schemaFilePath, e);
        }
    }
    
    /**
     * Read SQL file content
     * @param filePath Path to SQL file
     * @return SQL script as string
     * @throws IOException if file cannot be read
     */
    private static String readSqlFile(String filePath) throws IOException {
        StringBuilder sqlScript = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }
        }
        return sqlScript.toString();
    }
    
    /**
     * Execute SQL script
     * @param connection Database connection
     * @param sqlScript SQL script to execute
     * @throws SQLException if execution fails
     */
    private static void executeSqlScript(Connection connection, String sqlScript) throws SQLException {
        // Split by semicolon but keep statements that might span multiple lines
        String[] statements = sqlScript.split(";");
        
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                // Skip empty statements and comments
                if (!trimmedSql.isEmpty() && !trimmedSql.startsWith("--")) {
                    statement.execute(trimmedSql);
                }
            }
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            Connection connection = dbConnection.getConnection();
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
