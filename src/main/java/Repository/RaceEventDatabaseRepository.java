package Repository;

import Database.DatabaseConnection;
import Domain.RaceEvent;
import Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository implementation for RaceEvent entities using PostgreSQL database
 */
public class RaceEventDatabaseRepository implements Repository<Long, RaceEvent> {
    private final Validator<RaceEvent> validator;
    private final DatabaseConnection dbConnection;
    
    public RaceEventDatabaseRepository(Validator<RaceEvent> validator) {
        this.validator = validator;
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public RaceEvent findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        String sql = "SELECT id, event_name, location FROM race_events WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractRaceEventFromResultSet(resultSet, connection);
            }
        } catch (SQLException e) {
            System.err.println("Error finding race event: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Iterable<RaceEvent> findAll() {
        List<RaceEvent> events = new ArrayList<>();
        String sql = "SELECT id, event_name, location FROM race_events";
        
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                events.add(extractRaceEventFromResultSet(resultSet, connection));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all race events: " + e.getMessage());
        }
        
        return events;
    }
    
    @Override
    public RaceEvent save(RaceEvent entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null");
        }
        validator.validate(entity);
        
        // Check if event already exists
        if (findOne(entity.getId()) != null) {
            return entity;
        }
        
        String eventSql = "INSERT INTO race_events (id, event_name, location) VALUES (?, ?, ?)";
        String laneSql = "INSERT INTO race_event_lanes (event_id, lane_number) VALUES (?, ?)";
        
        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement eventStmt = connection.prepareStatement(eventSql);
                 PreparedStatement laneStmt = connection.prepareStatement(laneSql)) {
                
                // Insert event
                eventStmt.setLong(1, entity.getId());
                eventStmt.setString(2, entity.getName());
                eventStmt.setString(3, entity.getLocation());
                eventStmt.executeUpdate();
                
                // Insert lanes
                for (Double lane : entity.getCuloare()) {
                    laneStmt.setLong(1, entity.getId());
                    laneStmt.setDouble(2, lane);
                    laneStmt.executeUpdate();
                }
                
                connection.commit();
                return null;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error saving race event: " + e.getMessage());
            return entity;
        }
    }
    
    @Override
    public RaceEvent delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        RaceEvent event = findOne(id);
        if (event == null) {
            return null;
        }
        
        String sql = "DELETE FROM race_events WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            statement.executeUpdate();
            return event;
        } catch (SQLException e) {
            System.err.println("Error deleting race event: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public RaceEvent update(RaceEvent entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        validator.validate(entity);
        
        if (findOne(entity.getId()) == null) {
            return entity;
        }
        
        String eventSql = "UPDATE race_events SET event_name = ?, location = ? WHERE id = ?";
        String deleteLanesSql = "DELETE FROM race_event_lanes WHERE event_id = ?";
        String insertLaneSql = "INSERT INTO race_event_lanes (event_id, lane_number) VALUES (?, ?)";
        
        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement eventStmt = connection.prepareStatement(eventSql);
                 PreparedStatement deleteLanesStmt = connection.prepareStatement(deleteLanesSql);
                 PreparedStatement insertLaneStmt = connection.prepareStatement(insertLaneSql)) {
                
                // Update event
                eventStmt.setString(1, entity.getName());
                eventStmt.setString(2, entity.getLocation());
                eventStmt.setLong(3, entity.getId());
                eventStmt.executeUpdate();
                
                // Delete old lanes
                deleteLanesStmt.setLong(1, entity.getId());
                deleteLanesStmt.executeUpdate();
                
                // Insert new lanes
                for (Double lane : entity.getCuloare()) {
                    insertLaneStmt.setLong(1, entity.getId());
                    insertLaneStmt.setDouble(2, lane);
                    insertLaneStmt.executeUpdate();
                }
                
                connection.commit();
                return null;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error updating race event: " + e.getMessage());
            return entity;
        }
    }
    
    /**
     * Extract RaceEvent object from ResultSet
     */
    private RaceEvent extractRaceEventFromResultSet(ResultSet rs, Connection connection) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("event_name");
        String location = rs.getString("location");
        
        RaceEvent event = new RaceEvent(name, location);
        event.setId(id);
        
        // Load lanes
        String laneSql = "SELECT lane_number FROM race_event_lanes WHERE event_id = ?";
        try (PreparedStatement laneStmt = connection.prepareStatement(laneSql)) {
            laneStmt.setLong(1, id);
            ResultSet laneRs = laneStmt.executeQuery();
            
            while (laneRs.next()) {
                event.addculoar(laneRs.getDouble("lane_number"));
            }
        }
        
        return event;
    }
}
