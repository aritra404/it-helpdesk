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
 * EmployeeServlet manages the Employee Dashboard, ticket creation,
 * and retrieval of tickets submitted by the authenticated employee.
 */
@WebServlet(name = "EmployeeServlet", urlPatterns = {"/employee"})
public class EmployeeServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?role=employee");
            return;
        }

        try {
            List<Ticket> tickets = ticketDAO.getTicketsByEmployee(user.getUserId());

            int totalCount = tickets.size();
            int openCount = 0;
            int inProgressCount = 0;
            int resolvedCount = 0;

            for (Ticket t : tickets) {
                String status = t.getStatus().toUpperCase();
                if ("OPEN".equals(status) || "ASSIGNED".equals(status)) {
                    openCount++;
                } else if ("IN_PROGRESS".equals(status)) {
                    inProgressCount++;
                } else if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
                    resolvedCount++;
                }
            }

            request.setAttribute("tickets", tickets);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("openCount", openCount);
            request.setAttribute("inProgressCount", inProgressCount);
            request.setAttribute("resolvedCount", resolvedCount);

            request.getRequestDispatcher("/employee-dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading tickets: " + e.getMessage());
            request.getRequestDispatcher("/employee-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?role=employee");
            return;
        }

        String action = request.getParameter("action");
        if ("create".equalsIgnoreCase(action)) {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String category = request.getParameter("category");
            String priority = request.getParameter("priority");

            if (title != null) title = title.trim();
            if (description != null) description = description.trim();

            if (title == null || title.isEmpty() || description == null || description.isEmpty()) {
                request.setAttribute("error", "Please provide both title and description.");
                doGet(request, response);
                return;
            }

            if (category == null || category.isEmpty()) category = "Other";
            if (priority == null || priority.isEmpty()) priority = "MEDIUM";

            try {
                Ticket newTicket = new Ticket(title, description, category, priority.toUpperCase(), user.getUserId());
                int newId = ticketDAO.createTicket(newTicket);
                response.sendRedirect(request.getContextPath() + "/employee?success=Ticket+%23" + newId + "+created+successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("error", "Failed to create ticket: " + e.getMessage());
                doGet(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/employee");
        }
    }
}
