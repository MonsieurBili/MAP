package Repository.Database;

import Domain.Ducks.Duck;
import Domain.Ducks.DuckFactory;
import Domain.Ducks.TipRata;
import Repository.Repository;
import Validators.Validator;
import Repository.PagingRepository;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryDuckDB implements PagingRepository<Long, Duck> {
    private final Validator<Duck> validator;
    private final DuckFactory duckFactory;

    public RepositoryDuckDB(Validator<Duck> validator) {
        this.validator = validator;
        this.duckFactory = DuckFactory.getInstance();
    }
    @Override
    public Page<Duck> findAllOnPage(Pageable pageable) {
        List<Duck> ducksOnPage = new ArrayList<>();
        int totalCount = 0;
        String countSql = "SELECT COUNT(*) AS count FROM users u JOIN ducks d ON u.id = d.id";
        String pagingSql = "SELECT u.id, u.username, u.email, u.password, u.user_type, " +
                "d.tip_rata, d.viteza, d.rezistenta, d.idcard " +
                "FROM users u JOIN ducks d ON u.id = d.id " + "ORDER BY u.id ASC " + "LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCount = conn.prepareStatement(countSql);
                 ResultSet rsCount = psCount.executeQuery()) {
                if (rsCount.next()) {
                    totalCount = rsCount.getInt("count");
                }
            }
            if (totalCount > 0) {
                try (PreparedStatement psPaging = conn.prepareStatement(pagingSql)) {
                    psPaging.setInt(1, pageable.getPageSize());
                    psPaging.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
                    try (ResultSet rs = psPaging.executeQuery()) {
                        while (rs.next()) {
                            ducksOnPage.add(mapRow(rs));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(ducksOnPage, totalCount);
    }


    @Override
    public Duck findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT u.id, u.username, u.email, u.password, u.user_type, " +
                "d.tip_rata, d.viteza, d.rezistenta, d.idcard " +
                "FROM users u JOIN ducks d ON u.id = d.id WHERE u.id = ?";
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
    public Iterable<Duck> findAll() {
        final String sql = "SELECT u.id, u.username, u.email, u.password, u.user_type, " +
                "d.tip_rata, d.viteza, d.rezistenta, d.idcard " +
                "FROM users u JOIN ducks d ON u.id = d.id";
        List<Duck> results = new ArrayList<>();
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
    public Duck save(Duck entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        final String userSql = "INSERT INTO users (username,email,password,user_type) VALUES (?,?,?,?)";

        final String duckSql = "INSERT INTO ducks (id, tip_rata, viteza, rezistenta,idcard) VALUES (?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, entity.getUsername());
                psUser.setString(2, entity.getEmail());
                psUser.setString(3, entity.getPassword());
                psUser.setString(4, "DUCK");
                psUser.executeUpdate();

                try (ResultSet keys = psUser.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No id obtained for user");
                    long userId = keys.getLong(1);

                    try (PreparedStatement psDuck = conn.prepareStatement(duckSql)) {
                        psDuck.setLong(1, userId);
                        psDuck.setString(2, entity.getTipRata().name());
                        psDuck.setDouble(3, entity.getViteza());
                        psDuck.setDouble(4, entity.getRezistenta());
                        psDuck.setLong(5, entity.getIdCard());
                        psDuck.executeUpdate();
                    }

                    entity.setId(userId);
                    return null;
                }
            }
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }
    @Override
    public Duck delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        Duck existing = findOne(id);
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
    public Duck update(Duck entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if (entity.getId() == null) throw new IllegalArgumentException("entity id must be not null");

        final String userSql = "UPDATE users SET username=?, email=?, password=?, user_type=? WHERE id=?";
        final String duckSql = "UPDATE ducks SET tip_rata=?, viteza=?, rezistenta=?, idcard=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement psUser = conn.prepareStatement(userSql);
                 PreparedStatement psDuck = conn.prepareStatement(duckSql)) {

                psUser.setString(1, entity.getUsername());
                psUser.setString(2, entity.getEmail());
                psUser.setString(3, entity.getPassword());
                psUser.setString(4, "DUCK");
                psUser.setLong(5, entity.getId());
                int uRows = psUser.executeUpdate();

                psDuck.setString(1, entity.getTipRata().name());
                psDuck.setDouble(2, entity.getViteza());
                psDuck.setDouble(3, entity.getRezistenta());
                psDuck.setLong(5, entity.getId());
                psDuck.setLong(4, entity.getIdCard());
                int dRows = psDuck.executeUpdate();
                if (uRows == 0 && dRows == 0) return entity;
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Duck mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        TipRata tipRata = TipRata.valueOf(rs.getString("tip_rata"));
        double viteza = rs.getDouble("viteza");
        double rezistenta = rs.getDouble("rezistenta");
        long idcard = rs.getLong("idcard");
        duckFactory.setData(username, email, password, tipRata, viteza, rezistenta,idcard);
        Duck d = duckFactory.createUser();
        d.setId(id);
        return d;
    }

    public Iterable<Duck> filterByType(String type) {
        final String sql = "SELECT u.id, u.username, u.email, u.password, u.user_type, " +
                "d.tip_rata, d.viteza, d.rezistenta, d.idcard " +
                "FROM users u JOIN ducks d ON u.id = d.id WHERE d.tip_rata = ?";
        List<Duck> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,type);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(mapRow(rs));
                    }
                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public Page<Duck> findAllOnPageFiltered(Pageable pageable, String typeFilter) {
        List<Duck> ducksOnPage = new ArrayList<>();
        int totalCount = 0;

        String countSql;
        String pagingSql;

        if (typeFilter == null || typeFilter.isEmpty()) {
            countSql = "SELECT COUNT(*) AS count FROM users u JOIN ducks d ON u.id = d.id";
            pagingSql = "SELECT u.id, u.username, u.email, u.password, u.user_type, " +
                    "d.tip_rata, d.viteza, d.rezistenta, d.idcard " +
                    "FROM users u JOIN ducks d ON u.id = d.id " +
                    "ORDER BY u.id ASC " +
                    "LIMIT ? OFFSET ?";
        } else {
            countSql = "SELECT COUNT(*) AS count FROM users u JOIN ducks d ON u.id = d.id WHERE d.tip_rata = ?";
            pagingSql = "SELECT u.id, u.username, u.email, u.password, u.user_type, " +
                    "d.tip_rata, d.viteza, d.rezistenta, d.idcard " +
                    "FROM users u JOIN ducks d ON u.id = d.id " +
                    "WHERE d.tip_rata = ? " +
                    "ORDER BY u.id ASC " +
                    "LIMIT ? OFFSET ?";
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                if (typeFilter != null && !typeFilter.isEmpty()) {
                    psCount.setString(1, typeFilter);
                }
                try (ResultSet rsCount = psCount.executeQuery()) {
                    if (rsCount.next()) {
                        totalCount = rsCount.getInt("count");
                    }
                }
            }

            if (totalCount > 0) {
                try (PreparedStatement psPaging = conn.prepareStatement(pagingSql)) {
                    if (typeFilter != null && !typeFilter.isEmpty()) {
                        psPaging.setString(1, typeFilter);
                        psPaging.setInt(2, pageable.getPageSize());
                        psPaging.setInt(3, pageable.getPageNumber() * pageable.getPageSize());
                    } else {
                        psPaging.setInt(1, pageable.getPageSize());
                        psPaging.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
                    }
                    try (ResultSet rs = psPaging.executeQuery()) {
                        while (rs.next()) {
                            ducksOnPage.add(mapRow(rs));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(ducksOnPage, totalCount);
    }
}