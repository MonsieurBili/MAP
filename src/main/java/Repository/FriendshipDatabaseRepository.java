package Repository;

import Database.DatabaseConnection;
import Domain.Friendship;
import Domain.User;
import Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository implementation for Friendship entities using PostgreSQL database
 */
public class FriendshipDatabaseRepository implements Repository<Long, Friendship> {
    private final Validator<Friendship> validator;
    private final DatabaseConnection dbConnection;
    private final Repository<Long, ?> userRepository;
    
    public FriendshipDatabaseRepository(Validator<Friendship> validator, Repository<Long, ?> userRepository) {
        this.validator = validator;
        this.dbConnection = DatabaseConnection.getInstance();
        this.userRepository = userRepository;
    }
    
    @Override
    public Friendship findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        String sql = "SELECT id, user1_id, user2_id FROM friendships WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractFriendshipFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding friendship: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT id, user1_id, user2_id FROM friendships";
        
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                friendships.add(extractFriendshipFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all friendships: " + e.getMessage());
        }
        
        return friendships;
    }
    
    @Override
    public Friendship save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null");
        }
        validator.validate(entity);
        
        // Ensure consistent ordering (user1_id < user2_id)
        Long user1Id = entity.getUser1().getId();
        Long user2Id = entity.getUser2().getId();
        
        if (user1Id > user2Id) {
            Long temp = user1Id;
            user1Id = user2Id;
            user2Id = temp;
        }
        
        String sql = "INSERT INTO friendships (id, user1_id, user2_id) VALUES (?, ?, ?)";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, entity.getId());
            statement.setLong(2, user1Id);
            statement.setLong(3, user2Id);
            statement.executeUpdate();
            return null;
        } catch (SQLException e) {
            System.err.println("Error saving friendship: " + e.getMessage());
            return entity;
        }
    }
    
    @Override
    public Friendship delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        Friendship friendship = findOne(id);
        if (friendship == null) {
            return null;
        }
        
        String sql = "DELETE FROM friendships WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            statement.executeUpdate();
            return friendship;
        } catch (SQLException e) {
            System.err.println("Error deleting friendship: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Friendship update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        validator.validate(entity);
        
        if (findOne(entity.getId()) == null) {
            return entity;
        }
        
        // Ensure consistent ordering
        Long user1Id = entity.getUser1().getId();
        Long user2Id = entity.getUser2().getId();
        
        if (user1Id > user2Id) {
            Long temp = user1Id;
            user1Id = user2Id;
            user2Id = temp;
        }
        
        String sql = "UPDATE friendships SET user1_id = ?, user2_id = ? WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, user1Id);
            statement.setLong(2, user2Id);
            statement.setLong(3, entity.getId());
            statement.executeUpdate();
            return null;
        } catch (SQLException e) {
            System.err.println("Error updating friendship: " + e.getMessage());
            return entity;
        }
    }
    
    /**
     * Extract Friendship object from ResultSet
     */
    private Friendship extractFriendshipFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long user1Id = rs.getLong("user1_id");
        Long user2Id = rs.getLong("user2_id");
        
        // Get user objects - for now we'll create basic User references
        // In a real implementation, you might want to fetch full user objects
        User user1 = (User) userRepository.findOne(user1Id);
        User user2 = (User) userRepository.findOne(user2Id);
        
        if (user1 == null || user2 == null) {
            throw new SQLException("Referenced users not found");
        }
        
        Friendship friendship = new Friendship(user1, user2);
        friendship.setId(id);
        
        return friendship;
    }
}
