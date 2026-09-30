package com.helpdesk.dao;

import com.helpdesk.model.Ticket;
import com.helpdesk.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * TicketDAO (Data Access Object) handles all database interactions
 * for the 'tickets' table with role-based authorization checks.
 */
public class TicketDAO {

    // Base SELECT query joining tickets with users to get employee and technician names
    private static final String BASE_SELECT =
        "SELECT t.ticket_id, t.title, t.description, t.category, t.priority, t.status, " +
        "       t.employee_id, t.assigned_to, t.created_at, t.updated_at, " +
        "       e.name AS employee_name, tech.name AS technician_name " +
        "FROM tickets t " +
        "JOIN users e ON t.employee_id = e.user_id " +
        "LEFT JOIN users tech ON t.assigned_to = tech.user_id ";

    /**
     * Inserts a new ticket into the MySQL database.
     */
    public int createTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (title, description, category, priority, status, employee_id, assigned_to) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ticket.getTitle());
            ps.setString(2, ticket.getDescription());
            ps.setString(3, ticket.getCategory());
            ps.setString(4, ticket.getPriority());
            ps.setString(5, ticket.getStatus() != null ? ticket.getStatus() : "OPEN");
            ps.setInt(6, ticket.getEmployeeId());

            if (ticket.getAssignedTo() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, ticket.getAssignedTo());
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    ticket.setTicketId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves all tickets from the database (Admin privilege).
     */
    public List<Ticket> getAllTickets() throws SQLException {
        List<Ticket> ticketList = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY t.ticket_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ticketList.add(mapResultSetToTicket(rs));
            }
        }

        return ticketList;
    }

    /**
     * Retrieves only tickets created by a specific employee (Employee feature).
     */
    public List<Ticket> getTicketsByEmployee(int employeeId) throws SQLException {
        List<Ticket> ticketList = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.employee_id = ? ORDER BY t.ticket_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ticketList.add(mapResultSetToTicket(rs));
                }
            }
        }

        return ticketList;
    }

    /**
     * Finds a single ticket by its unique ticket ID.
     */
    public Ticket getTicketById(int ticketId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.ticket_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a ticket by ID only if owned by the specific employee (Authorization Check).
     */
    public Ticket getTicketForEmployee(int ticketId, int employeeId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.ticket_id = ? AND t.employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            ps.setInt(2, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a ticket for a technician only if assigned to them (Technician Authorization).
     */
    public Ticket getTicketForTechnician(int ticketId, int technicianId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.ticket_id = ? AND t.assigned_to = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            ps.setInt(2, technicianId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }

        return null;
    }

    /**
     * Checks if a ticket is owned by a specific employee.
     */
    public boolean isTicketOwnedBy(int ticketId, int employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tickets WHERE ticket_id = ? AND employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            ps.setInt(2, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a ticket is assigned to a specific technician.
     */
    public boolean isTicketAssignedTo(int ticketId, int technicianId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tickets WHERE ticket_id = ? AND assigned_to = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            ps.setInt(2, technicianId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Updates ticket details (Admin privilege).
     */
    public boolean updateTicketDetails(int ticketId, String title, String description, String category, String priority) throws SQLException {
        String sql = "UPDATE tickets SET title = ?, description = ?, category = ?, priority = ? WHERE ticket_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, category);
            ps.setString(4, priority);
            ps.setInt(5, ticketId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Assigns a ticket to a technician (Admin privilege).
     */
    public boolean assignTechnician(int ticketId, int technicianId) throws SQLException {
        String sql = "UPDATE tickets " +
                     "SET assigned_to = ?, " +
                     "    status = CASE WHEN status = 'OPEN' THEN 'ASSIGNED' ELSE status END " +
                     "WHERE ticket_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, technicianId);
            ps.setInt(2, ticketId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Updates the status of a ticket (Admin privilege).
     */
    public boolean updateTicketStatus(int ticketId, String newStatus) throws SQLException {
        String sql = "UPDATE tickets SET status = ? WHERE ticket_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, ticketId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Updates the status of a ticket ONLY if assigned to the given technician (Technician Authorization).
     */
    public boolean updateStatusForTechnician(int ticketId, int technicianId, String newStatus) throws SQLException {
        String sql = "UPDATE tickets SET status = ? WHERE ticket_id = ? AND assigned_to = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, ticketId);
            ps.setInt(3, technicianId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves all unresolved tickets.
     */
    public List<Ticket> getUnresolvedTickets() throws SQLException {
        List<Ticket> ticketList = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.status NOT IN ('RESOLVED', 'CLOSED') ORDER BY t.ticket_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ticketList.add(mapResultSetToTicket(rs));
            }
        }

        return ticketList;
    }

    /**
     * Retrieves all tickets assigned to a specific technician.
     */
    public List<Ticket> getTicketsByTechnician(int technicianId) throws SQLException {
        List<Ticket> ticketList = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.assigned_to = ? ORDER BY t.ticket_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, technicianId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ticketList.add(mapResultSetToTicket(rs));
                }
            }
        }

        return ticketList;
    }

    /**
     * Filters tickets by Priority and/or Status (Admin reporting).
     */
    public List<Ticket> getTicketsByPriorityAndStatus(String priority, String status) throws SQLException {
        List<Ticket> ticketList = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        List<String> conditions = new ArrayList<>();

        if (priority != null && !priority.isEmpty() && !"ALL".equalsIgnoreCase(priority)) {
            conditions.add("t.priority = ?");
        }
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            conditions.add("t.status = ?");
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }
        sql.append("ORDER BY t.ticket_id ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (priority != null && !priority.isEmpty() && !"ALL".equalsIgnoreCase(priority)) {
                ps.setString(paramIndex++, priority);
            }
            if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                ps.setString(paramIndex++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ticketList.add(mapResultSetToTicket(rs));
                }
            }
        }

        return ticketList;
    }

    /**
     * Deletes a ticket ONLY if its status is still 'OPEN'.
     */
    public int deleteTicketIfOpen(int ticketId) throws SQLException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            return 0; // Not found
        }

        if (!"OPEN".equalsIgnoreCase(ticket.getStatus())) {
            return -1; // Not in OPEN status
        }

        String sql = "DELETE FROM tickets WHERE ticket_id = ? AND status = 'OPEN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0 ? 1 : -1;
        }
    }

    /**
     * Checks if a user ID exists in the database and matches the expected role.
     */
    public boolean isValidUser(int userId, String expectedRole) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ? AND role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, expectedRole);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Helper method to map a single row from ResultSet to a Ticket object.
     */
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        int ticketId = rs.getInt("ticket_id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String category = rs.getString("category");
        String priority = rs.getString("priority");
        String status = rs.getString("status");
        int employeeId = rs.getInt("employee_id");

        int techId = rs.getInt("assigned_to");
        Integer assignedTo = rs.wasNull() ? null : techId;

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        Ticket ticket = new Ticket(ticketId, title, description, category, priority, status,
                                   employeeId, assignedTo, createdAt, updatedAt);

        ticket.setEmployeeName(rs.getString("employee_name"));
        ticket.setTechnicianName(rs.getString("technician_name"));

        return ticket;
    }
}
