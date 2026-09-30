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
 * TicketServlet displays detailed incident views and chronological discussion threads,
 * and processes troubleshooting comments with role-based authorization checks.
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/ticket"})
public class TicketServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final TicketCommentDAO commentDAO = new TicketCommentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            redirectRoleHome(request, response, user, "Invalid or missing Ticket ID.");
            return;
        }

        try {
            int ticketId = Integer.parseInt(idStr);
            Ticket ticket = ticketDAO.getTicketById(ticketId);

            if (ticket == null) {
                redirectRoleHome(request, response, user, "Ticket #" + ticketId + " not found.");
                return;
            }

            // Role-based authorization check
            String role = user.getRole().toUpperCase();
            if ("EMPLOYEE".equals(role)) {
                if (ticket.getEmployeeId() != user.getUserId()) {
                    redirectRoleHome(request, response, user, "Access Denied: You can only view your own tickets.");
                    return;
                }
            } else if ("TECHNICIAN".equals(role)) {
                if (ticket.getAssignedTo() == null || ticket.getAssignedTo() != user.getUserId()) {
                    redirectRoleHome(request, response, user, "Access Denied: Ticket is not assigned to your technician queue.");
                    return;
                }
            }

            List<TicketComment> comments = commentDAO.getCommentsByTicketId(ticketId);

            request.setAttribute("ticket", ticket);
            request.setAttribute("comments", comments);
            request.getRequestDispatcher("/ticket-details.jsp").forward(request, response);

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            redirectRoleHome(request, response, user, "Error loading ticket: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String ticketIdStr = request.getParameter("ticketId");
        String commentText = request.getParameter("commentText");

        if (ticketIdStr == null || commentText == null || commentText.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ticket?id=" + ticketIdStr + "&error=Comment+cannot+be+empty.");
            return;
        }

        try {
            int ticketId = Integer.parseInt(ticketIdStr);
            Ticket ticket = ticketDAO.getTicketById(ticketId);

            if (ticket == null) {
                redirectRoleHome(request, response, user, "Ticket not found.");
                return;
            }

            // Verify comment authorization
            String role = user.getRole().toUpperCase();
            if ("EMPLOYEE".equals(role) && ticket.getEmployeeId() != user.getUserId()) {
                redirectRoleHome(request, response, user, "Access Denied.");
                return;
            }
            if ("TECHNICIAN".equals(role) && (ticket.getAssignedTo() == null || ticket.getAssignedTo() != user.getUserId())) {
                redirectRoleHome(request, response, user, "Access Denied.");
                return;
            }

            commentDAO.addComment(ticketId, user.getUserId(), commentText.trim());
            response.sendRedirect(request.getContextPath() + "/ticket?id=" + ticketId + "&success=Comment+added+successfully.");

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ticket?id=" + ticketIdStr + "&error=" + e.getMessage());
        }
    }

    private void redirectRoleHome(HttpServletRequest request, HttpServletResponse response, User user, String errorMsg)
            throws IOException {
        String dest = "/index.jsp";
        if (user != null) {
            String role = user.getRole().toUpperCase();
            if ("ADMIN".equals(role)) dest = "/admin";
            else if ("TECHNICIAN".equals(role)) dest = "/technician";
            else dest = "/employee";
        }
        response.sendRedirect(request.getContextPath() + dest + "?error=" + (errorMsg != null ? errorMsg.replace(" ", "+") : ""));
    }
}
