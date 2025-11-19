package Repository;

import Database.DatabaseConnection;
import Domain.Person.Persoana;
import Validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository implementation for Person entities using PostgreSQL database
 */
public class PersonDatabaseRepository implements Repository<Long, Persoana> {
    private final Validator<Persoana> validator;
    private final DatabaseConnection dbConnection;
    
    public PersonDatabaseRepository(Validator<Persoana> validator) {
        this.validator = validator;
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public Persoana findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        String sql = "SELECT u.id, u.username, u.email, u.password, " +
                     "p.last_name, p.first_name, p.date_of_birth, p.occupation, p.empathy_level " +
                     "FROM users u " +
                     "JOIN persons p ON u.id = p.id " +
                     "WHERE u.id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractPersonFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding person: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public Iterable<Persoana> findAll() {
        List<Persoana> persons = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email, u.password, " +
                     "p.last_name, p.first_name, p.date_of_birth, p.occupation, p.empathy_level " +
                     "FROM users u " +
                     "JOIN persons p ON u.id = p.id";
        
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                persons.add(extractPersonFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all persons: " + e.getMessage());
        }
        
        return persons;
    }
    
    @Override
    public Persoana save(Persoana entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null");
        }
        validator.validate(entity);
        
        // Check if person already exists
        if (findOne(entity.getId()) != null) {
            return entity;
        }
        
        String userSql = "INSERT INTO users (id, username, email, password, user_type) VALUES (?, ?, ?, ?, 'PERSON')";
        String personSql = "INSERT INTO persons (id, last_name, first_name, date_of_birth, occupation, empathy_level) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement userStmt = connection.prepareStatement(userSql);
                 PreparedStatement personStmt = connection.prepareStatement(personSql)) {
                
                // Insert into users table
                userStmt.setLong(1, entity.getId());
                userStmt.setString(2, entity.getUsername());
                userStmt.setString(3, entity.getEmail());
                userStmt.setString(4, entity.getPassword());
                userStmt.executeUpdate();
                
                // Insert into persons table
                personStmt.setLong(1, entity.getId());
                personStmt.setString(2, entity.getNume());
                personStmt.setString(3, entity.getPrenume());
                personStmt.setDate(4, Date.valueOf(entity.getDataNasterii()));
                personStmt.setString(5, entity.getOcupatie());
                personStmt.setInt(6, entity.getNivelEmpatie());
                personStmt.executeUpdate();
                
                connection.commit();
                return null;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error saving person: " + e.getMessage());
            return entity;
        }
    }
    
    @Override
    public Persoana delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be not null");
        }
        
        Persoana person = findOne(id);
        if (person == null) {
            return null;
        }
        
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            statement.executeUpdate();
            return person;
        } catch (SQLException e) {
            System.err.println("Error deleting person: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Persoana update(Persoana entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        validator.validate(entity);
        
        if (findOne(entity.getId()) == null) {
            return entity;
        }
        
        String userSql = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
        String personSql = "UPDATE persons SET last_name = ?, first_name = ?, date_of_birth = ?, " +
                          "occupation = ?, empathy_level = ? WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection()) {
            connection.setAutoCommit(false);
            
            try (PreparedStatement userStmt = connection.prepareStatement(userSql);
                 PreparedStatement personStmt = connection.prepareStatement(personSql)) {
                
                // Update users table
                userStmt.setString(1, entity.getUsername());
                userStmt.setString(2, entity.getEmail());
                userStmt.setString(3, entity.getPassword());
                userStmt.setLong(4, entity.getId());
                userStmt.executeUpdate();
                
                // Update persons table
                personStmt.setString(1, entity.getNume());
                personStmt.setString(2, entity.getPrenume());
                personStmt.setDate(3, Date.valueOf(entity.getDataNasterii()));
                personStmt.setString(4, entity.getOcupatie());
                personStmt.setInt(5, entity.getNivelEmpatie());
                personStmt.setLong(6, entity.getId());
                personStmt.executeUpdate();
                
                connection.commit();
                return null;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error updating person: " + e.getMessage());
            return entity;
        }
    }
    
    /**
     * Extract Persoana object from ResultSet
     */
    private Persoana extractPersonFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String lastName = rs.getString("last_name");
        String firstName = rs.getString("first_name");
        LocalDate dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
        String occupation = rs.getString("occupation");
        
        Persoana person = new Persoana(username, email, password, lastName, firstName, dateOfBirth, occupation);
        person.setId(id);
        return person;
    }
}
