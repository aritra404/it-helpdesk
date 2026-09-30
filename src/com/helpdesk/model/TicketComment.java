package com.helpdesk.model;

import java.sql.Timestamp;

/**
 * TicketComment represents a comment or troubleshooting note added to a Ticket.
 * Mirrors the 'ticket_comments' table in MySQL.
 */
public class TicketComment {

    private int commentId;
    private int ticketId;
    private int userId;
    private String userName; // Joined from users table
    private String userRole; // Joined from users table
    private String commentText;
    private Timestamp createdAt;

    public TicketComment() {
    }

    public TicketComment(int commentId, int ticketId, int userId, String userName, String userRole, String commentText, Timestamp createdAt) {
        this.commentId = commentId;
        this.ticketId = ticketId;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s): %s",
            createdAt, userName, userRole, commentText);
    }
}
