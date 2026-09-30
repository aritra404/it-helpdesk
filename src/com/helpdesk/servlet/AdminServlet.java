package com.helpdesk.servlet;

import com.helpdesk.dao.TicketDAO;
import com.helpdesk.dao.UserDAO;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AdminServlet manages Administrator features: organization-wide ticket oversight,
 * technician assignment, status management, open ticket deletion, and user administration.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?role=admin");
            return;
        }

        String filterPriority = request.getParameter("priority");
        String filterStatus = request.getParameter("status");
        String activeTab = request.getParameter("tab");

        if (filterPriority == null || filterPriority.trim().isEmpty()) filterPriority = "ALL";
        if (filterStatus == null || filterStatus.trim().isEmpty()) filterStatus = "ALL";
        if (activeTab == null || activeTab.trim().isEmpty()) activeTab = "tickets";

        try {
            List<Ticket> filteredTickets = ticketDAO.getTicketsByPriorityAndStatus(filterPriority, filterStatus);
            List<Ticket> allTickets = ticketDAO.getAllTickets();
            List<User> technicians = userDAO.getUsersByRole("TECHNICIAN");
            List<User> allUsers = userDAO.getAllUsers();

            int totalTickets = allTickets.size();
            int openTickets = 0;
            int inProgressTickets = 0;
            int resolvedTickets = 0;

            for (Ticket t : allTickets) {
                String s = t.getStatus().toUpperCase();
                if ("OPEN".equals(s) || "ASSIGNED".equals(s)) {
                    openTickets++;
                } else if ("IN_PROGRESS".equals(s)) {
                    inProgressTickets++;
                } else if ("RESOLVED".equals(s) || "CLOSED".equals(s)) {
                    resolvedTickets++;
                }
            }

            request.setAttribute("tickets", filteredTickets);
            request.setAttribute("technicians", technicians);
            request.setAttribute("allUsers", allUsers);
            request.setAttribute("totalTickets", totalTickets);
            request.setAttribute("openTickets", openTickets);
            request.setAttribute("inProgressTickets", inProgressTickets);
            request.setAttribute("resolvedTickets", resolvedTickets);
            request.setAttribute("filterPriority", filterPriority);
            request.setAttribute("filterStatus", filterStatus);
            request.setAttribute("activeTab", activeTab);

            request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error loading admin dashboard: " + e.getMessage());
            request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?role=admin");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "";

        try {
            switch (action.toLowerCase()) {
                case "assign": {
                    int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                    int techId = Integer.parseInt(request.getParameter("technicianId"));
                    boolean success = ticketDAO.assignTechnician(ticketId, techId);
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/admin?success=Technician+assigned+successfully+to+Ticket+%23" + ticketId);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin?error=Failed+to+assign+technician.");
                    }
                    break;
                }
                case "updatestatus": {
                    int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                    String newStatus = request.getParameter("newStatus");
                    boolean success = ticketDAO.updateTicketStatus(ticketId, newStatus.toUpperCase());
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/admin?success=Ticket+%23" + ticketId + "+status+updated+to+" + newStatus.toUpperCase());
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin?error=Failed+to+update+ticket+status.");
                    }
                    break;
                }
                case "delete": {
                    int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                    int result = ticketDAO.deleteTicketIfOpen(ticketId);
                    if (result == 1) {
                        response.sendRedirect(request.getContextPath() + "/admin?success=Ticket+%23" + ticketId + "+deleted+successfully.");
                    } else if (result == -1) {
                        response.sendRedirect(request.getContextPath() + "/admin?error=Deletion+rejected:+Only+tickets+with+OPEN+status+can+be+deleted.");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin?error=Ticket+%23" + ticketId + "+not+found.");
                    }
                    break;
                }
                case "createuser": {
                    String username = request.getParameter("username");
                    String rawPassword = request.getParameter("password");
                    String name = request.getParameter("name");
                    String email = request.getParameter("email");
                    String role = request.getParameter("role");

                    if (username == null || rawPassword == null || name == null || email == null || role == null
                            || username.trim().isEmpty() || rawPassword.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin?tab=users&error=All+fields+are+required+for+user+creation.");
                        return;
                    }

                    int newUserId = userDAO.createUser(username.trim(), rawPassword, name.trim(), email.trim(), role.trim().toUpperCase());
                    response.sendRedirect(request.getContextPath() + "/admin?tab=users&success=User+'" + username + "'+(ID+" + newUserId + ")+created+successfully.");
                    break;
                }
                default:
                    response.sendRedirect(request.getContextPath() + "/admin");
                    break;
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin?error=" + e.getMessage());
        }
    }
}
