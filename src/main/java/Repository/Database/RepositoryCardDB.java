package Repository.Database;

import Domain.Ducks.Card;
import Domain.Ducks.Duck;
import Domain.Ducks.TipRata;
import Repository.Repository;
import Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryCardDB implements Repository<Long, Card> {
    private final Validator<Card> validator;
    private final Repository<Long, Duck> duckRepository;

    public RepositoryCardDB(Validator<Card> validator, Repository<Long, Duck> duckRepository) {
        this.validator = validator;
        this.duckRepository = duckRepository;
    }

    @Override
    public Card findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        final String sql = "SELECT id, name, tip_rata FROM cards WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Card card = mapRow(rs);
                    loadMembers(conn, card);
                    return card;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Card> findAll() {
        final String sql = "SELECT id, name, tip_rata FROM cards";
        List<Card> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Card card = mapRow(rs);
                loadMembers(conn, card);
                results.add(card);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @Override
    public Card save(Card entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        final String cardSql = "INSERT INTO cards (name, tip_rata) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(cardSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNumeCard());
            ps.setString(2, entity.getTip().name());
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
    public Card delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id must be not null");
        Card existing = findOne(id);
        if (existing == null) return null;

        final String sql = "DELETE FROM cards WHERE id = ?";
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
    public Card update(Card entity) {
        if (entity == null) throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if (entity.getId() == null) throw new IllegalArgumentException("entity id must be not null");

        final String sql = "UPDATE cards SET name = ?, tip_rata = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getNumeCard());
            ps.setString(2, entity.getTip().name());
            ps.setLong(3, entity.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) return entity;
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a duck member to a card (flock)
     */
    public void addMember(Long cardId, Long duckId) {
        if (cardId == null || duckId == null) throw new IllegalArgumentException("ids must be not null");
        final String sql = "INSERT INTO card_members (card_id, duck_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setLong(2, duckId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove a duck member from a card (flock)
     */
    public void removeMember(Long cardId, Long duckId) {
        if (cardId == null || duckId == null) throw new IllegalArgumentException("ids must be not null");
        final String sql = "DELETE FROM card_members WHERE card_id = ? AND duck_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setLong(2, duckId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Card mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        TipRata tipRata = TipRata.valueOf(rs.getString("tip_rata"));

        Card card = new Card(name, tipRata);
        card.setId(id);
        return card;
    }

    private void loadMembers(Connection conn, Card card) throws SQLException {
        final String sql = "SELECT duck_id FROM card_members WHERE card_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, card.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long duckId = rs.getLong("duck_id");
                    Duck duck = duckRepository.findOne(duckId);
                    if (duck != null) {
                        card.addMembri(duck);
                    }
                }
            }
        }
    }
}
