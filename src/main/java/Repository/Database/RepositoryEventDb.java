package Repository.Database;

import Domain.RaceEvent;
import Repository.Repository;
import Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryEventDb implements Repository<Long, RaceEvent> {
    private final Validator<RaceEvent> validator;

    public RepositoryEventDb(Validator<RaceEvent> validator) {
        this.validator = validator;
    }

    @Override
    public RaceEvent findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT id, name, location FROM events WHERE id = ?";
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
    public Iterable<RaceEvent> findAll() {
        final String sql = "SELECT id, name, location FROM events";
        List<RaceEvent> results = new ArrayList<>();
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
    public RaceEvent save(RaceEvent entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        final String sql = "INSERT INTO events (name, location) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getLocation());
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
    public RaceEvent delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        RaceEvent existing = findOne(id);
        if (existing == null) return null;
        final String sql = "DELETE FROM events WHERE id = ?";
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
    public RaceEvent update(RaceEvent entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if (entity.getId() == null) throw new IllegalArgumentException("entity id must be not null");
        final String sql = "UPDATE events SET name = ?, location = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getLocation());
            ps.setLong(3, entity.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) return entity;
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private RaceEvent mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String location = rs.getString("location");
        RaceEvent event = new RaceEvent(name, location);
        event.setId(id);
        return event;
    }
}
