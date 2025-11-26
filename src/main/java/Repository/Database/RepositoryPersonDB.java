package Repository.Database;

import Domain.Person.Persoana;
import Repository.Repository;
import Validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositoryPersonDB implements Repository<Long, Persoana> {
    private final Validator<Persoana> validator;
    public RepositoryPersonDB(Validator<Persoana> validator) {
        this.validator = validator;
    }

    @Override
    public Persoana findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT u.id,u.username,u.email,u.password,u.user_type," +
                " p.last_name,p.first_name,p.date_of_birth,p.occupation,p.empathy_level" + " FROM users u JOIN persons p ON u.id = p.id WHERE u.id = ?";
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
    public Iterable<Persoana>findAll()
    {
        String sql = "SELECT u.id, u.username, u.email, u.password, u.user_type, " + "p.last_name, p.first_name, p.date_of_birth, p.occupation, p.empathy_level " + "FROM users u JOIN persons p ON u.id = p.id";
        List<Persoana> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
                results.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @Override
    public Persoana save(Persoana entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        final String userSql = "INSERT INTO users (username,email,password,user_type) VALUES (?,?,?,?)";
        final String personSql = "INSERT INTO persons (id,last_name,first_name,date_of_birth,occupation,empathy_level) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, entity.getUsername());
                psUser.setString(2, entity.getEmail());
                psUser.setString(3, entity.getPassword());
                psUser.setString(4, "PERSON");
                psUser.executeUpdate();

                try (ResultSet keys = psUser.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No id obtained for user");
                    long userId = keys.getLong(1);

                    try (PreparedStatement psPerson = conn.prepareStatement(personSql)) {
                        psPerson.setLong(1, userId);
                        psPerson.setString(2, entity.getNume());
                        psPerson.setString(3, entity.getPrenume());
                        LocalDate dob = entity.getDataNasterii();
                        psPerson.setDate(4, dob == null ? null : Date.valueOf(dob));
                        psPerson.setString(5, entity.getOcupatie());
                        try {
                            psPerson.setInt(6, entity.getNivelEmpatie());
                        } catch (Throwable t) {
                            psPerson.setInt(6, 0);
                        }
                        psPerson.executeUpdate();
                    }

                    conn.commit();
                    entity.setId(userId);
                    return null;
                }
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
    public Persoana delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        // Follow same semantic as your in-memory repo: return removed entity
        Persoana existing = findOne(id);
        if (existing == null) return null;
        final String sql = "DELETE FROM users WHERE id = ?";
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
    public Persoana update(Persoana entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if (entity.getId() == null) throw new IllegalArgumentException("entity id must be not null");

        final String userSql = "UPDATE users SET username=?, email=?, password=?, user_type=? WHERE id=?";
        final String personSql = "UPDATE persons SET last_name=?, first_name=?, date_of_birth=?, occupation=?, empathy_level=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psUser = conn.prepareStatement(userSql);
                 PreparedStatement psPerson = conn.prepareStatement(personSql)) {

                psUser.setString(1, entity.getUsername());
                psUser.setString(2, entity.getEmail());
                psUser.setString(3, entity.getPassword());
                psUser.setString(4, "PERSON");
                psUser.setLong(5, entity.getId());
                int uRows = psUser.executeUpdate();

                psPerson.setString(1, entity.getNume());
                psPerson.setString(2, entity.getPrenume());
                LocalDate dob = entity.getDataNasterii();
                psPerson.setDate(3, dob == null ? null : Date.valueOf(dob));
                psPerson.setString(4, entity.getOcupatie());
                try {
                    psPerson.setInt(5, entity.getNivelEmpatie());
                } catch (Throwable t) {
                    psPerson.setInt(5, 0);
                }
                psPerson.setLong(6, entity.getId());
                int pRows = psPerson.executeUpdate();
                conn.commit();
                if (uRows == 0 && pRows == 0) return entity;
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

    private Persoana mapRow(ResultSet rs) throws SQLException {
        // Adapt to your Persoana constructor / setters if they differ.
        Persoana p = new Persoana(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getDate("date_of_birth") == null ? null : rs.getDate("date_of_birth").toLocalDate(),
                rs.getString("occupation")
        );
        p.setId(rs.getLong("id"));
        return p;
    }


}
