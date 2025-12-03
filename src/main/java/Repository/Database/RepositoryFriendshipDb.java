package Repository.Database;

import Domain.Friendship;
import Domain.User;
import Repository.Repository;
import Validators.FriendshipValidator;
import Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryFriendshipDb implements Repository<Long, Friendship> {
    private final FriendshipValidator validator;
    private RepositoryPersonDB repositoryPersonDB;
    private RepositoryDuckDB repositoryDuckDB;
    public  RepositoryFriendshipDb(FriendshipValidator validator ,RepositoryPersonDB repositoryPersonDB, RepositoryDuckDB repositoryDuckDB) {
        this.validator = validator;
        this.repositoryPersonDB = repositoryPersonDB;
        this.repositoryDuckDB = repositoryDuckDB;
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
                    long generatedId = keys.getLong(1);
                    entity.setId(generatedId);
                } else {
                    throw new SQLException("Failed to retrieve generated ID for Card.");
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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

    private Friendship mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long user1Id = rs.getLong("user1_id");
        Long user2Id = rs.getLong("user2_id");
        User user1 = repositoryDuckDB.findOne(user1Id);
        User user2 = repositoryDuckDB.findOne(user2Id);
        if (user1 == null) {
            user1 = repositoryPersonDB.findOne(user1Id);
        }
        if (user2 == null)
        {
            user2 = repositoryPersonDB.findOne(user2Id);
        }
        Friendship friendship = new Friendship(user1, user2);
        friendship.setId(id);
        return friendship;
    }

}
