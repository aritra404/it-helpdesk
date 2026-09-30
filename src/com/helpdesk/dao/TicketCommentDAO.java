package com.helpdesk.dao;

import com.helpdesk.model.TicketComment;
import com.helpdesk.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * TicketCommentDAO handles database operations for the 'ticket_comments' table.
 */
public class TicketCommentDAO {

    /**
     * Adds a troubleshooting note or comment to a specific ticket.
     */
    public int addComment(int ticketId, int userId, String commentText) throws SQLException {
        String sql = "INSERT INTO ticket_comments (ticket_id, user_id, comment_text) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ticketId);
            ps.setInt(2, userId);
            ps.setString(3, commentText);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding comment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Adding comment failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves all comments for a specific ticket, ordered chronologically.
     */
    public List<TicketComment> getCommentsByTicketId(int ticketId) throws SQLException {
        List<TicketComment> comments = new ArrayList<>();
        String sql = "SELECT c.comment_id, c.ticket_id, c.user_id, c.comment_text, c.created_at, " +
                     "       u.name AS user_name, u.role AS user_role " +
                     "FROM ticket_comments c " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "WHERE c.ticket_id = ? " +
                     "ORDER BY c.created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int commentId = rs.getInt("comment_id");
                    int userId = rs.getInt("user_id");
                    String commentText = rs.getString("comment_text");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    String userName = rs.getString("user_name");
                    String userRole = rs.getString("user_role");

                    TicketComment comment = new TicketComment(commentId, ticketId, userId, userName, userRole, commentText, createdAt);
                    comments.add(comment);
                }
            }
        }

        return comments;
    }
}
