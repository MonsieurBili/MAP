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

public class RepositoryRaceEventDB implements Repository<Long, RaceEvent> {
    private final Validator<RaceEvent> validator;
    private final Repository<Long, Duck> duckRepository;

    public RepositoryRaceEventDB(Validator<RaceEvent> validator, Repository<Long, Duck> duckRepository) {
        this.validator = validator;
        this.duckRepository = duckRepository;
    }

    @Override
    public RaceEvent findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT id, name, location FROM race_events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RaceEvent event = mapRow(rs);
                    loadLanes(conn, event);
                    loadParticipants(conn, event);
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
        final String sql = "SELECT id, name, location FROM race_events";
        List<RaceEvent> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RaceEvent event = mapRow(rs);
                loadLanes(conn, event);
                loadParticipants(conn, event);
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

        final String eventSql = "INSERT INTO race_events (name, location) VALUES (?, ?)";
        final String laneSql = "INSERT INTO lanes (event_id, lane_length) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psEvent = conn.prepareStatement(eventSql, Statement.RETURN_GENERATED_KEYS)) {
                psEvent.setString(1, entity.getName());
                psEvent.setString(2, entity.getLocation());
                psEvent.executeUpdate();

                try (ResultSet keys = psEvent.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No id obtained for race event");
                    long eventId = keys.getLong(1);
                    entity.setId(eventId);

                    // Save lanes
                    try (PreparedStatement psLane = conn.prepareStatement(laneSql)) {
                        for (Double laneLength : entity.getCuloare()) {
                            psLane.setLong(1, eventId);
                            psLane.setDouble(2, laneLength);
                            psLane.addBatch();
                        }
                        psLane.executeBatch();
                    }
                }
                conn.commit();
                return null;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RaceEvent delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        RaceEvent existing = findOne(id);
        if (existing == null) return null;

        final String sql = "DELETE FROM race_events WHERE id = ?";
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

        final String eventSql = "UPDATE race_events SET name = ?, location = ? WHERE id = ?";
        final String deleteLanesSql = "DELETE FROM lanes WHERE event_id = ?";
        final String laneSql = "INSERT INTO lanes (event_id, lane_length) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psEvent = conn.prepareStatement(eventSql);
                 PreparedStatement psDeleteLanes = conn.prepareStatement(deleteLanesSql);
                 PreparedStatement psLane = conn.prepareStatement(laneSql)) {

                psEvent.setString(1, entity.getName());
                psEvent.setString(2, entity.getLocation());
                psEvent.setLong(3, entity.getId());
                int rows = psEvent.executeUpdate();

                if (rows == 0) {
                    conn.rollback();
                    return entity;
                }

                // Delete old lanes and insert new ones
                psDeleteLanes.setLong(1, entity.getId());
                psDeleteLanes.executeUpdate();

                for (Double laneLength : entity.getCuloare()) {
                    psLane.setLong(1, entity.getId());
                    psLane.setDouble(2, laneLength);
                    psLane.addBatch();
                }
                psLane.executeBatch();

                conn.commit();
                return null;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a participant to an event
     */
    public void addParticipant(Long eventId, Long duckId) {
        if (eventId == null || duckId == null) throw new IllegalArgumentException("ids must be not null");
        final String sql = "INSERT INTO event_participants (event_id, duck_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.setLong(2, duckId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove a participant from an event
     */
    public void removeParticipant(Long eventId, Long duckId) {
        if (eventId == null || duckId == null) throw new IllegalArgumentException("ids must be not null");
        final String sql = "DELETE FROM event_participants WHERE event_id = ? AND duck_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.setLong(2, duckId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Subscribe a user to an event
     */
    public void subscribeUser(Long eventId, Long userId) {
        if (eventId == null || userId == null) throw new IllegalArgumentException("ids must be not null");
        final String sql = "INSERT INTO event_subscribers (event_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unsubscribe a user from an event
     */
    public void unsubscribeUser(Long eventId, Long userId) {
        if (eventId == null || userId == null) throw new IllegalArgumentException("ids must be not null");
        final String sql = "DELETE FROM event_subscribers WHERE event_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            ps.executeUpdate();
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

    private void loadLanes(Connection conn, RaceEvent event) throws SQLException {
        final String sql = "SELECT lane_length FROM lanes WHERE event_id = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, event.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    event.addculoar(rs.getDouble("lane_length"));
                }
            }
        }
    }

    private void loadParticipants(Connection conn, RaceEvent event) throws SQLException {
        final String sql = "SELECT duck_id FROM event_participants WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, event.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long duckId = rs.getLong("duck_id");
                    Duck duck = duckRepository.findOne(duckId);
                    if (duck instanceof SwimmingDuck) {
                        event.addParticipant((SwimmingDuck) duck);
                    }
                }
            }
        }
    }
}
