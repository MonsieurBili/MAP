package Repository;

import Database.DatabaseConnection;
import Domain.Ducks.Duck;
import Domain.Ducks.DuckFactory;
import Domain.Ducks.TipRata;
import Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository implementation for Duck entities using PostgreSQL database
 */
public class DuckDatabaseRepository implements Repository<Long, Duck> {
    private final Validator<Duck> validator;
    private final DatabaseConnection dbConnection;
    
    public DuckDatabaseRepository(Validator<Duck> validator) {
        this.validator = validator;
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public Duck findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        String sql = "SELECT u.id, u.username, u.email, u.password, " +
                     "d.duck_type, d.speed, d.resistance " +
                     "FROM users u " +
                     "JOIN ducks d ON u.id = d.id " +
                     "WHERE u.id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractDuckFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding duck: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Iterable<Duck> findAll() {
        List<Duck> ducks = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email, u.password, " +
                     "d.duck_type, d.speed, d.resistance " +
                     "FROM users u " +
                     "JOIN ducks d ON u.id = d.id";
        
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                ducks.add(extractDuckFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all ducks: " + e.getMessage());
        }
        
        return ducks;
    }
    
    @Override
    public Duck save(Duck entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null");
        }
        validator.validate(entity);
        
        // Check if duck already exists
        if (findOne(entity.getId()) != null) {
            return entity;
        }
        
        String userSql = "INSERT INTO users (id, username, email, password, user_type) VALUES (?, ?, ?, ?, 'DUCK')";
        String duckSql = "INSERT INTO ducks (id, duck_type, speed, resistance) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement userStmt = connection.prepareStatement(userSql);
                 PreparedStatement duckStmt = connection.prepareStatement(duckSql)) {
                
                // Insert into users table
                userStmt.setLong(1, entity.getId());
                userStmt.setString(2, entity.getUsername());
                userStmt.setString(3, entity.getEmail());
                userStmt.setString(4, entity.getPassword());
                userStmt.executeUpdate();
                
                // Insert into ducks table
                duckStmt.setLong(1, entity.getId());
                duckStmt.setString(2, entity.getTipRata().name());
                duckStmt.setDouble(3, entity.getViteza());
                duckStmt.setDouble(4, entity.getRezistenta());
                duckStmt.executeUpdate();
                
                connection.commit();
                return null;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error saving duck: " + e.getMessage());
            return entity;
        }
    }
    
    @Override
    public Duck delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        Duck duck = findOne(id);
        if (duck == null) {
            return null;
        }
        
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            statement.executeUpdate();
            return duck;
        } catch (SQLException e) {
            System.err.println("Error deleting duck: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Duck update(Duck entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        validator.validate(entity);
        
        if (findOne(entity.getId()) == null) {
            return entity;
        }
        
        String userSql = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
        String duckSql = "UPDATE ducks SET duck_type = ?, speed = ?, resistance = ? WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement userStmt = connection.prepareStatement(userSql);
                 PreparedStatement duckStmt = connection.prepareStatement(duckSql)) {
                
                // Update users table
                userStmt.setString(1, entity.getUsername());
                userStmt.setString(2, entity.getEmail());
                userStmt.setString(3, entity.getPassword());
                userStmt.setLong(4, entity.getId());
                userStmt.executeUpdate();
                
                // Update ducks table
                duckStmt.setString(1, entity.getTipRata().name());
                duckStmt.setDouble(2, entity.getViteza());
                duckStmt.setDouble(3, entity.getRezistenta());
                duckStmt.setLong(4, entity.getId());
                duckStmt.executeUpdate();
                
                connection.commit();
                return null;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error updating duck: " + e.getMessage());
            return entity;
        }
    }
    
    /**
     * Extract Duck object from ResultSet
     */
    private Duck extractDuckFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String duckTypeStr = rs.getString("duck_type");
        double speed = rs.getDouble("speed");
        double resistance = rs.getDouble("resistance");
        
        TipRata duckType = TipRata.valueOf(duckTypeStr);
        
        DuckFactory duckFactory = DuckFactory.getInstance();
        duckFactory.setData(username, email, password, duckType, speed, resistance);
        Duck duck = duckFactory.createUser();
        duck.setId(id);
        
        return duck;
    }
}
