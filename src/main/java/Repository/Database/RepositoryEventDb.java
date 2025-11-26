package Repository.Database;

import Domain.Ducks.Duck;
import Domain.Ducks.SwimmingDuck;
import Domain.RaceEvent;
import Domain.User;
import Repository.Repository;
import Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryEventDb implements Repository<Long, RaceEvent> {
    private final Validator<RaceEvent> validator;
    private final RepositoryDuckDB repositoryDuckDB;
    private final RepositoryPersonDB repositoryPersonDB;

    public RepositoryEventDb(Validator<RaceEvent> validator, RepositoryDuckDB repositoryDuckDB, RepositoryPersonDB repositoryPersonDB) {
        this.validator = validator;
        this.repositoryDuckDB = repositoryDuckDB;
        this.repositoryPersonDB = repositoryPersonDB;
    }

    @Override
    public RaceEvent findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT id, name, location FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RaceEvent event = mapRow(rs);
                    loadParticipants(event);
                    loadSubscribers(event);
                    return event;
                }
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
            while (rs.next()) {
                RaceEvent event = mapRow(rs);
                loadParticipants(event);
                loadSubscribers(event);
                results.add(event);
            }
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
            saveParticipants(entity);
            saveSubscribers(entity);
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
            deleteParticipants(entity.getId());
            deleteSubscribers(entity.getId());
            saveParticipants(entity);
            saveSubscribers(entity);
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

    private void loadParticipants(RaceEvent event) {
        final String sql = "SELECT duck_id FROM event_participants WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, event.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long duckId = rs.getLong("duck_id");
                    Duck duck = repositoryDuckDB.findOne(duckId);
                    if (duck instanceof SwimmingDuck) {
                        event.addParticipant((SwimmingDuck) duck);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSubscribers(RaceEvent event) {
        final String sql = "SELECT user_id FROM event_subscribers WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, event.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long userId = rs.getLong("user_id");
                    User user = repositoryDuckDB.findOne(userId);
                    if (user == null) {
                        user = repositoryPersonDB.findOne(userId);
                    }
                    if (user != null) {
                        event.subscribe(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveParticipants(RaceEvent event) {
        final String sql = "INSERT INTO event_participants (event_id, duck_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (SwimmingDuck participant : event.getParticipants()) {
                ps.setLong(1, event.getId());
                ps.setLong(2, participant.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSubscribers(RaceEvent event) {
        final String sql = "INSERT INTO event_subscribers (event_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (User subscriber : event.getSubscribers()) {
                ps.setLong(1, event.getId());
                ps.setLong(2, subscriber.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteParticipants(Long eventId) {
        final String sql = "DELETE FROM event_participants WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteSubscribers(Long eventId) {
        final String sql = "DELETE FROM event_subscribers WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
