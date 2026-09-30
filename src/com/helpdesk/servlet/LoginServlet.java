package com.helpdesk.servlet;

import com.helpdesk.dao.UserDAO;
import com.helpdesk.model.User;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginServlet handles user authentication for the Helpdesk Web Application.
 * Validates credentials using UserDAO and assigns role-based sessions.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String role = request.getParameter("role");
        if (role == null || role.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp?role=" + role);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String requestedRole = request.getParameter("role");

        if (username != null) username = username.trim();
        if (requestedRole != null) requestedRole = requestedRole.trim().toLowerCase();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Please enter both username and password.");
            request.getRequestDispatcher("/login.jsp?role=" + (requestedRole != null ? requestedRole : "employee"))
                   .forward(request, response);
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);

            if (user == null) {
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("/login.jsp?role=" + (requestedRole != null ? requestedRole : "employee"))
                       .forward(request, response);
                return;
            }

            // Verify if the user's role matches the selected role
            if (requestedRole != null && !user.getRole().equalsIgnoreCase(requestedRole)) {
                request.setAttribute("error", "Role mismatch: User '" + username + "' is registered as " 
                        + user.getRole() + ", but attempted to sign in as " + requestedRole.toUpperCase() + ".");
                request.getRequestDispatcher("/login.jsp?role=" + requestedRole)
                       .forward(request, response);
                return;
            }

            // Create session and store user object
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            // Redirect based on verified role
            String userRole = user.getRole().toUpperCase();
            if ("ADMIN".equals(userRole)) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else if ("TECHNICIAN".equals(userRole)) {
                response.sendRedirect(request.getContextPath() + "/technician");
            } else {
                response.sendRedirect(request.getContextPath() + "/employee");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database connection error: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp?role=" + (requestedRole != null ? requestedRole : "employee"))
                   .forward(request, response);
        }
    }
}
