package Repository.Database;

import Domain.Ducks.Duck;
import Domain.RaceEvent;
import Repository.Repository;
import Validators.EventValidator;
import Repository.Database.RepositoryDuckDB;
import Domain.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoryEventDb implements Repository<Long, RaceEvent>
{
    private final EventValidator eventValidator;
    private RepositoryDuckDB repositoryDuckDB;
    private RepositoryPersonDB repositoryPersonDB;
    public RepositoryEventDb(EventValidator eventValidator,RepositoryDuckDB repositoryDuckDB,RepositoryPersonDB repositoryPersonDB) {

        this.eventValidator = eventValidator;
        this.repositoryDuckDB = repositoryDuckDB;
        this.repositoryPersonDB = repositoryPersonDB;
    }
    private List<User> GetSubscribers(Long eventId) {
        final String sql = "SELECT user_id FROM event_subscribers WHERE event_id = ?";
        List<User> subscribers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long userId = rs.getLong("user_id");
                    // Folosește repository-ul User pentru a încărca entitatea completă
                    User user = repositoryPersonDB.findOne(userId);
                    if (user == null)
                        user = repositoryDuckDB.findOne(userId);
                    if (user != null) subscribers.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la încărcarea abonaților evenimentului", e);
        }
        return subscribers;
    }

    private List<Duck> GetParticipants(Long idevent)
    {
        final String sql = "SELECT duck_id FROM event_participants WHERE event_id = ?";
        List<Duck> participants = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idevent);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Long duckId = rs.getLong("duck_id");
                    Duck duck = repositoryDuckDB.findOne(duckId);
                    if (duck != null) participants.add(duck);
                }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return participants;
    }

    @Override
    public RaceEvent findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT e.id,e.location,e.name,e.culoare FROM events e WHERE e.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    RaceEvent event = mapRow(rs);
                    List<Duck> participants = GetParticipants(event.getId());
                    List<User>  subscribers = GetSubscribers(event.getId());
                    for (Duck duck : participants) {
                        event.addParticipant((Domain.Ducks.SwimmingDuck) duck);
                    }
                    for (User user : subscribers) {
                        event.subscribe(user);
                    }
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
        final String sql = "SELECT e.id,e.location,e.name,e.culoare FROM events e";
        List<RaceEvent> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                RaceEvent event = mapRow(rs);
                List<Duck> participants = GetParticipants(event.getId());
                List<User>  subscribers = GetSubscribers(event.getId());
                for (Duck duck : participants) {
                    event.addParticipant((Domain.Ducks.SwimmingDuck) duck);
                }
                for (User user : subscribers) {
                    event.subscribe(user);
                }
                results.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public void addSubscriber(Long eventId, User user) {
        if (eventId == null || user == null || user.getId() == null) {
            throw new IllegalArgumentException("IDs must not be null.");
        }
        final String subscriberSql = "INSERT INTO event_subscribers (event_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(subscriberSql)) {

            ps.setLong(1, eventId);
            ps.setLong(2, user.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la adăugarea abonatului cu ID " + user.getId() + " în evenimentul " + eventId, e);
        }
    }

    public void addParticipant(Long eventId, Duck duck) {
        if (eventId == null || duck == null || duck.getId() == null) {
            throw new IllegalArgumentException("IDs must not be null.");
        }
        final String participantSql = "INSERT INTO event_participants (event_id, duck_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(participantSql)) {

            ps.setLong(1, eventId);
            ps.setLong(2, duck.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Eroare la adăugarea participantului cu ID " + duck.getId() + " în evenimentul " + eventId, e);
        }
    }
    @Override
    public RaceEvent save(RaceEvent entity) {
        if (entity.getId() == null) throw new IllegalArgumentException("ID is null");
        eventValidator.validate(entity);
        final String sql = "INSERT INTO events (location, name, culoare) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement psEvent = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                psEvent.setString(1, entity.getLocation());
                psEvent.setString(2, entity.getName());
                Double[] culoare = entity.getCuloare().toArray(new Double[0]);
                Array sqlArray = conn.createArrayOf("NUMERIC", culoare);
                psEvent.setArray(3, sqlArray);
                psEvent.executeUpdate();
                try (ResultSet keys = psEvent.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("no id found");
                    long id = keys.getLong(1);
                    entity.setId(id);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
    @Override
    public RaceEvent delete(Long id) {
            RaceEvent existingEvent = findOne(id);
            if (existingEvent == null) {
                return null;
            }

            final String sql = "DELETE FROM events WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {

                    return existingEvent;
                } else {
                    return existingEvent;
                }

            } catch (SQLException e) {
                throw new RuntimeException("Eroare la ștergerea RaceEvent cu ID " + id + ": " + e.getMessage(), e);
            }
        }

    @Override
    public RaceEvent update(RaceEvent entity) {
        return null;
    }

    public RaceEvent mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String location = rs.getString("location");
        String name = rs.getString("name");
        Array sqlArray = rs.getArray("culoare");

        RaceEvent result = new RaceEvent(location,name);
        if (sqlArray != null) {
            Object[] array = (Object[]) sqlArray.getArray();
            if (array != null) {
                List<Double> culoareList = Arrays.stream(array)
                        .map(o -> ((Number) o).doubleValue())
                        .collect(Collectors.toList());
                for (Double cl : culoareList) {
                    result.addculoar(cl);
                }
            }
        }
        result.setId(id);
        return result;
    }
}
