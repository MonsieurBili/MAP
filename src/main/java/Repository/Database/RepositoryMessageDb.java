package Repository.Database;

import Domain.Message;
import Domain.User;
import Repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositoryMessageDb implements Repository<Long, Message> {
    private final RepositoryPersonDB personRepository;
    private final RepositoryDuckDB duckRepository;

    public RepositoryMessageDb(RepositoryPersonDB personRepository, RepositoryDuckDB duckRepository) {
        this.personRepository = personRepository;
        this.duckRepository = duckRepository;
    }

    private User findUserById(Long id) {
        User user = personRepository.findOne(id);
        if (user == null) {
            user = duckRepository.findOne(id);
        }
        return user;
    }

    @Override
    public Message findOne(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");

        String sql = "SELECT id, from_user_id, message_text, sent_at, reply_to_id FROM messages WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMessage(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT id, from_user_id, message_text, sent_at, reply_to_id FROM messages ORDER BY sent_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    public List<Message> findConversation(Long userId1, Long userId2) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT DISTINCT m.id, m.from_user_id, m.message_text, m.sent_at, m.reply_to_id 
            FROM messages m
            LEFT JOIN message_recipients mr ON m.id = mr.message_id
            WHERE (m.from_user_id = ? AND mr.user_id = ?)
               OR (m.from_user_id = ? AND mr.user_id = ?)
            ORDER BY m.sent_at ASC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId1);
            ps.setLong(2, userId2);
            ps.setLong(3, userId2);
            ps.setLong(4, userId1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Message save(Message entity) {
        if (entity == null) throw new IllegalArgumentException("Message cannot be null");

        String insertMessage = "INSERT INTO messages (from_user_id, message_text, sent_at, reply_to_id) VALUES (?, ?, ?, ?)";
        String insertRecipient = "INSERT INTO message_recipients (message_id, user_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, entity.getFrom().getId());
                    ps.setString(2, entity.getMessage());
                    ps.setTimestamp(3, Timestamp.valueOf(entity.getData()));
                    if (entity.getReply() != null) {
                        ps.setLong(4, entity.getReply().getId());
                    } else {
                        ps.setNull(4, Types.BIGINT);
                    }
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            entity.setId(keys.getLong(1));
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertRecipient)) {
                    for (User recipient : entity.getTo()) {
                        ps.setLong(1, entity.getId());
                        ps.setLong(2, recipient.getId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return null;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message delete(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");

        Message message = findOne(id);
        if (message == null) return null;

        String deleteRecipients = "DELETE FROM message_recipients WHERE message_id = ?";
        String deleteMessage = "DELETE FROM messages WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(deleteRecipients)) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(deleteMessage)) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                conn.commit();
                return message;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message update(Message entity) {
        if (entity == null || entity.getId() == null)
            throw new IllegalArgumentException("Message and ID cannot be null");

        String sql = "UPDATE messages SET message_text = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getMessage());
            ps.setLong(2, entity.getId());
            int rows = ps.executeUpdate();
            return rows == 0 ? entity : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long fromUserId = rs.getLong("from_user_id");
        String messageText = rs.getString("message_text");
        LocalDateTime sentAt = rs.getTimestamp("sent_at").toLocalDateTime();
        Long replyToId = rs.getObject("reply_to_id") != null ? rs.getLong("reply_to_id") : null;

        User fromUser = findUserById(fromUserId);
        List<User> toUsers = findRecipients(id);

        Message replyMessage = null;
        if (replyToId != null) {
            replyMessage = findOne(replyToId);
        }

        Message message = new Message(fromUser, toUsers, messageText, sentAt, replyMessage);
        message.setId(id);
        return message;
    }

    private List<User> findRecipients(Long messageId) {
        List<User> recipients = new ArrayList<>();
        String sql = "SELECT user_id FROM message_recipients WHERE message_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long userId = rs.getLong("user_id");
                    User user = findUserById(userId);
                    if (user != null) {
                        recipients.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return recipients;
    }
}

