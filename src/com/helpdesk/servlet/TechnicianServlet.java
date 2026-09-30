package com.helpdesk.servlet;

import com.helpdesk.dao.TicketCommentDAO;
import com.helpdesk.dao.TicketDAO;
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
 * TechnicianServlet manages the Technician Workbench, assigned ticket queues,
 * status updates, and logging diagnosis notes.
 */
@WebServlet(name = "TechnicianServlet", urlPatterns = {"/technician"})
public class TechnicianServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final TicketCommentDAO commentDAO = new TicketCommentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"TECHNICIAN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?role=technician");
            return;
        }

        try {
            List<Ticket> tickets = ticketDAO.getTicketsByTechnician(user.getUserId());

            int totalAssigned = tickets.size();
            int inProgressCount = 0;
            int resolvedCount = 0;
            int pendingCount = 0;

            for (Ticket t : tickets) {
                String status = t.getStatus().toUpperCase();
                if ("IN_PROGRESS".equals(status)) {
                    inProgressCount++;
                } else if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
                    resolvedCount++;
                } else {
                    pendingCount++;
                }
            }

            request.setAttribute("tickets", tickets);
            request.setAttribute("totalAssigned", totalAssigned);
            request.setAttribute("inProgressCount", inProgressCount);
            request.setAttribute("resolvedCount", resolvedCount);
            request.setAttribute("pendingCount", pendingCount);

            request.getRequestDispatcher("/technician-dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading assigned tickets: " + e.getMessage());
            request.getRequestDispatcher("/technician-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"TECHNICIAN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?role=technician");
            return;
        }

        String action = request.getParameter("action");
        if ("updateStatus".equalsIgnoreCase(action)) {
            String ticketIdStr = request.getParameter("ticketId");
            String newStatus = request.getParameter("newStatus");
            String notes = request.getParameter("notes");

            if (ticketIdStr == null || newStatus == null || newStatus.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/technician?error=Missing+parameters");
                return;
            }

            try {
                int ticketId = Integer.parseInt(ticketIdStr);
                boolean updated = ticketDAO.updateStatusForTechnician(ticketId, user.getUserId(), newStatus.toUpperCase());

                if (!updated) {
                    response.sendRedirect(request.getContextPath() + "/technician?error=Authorization+failed+or+ticket+not+assigned+to+you.");
                    return;
                }

                // If notes were provided, log them as a comment
                if (notes != null && !notes.trim().isEmpty()) {
                    commentDAO.addComment(ticketId, user.getUserId(), "[Status changed to " + newStatus.toUpperCase() + "] " + notes.trim());
                }

                response.sendRedirect(request.getContextPath() + "/technician?success=Ticket+%23" + ticketId + "+updated+to+" + newStatus.toUpperCase());

            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/technician?error=" + e.getMessage());
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/technician");
        }
    }
}
