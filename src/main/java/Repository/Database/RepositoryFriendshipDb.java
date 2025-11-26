package Repository.Database;

import Domain.Friendship;
import Domain.User;
import Repository.Repository;
import Validators.FriendshipValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryFriendshipDb implements Repository<Long, Friendship> {
    private final FriendshipValidator validator;
    private final Repository<Long, ? extends User> userRepository;

    public RepositoryFriendshipDb(FriendshipValidator validator, Repository<Long, ? extends User> userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }

    @Override
    public Friendship findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT id, user1_id, user2_id FROM friendships WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        final String sql = "SELECT id, user1_id, user2_id FROM friendships";
        List<Friendship> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                results.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        final String sql = "INSERT INTO friendships (user1_id, user2_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, entity.getUser1().getId());
            ps.setLong(2, entity.getUser2().getId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Friendship delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        Friendship existing = findOne(id);
        if (existing == null) return null;

        final String sql = "DELETE FROM friendships WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            return existing;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Friendship update(Friendship entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if (entity.getId() == null) throw new IllegalArgumentException("entity id must be not null");

        final String sql = "UPDATE friendships SET user1_id = ?, user2_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, entity.getUser1().getId());
            ps.setLong(2, entity.getUser2().getId());
            ps.setLong(3, entity.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) return entity;
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find friendship by both user IDs
     */
    public Friendship findByUsers(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) throw new IllegalArgumentException("user ids must be not null");
        final String sql = "SELECT id, user1_id, user2_id FROM friendships WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId1);
            ps.setLong(2, userId2);
            ps.setLong(3, userId2);
            ps.setLong(4, userId1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find all friendships of a user
     */
    public Iterable<Friendship> findByUser(Long userId) {
        if (userId == null) throw new IllegalArgumentException("user id must be not null");
        final String sql = "SELECT id, user1_id, user2_id FROM friendships WHERE user1_id = ? OR user2_id = ?";
        List<Friendship> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    private Friendship mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long user1Id = rs.getLong("user1_id");
        Long user2Id = rs.getLong("user2_id");

        User user1 = userRepository.findOne(user1Id);
        User user2 = userRepository.findOne(user2Id);

        Friendship friendship = new Friendship(user1, user2);
        friendship.setId(id);
        return friendship;
    }
}
