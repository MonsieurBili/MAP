package Repository.Database;

import Domain.Ducks.Card;
import Domain.Ducks.TipRata;
import Repository.Repository;
import Validators.CardValidator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositoryCardDb implements Repository<Long, Card> {
    CardValidator validator;
    public RepositoryCardDb(CardValidator validator) {
        this.validator = validator;
    }
    @Override
    public Card findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT id, card_nume, tip_rata FROM carduri WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findOne on Card: " + e.getMessage(), e);
        }
    }

    @Override
    public Iterable<Card> findAll() {
        final String sql = "SELECT id, card_nume, tip_rata FROM carduri";
        List<Card> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing findAll on Card: " + e.getMessage(), e);
        }
        return results;
    }

    @Override
    public Card save(Card entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        final String cardSql = "INSERT INTO carduri (card_nume,tip_rata) VALUES (?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try  (PreparedStatement stmt = conn.prepareStatement(cardSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, entity.getNumeCard());
                stmt.setString(2, entity.getTip().toString());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        long generatedId = keys.getLong(1);
                        entity.setId(generatedId);
                    } else {
                        throw new SQLException("Failed to retrieve generated ID for Card.");
                    }
                }
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public Card delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        Card existingCard = findOne(id);
        if (existingCard == null) {
            return null;
        }
        final String sql = "DELETE FROM carduri WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return existingCard;
            } else {

                return existingCard;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Card with ID " + id + ": " + e.getMessage(), e);
        }
    }


    @Override
    public Card update(Card entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if (entity.getId() == null) throw new IllegalArgumentException("entity id must be not null");
        final String sql = "UPDATE carduri SET card_nume = ?, tip_rata = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getNumeCard());
            ps.setString(2, entity.getTip().toString());
            ps.setLong(3, entity.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return null;
            } else {
                return entity;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating Card with ID " + entity.getId() + ": " + e.getMessage(), e);
        }
    }
    private Card mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String numeCard = rs.getString("card_nume");

        TipRata tip = TipRata.valueOf(rs.getString("tip_rata"));
        Card card = new Card(numeCard, tip);
        card.setId(id);

        return card;
    }
}
