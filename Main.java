import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for IT Helpdesk & Incident Management System.
 * Features an intuitive Role-Selection flow with automatic User-ID resolution
 * for EMPLOYEE, TECHNICIAN, and ADMINISTRATOR roles.
 */
public class Main {

    private static final UserDAO userDAO = new UserDAO();
    private static final TicketDAO ticketDAO = new TicketDAO();
    private static final TicketCommentDAO commentDAO = new TicketCommentDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("      IT HELPDESK & INCIDENT MANAGEMENT SYSTEM             ");
        System.out.println("============================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n----------------- SELECT YOUR ROLE ------------------------");
            System.out.println(" Who are you?");
            System.out.println(" 1. Employee (Report & track IT problems)");
            System.out.println(" 2. Technician (Diagnose & resolve assigned tickets)");
            System.out.println(" 3. Administrator (Manage tickets, users & reports)");
            System.out.println(" 4. Exit Application");
            System.out.println("-----------------------------------------------------------");

            int choice = readInt("Select an option (1-4): ");

            try {
                switch (choice) {
                    case 1:
                        handleRoleLogin("EMPLOYEE", "Employee");
                        break;
                    case 2:
                        handleRoleLogin("TECHNICIAN", "Technician");
                        break;
                    case 3:
                        handleRoleLogin("ADMIN", "Administrator");
                        break;
                    case 4:
                        System.out.println("\nThank you for using the IT Helpdesk System. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("[!] Invalid option. Please enter a number between 1 and 4.");
                }
            } catch (SQLException e) {
                System.out.println("\n[ERROR] Database operation failed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n[ERROR] An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // ==========================================================
    // Role-Based ID Selection & Auto-Login
    // ==========================================================

    private static void handleRoleLogin(String roleCode, String roleDisplayName) throws SQLException {
        System.out.println("\n============================================================");
        System.out.println("                " + roleDisplayName.toUpperCase() + " LOGIN                ");
        System.out.println("============================================================");

        List<User> users = userDAO.getUsersByRole(roleCode);
        if (users.isEmpty()) {
            System.out.println("No registered users found with the role: " + roleDisplayName);
            return;
        }

        System.out.println("Available " + roleDisplayName + " Profiles in Database:");
        for (User u : users) {
            System.out.println(String.format("  -> ID %d: %-30s (Username: %s | %s)",
                u.getUserId(), u.getName(), u.getUsername(), u.getEmail()));
        }
        System.out.println("------------------------------------------------------------");

        while (true) {
            int selectedId = readInt("Enter your " + roleDisplayName + " ID (or 0 to cancel): ");
            if (selectedId == 0) {
                System.out.println("Returning to role selection...");
                return;
            }

            User user = userDAO.getUserByIdAndRole(selectedId, roleCode);
            if (user != null) {
                System.out.println("\n============================================================");
                System.out.println(String.format(" WELCOME, %s! (Logged in as %s)", user.getName(), roleDisplayName));
                System.out.println("============================================================");

                if ("EMPLOYEE".equals(roleCode)) {
                    runEmployeeMenu(user);
                } else if ("TECHNICIAN".equals(roleCode)) {
                    runTechnicianMenu(user);
                } else if ("ADMIN".equals(roleCode)) {
                    runAdminMenu(user);
                }
                return; // Return to role selection after logout
            } else {
                System.out.println("[!] User ID #" + selectedId + " is not a valid " + roleDisplayName + ". Please choose from the list above.");
            }
        }
    }

    // ==========================================================
    // 1. EMPLOYEE MENU
    // ==========================================================

    private static void runEmployeeMenu(User employee) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=================== EMPLOYEE PORTAL ========================");
            System.out.println(" Logged in as: " + employee.getName() + " (Employee ID: " + employee.getUserId() + ")");
            System.out.println("------------------------------------------------------------");
            System.out.println(" 1. Create a new support ticket");
            System.out.println(" 2. View only my submitted tickets");
            System.out.println(" 3. View details of my ticket (including comments)");
            System.out.println(" 4. Add a comment to my ticket");
            System.out.println(" 5. Switch User / Logout");
            System.out.println("============================================================");

            int choice = readInt("Enter your choice (1-5): ");

            try {
                switch (choice) {
                    case 1:
                        employeeCreateTicket(employee);
                        break;
                    case 2:
                        employeeViewMyTickets(employee);
                        break;
                    case 3:
                        employeeViewTicketDetails(employee);
                        break;
                    case 4:
                        employeeAddComment(employee);
                        break;
                    case 5:
                        System.out.println("\nLogging out from Employee Portal...");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("[!] Invalid choice. Please choose 1-5.");
                }
            } catch (SQLException e) {
                System.out.println("\n[ERROR] Database operation failed: " + e.getMessage());
            }

            if (loggedIn) {
                System.out.print("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void employeeCreateTicket(User employee) throws SQLException {
        System.out.println("\n--- Submit a New Support Ticket ---");
        String title = readNonEmptyString("Enter Issue Title: ");
        String description = readNonEmptyString("Enter Detailed Description: ");
        String category = readNonEmptyString("Enter Category (Hardware / Network / Software / Access / Other): ");
        String priority = readValidPriority();

        // Automatically bound to the logged-in employee ID!
        Ticket ticket = new Ticket(title, description, category, priority, employee.getUserId());
        int ticketId = ticketDAO.createTicket(ticket);

        System.out.println("\n[SUCCESS] Your ticket has been submitted! Ticket ID: #" + ticketId);
    }

    private static void employeeViewMyTickets(User employee) throws SQLException {
        System.out.println("\n--- My Submitted Tickets ---");
        List<Ticket> tickets = ticketDAO.getTicketsByEmployee(employee.getUserId());
        if (tickets.isEmpty()) {
            System.out.println("You have not submitted any support tickets yet.");
            return;
        }
        System.out.println("Total Tickets: " + tickets.size() + "\n");
        for (Ticket t : tickets) {
            System.out.println(t);
        }
    }

    private static void employeeViewTicketDetails(User employee) throws SQLException {
        System.out.println("\n--- View My Ticket Details ---");
        int ticketId = readInt("Enter Ticket ID: ");

        Ticket ticket = ticketDAO.getTicketForEmployee(ticketId, employee.getUserId());
        if (ticket == null) {
            System.out.println("[!] Access Denied or Ticket #" + ticketId + " does not exist.");
            System.out.println("    (You can only view tickets submitted under your Employee ID).");
            return;
        }

        System.out.println(ticket);
        displayComments(ticketId);
    }

    private static void employeeAddComment(User employee) throws SQLException {
        System.out.println("\n--- Add Comment to My Ticket ---");
        int ticketId = readInt("Enter Ticket ID: ");

        if (!ticketDAO.isTicketOwnedBy(ticketId, employee.getUserId())) {
            System.out.println("[!] Access Denied: You are not authorized to comment on Ticket #" + ticketId);
            return;
        }

        String commentText = readNonEmptyString("Enter your comment / update: ");
        int commentId = commentDAO.addComment(ticketId, employee.getUserId(), commentText);
        System.out.println("\n[SUCCESS] Comment added successfully (Comment ID: #" + commentId + ")");
    }

    // ==========================================================
    // 2. TECHNICIAN MENU
    // ==========================================================

    private static void runTechnicianMenu(User technician) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=================== TECHNICIAN PORTAL ======================");
            System.out.println(" Logged in as: " + technician.getName() + " (Tech ID: " + technician.getUserId() + ")");
            System.out.println("------------------------------------------------------------");
            System.out.println(" 1. View tickets assigned to me");
            System.out.println(" 2. View details of an assigned ticket");
            System.out.println(" 3. Update status of my assigned ticket");
            System.out.println(" 4. Add troubleshooting diagnosis note to a ticket");
            System.out.println(" 5. Switch User / Logout");
            System.out.println("============================================================");

            int choice = readInt("Enter your choice (1-5): ");

            try {
                switch (choice) {
                    case 1:
                        techViewAssignedTickets(technician);
                        break;
                    case 2:
                        techViewTicketDetails(technician);
                        break;
                    case 3:
                        techUpdateStatus(technician);
                        break;
                    case 4:
                        techAddNote(technician);
                        break;
                    case 5:
                        System.out.println("\nLogging out from Technician Portal...");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("[!] Invalid choice. Please choose 1-5.");
                }
            } catch (SQLException e) {
                System.out.println("\n[ERROR] Database operation failed: " + e.getMessage());
            }

            if (loggedIn) {
                System.out.print("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void techViewAssignedTickets(User technician) throws SQLException {
        System.out.println("\n--- Tickets Assigned to Me ---");
        List<Ticket> tickets = ticketDAO.getTicketsByTechnician(technician.getUserId());
        if (tickets.isEmpty()) {
            System.out.println("No tickets are currently assigned to you.");
            return;
        }
        System.out.println("Assigned Tickets: " + tickets.size() + "\n");
        for (Ticket t : tickets) {
            System.out.println(t);
        }
    }

    private static void techViewTicketDetails(User technician) throws SQLException {
        System.out.println("\n--- View Assigned Ticket Details ---");
        int ticketId = readInt("Enter Ticket ID: ");

        Ticket ticket = ticketDAO.getTicketForTechnician(ticketId, technician.getUserId());
        if (ticket == null) {
            System.out.println("[!] Access Denied: Ticket #" + ticketId + " is not assigned to you.");
            return;
        }

        System.out.println(ticket);
        displayComments(ticketId);
    }

    private static void techUpdateStatus(User technician) throws SQLException {
        System.out.println("\n--- Update Ticket Status ---");
        int ticketId = readInt("Enter Ticket ID: ");

        if (!ticketDAO.isTicketAssignedTo(ticketId, technician.getUserId())) {
            System.out.println("[!] Access Denied: You cannot modify tickets not assigned to you.");
            return;
        }

        String newStatus = readValidStatus();
        boolean updated = ticketDAO.updateStatusForTechnician(ticketId, technician.getUserId(), newStatus);
        if (updated) {
            System.out.println("\n[SUCCESS] Status for Ticket #" + ticketId + " updated to " + newStatus + "!");
        } else {
            System.out.println("\n[!] Could not update status.");
        }
    }

    private static void techAddNote(User technician) throws SQLException {
        System.out.println("\n--- Add Troubleshooting Diagnosis Note ---");
        int ticketId = readInt("Enter Ticket ID: ");

        if (!ticketDAO.isTicketAssignedTo(ticketId, technician.getUserId())) {
            System.out.println("[!] Access Denied: You can only add troubleshooting notes to your assigned tickets.");
            return;
        }

        String note = readNonEmptyString("Enter Troubleshooting / Resolution Note: ");
        int commentId = commentDAO.addComment(ticketId, technician.getUserId(), note);
        System.out.println("\n[SUCCESS] Troubleshooting note logged (Note ID: #" + commentId + ")");
    }

    // ==========================================================
    // 3. ADMINISTRATOR MENU
    // ==========================================================

    private static void runAdminMenu(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n================== ADMINISTRATOR PORTAL ====================");
            System.out.println(" Logged in as: " + admin.getName() + " [ADMIN ID: " + admin.getUserId() + "]");
            System.out.println("------------------------------------------------------------");
            System.out.println(" 1. View all tickets in system");
            System.out.println(" 2. View ticket details by ID (with full comment history)");
            System.out.println(" 3. Assign a ticket to a technician");
            System.out.println(" 4. Update ticket details (Title, Description, Category, Priority)");
            System.out.println(" 5. Update ticket status (OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED)");
            System.out.println(" 6. Filter tickets by Priority and Status");
            System.out.println(" 7. Create a new user account (EMPLOYEE or TECHNICIAN)");
            System.out.println(" 8. View all registered users");
            System.out.println(" 9. View summary reports (Unresolved & Tech Workloads)");
            System.out.println(" 10. Delete a ticket (Allowed only if OPEN)");
            System.out.println(" 11. Switch User / Logout");
            System.out.println("============================================================");

            int choice = readInt("Enter your choice (1-11): ");

            try {
                switch (choice) {
                    case 1:
                        adminViewAllTickets();
                        break;
                    case 2:
                        adminViewTicketById();
                        break;
                    case 3:
                        adminAssignTechnician();
                        break;
                    case 4:
                        adminUpdateTicketDetails();
                        break;
                    case 5:
                        adminUpdateStatus();
                        break;
                    case 6:
                        adminFilterTickets();
                        break;
                    case 7:
                        adminCreateUser();
                        break;
                    case 8:
                        adminViewAllUsers();
                        break;
                    case 9:
                        adminViewReports();
                        break;
                    case 10:
                        adminDeleteTicket();
                        break;
                    case 11:
                        System.out.println("\nLogging out from Administrator Portal...");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("[!] Invalid choice. Please choose 1-11.");
                }
            } catch (SQLException e) {
                System.out.println("\n[ERROR] Database operation failed: " + e.getMessage());
            }

            if (loggedIn) {
                System.out.print("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void adminViewAllTickets() throws SQLException {
        System.out.println("\n--- All Tickets in System ---");
        List<Ticket> tickets = ticketDAO.getAllTickets();
        if (tickets.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }
        System.out.println("Total Tickets: " + tickets.size() + "\n");
        for (Ticket t : tickets) {
            System.out.println(t);
        }
    }

    private static void adminViewTicketById() throws SQLException {
        System.out.println("\n--- View Ticket By ID ---");
        int ticketId = readInt("Enter Ticket ID: ");
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("[!] Ticket #" + ticketId + " does not exist.");
            return;
        }
        System.out.println(ticket);
        displayComments(ticketId);
    }

    private static void adminAssignTechnician() throws SQLException {
        System.out.println("\n--- Assign Ticket to Technician ---");
        int ticketId = readInt("Enter Ticket ID: ");
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("[!] Ticket #" + ticketId + " does not exist.");
            return;
        }

        System.out.println("\nAvailable Technicians in System:");
        List<User> techs = userDAO.getUsersByRole("TECHNICIAN");
        for (User u : techs) {
            System.out.println(String.format("  -> Tech ID %d: %s (%s)", u.getUserId(), u.getName(), u.getEmail()));
        }

        int techId;
        while (true) {
            techId = readInt("Enter Technician User ID: ");
            if (ticketDAO.isValidUser(techId, "TECHNICIAN")) {
                break;
            }
            System.out.println("[!] Invalid Technician ID. Please select one from the list above.");
        }

        boolean assigned = ticketDAO.assignTechnician(ticketId, techId);
        if (assigned) {
            System.out.println("\n[SUCCESS] Ticket #" + ticketId + " assigned to Technician ID #" + techId + "!");
        } else {
            System.out.println("\n[!] Failed to assign technician.");
        }
    }

    private static void adminUpdateTicketDetails() throws SQLException {
        System.out.println("\n--- Update Ticket Details ---");
        int ticketId = readInt("Enter Ticket ID to update: ");
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("[!] Ticket #" + ticketId + " does not exist.");
            return;
        }

        System.out.println("Current Title: " + ticket.getTitle());
        String title = readNonEmptyString("Enter New Title: ");

        System.out.println("Current Description: " + ticket.getDescription());
        String desc = readNonEmptyString("Enter New Description: ");

        System.out.println("Current Category: " + ticket.getCategory());
        String category = readNonEmptyString("Enter New Category: ");

        System.out.println("Current Priority: " + ticket.getPriority());
        String priority = readValidPriority();

        boolean updated = ticketDAO.updateTicketDetails(ticketId, title, desc, category, priority);
        if (updated) {
            System.out.println("\n[SUCCESS] Ticket #" + ticketId + " updated successfully!");
        } else {
            System.out.println("\n[!] Failed to update ticket.");
        }
    }

    private static void adminUpdateStatus() throws SQLException {
        System.out.println("\n--- Update Ticket Status ---");
        int ticketId = readInt("Enter Ticket ID: ");
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("[!] Ticket #" + ticketId + " does not exist.");
            return;
        }

        System.out.println("Current Status: " + ticket.getStatus());
        String newStatus = readValidStatus();

        boolean updated = ticketDAO.updateTicketStatus(ticketId, newStatus);
        if (updated) {
            System.out.println("\n[SUCCESS] Ticket #" + ticketId + " status changed to " + newStatus + "!");
        } else {
            System.out.println("\n[!] Failed to update status.");
        }
    }

    private static void adminFilterTickets() throws SQLException {
        System.out.println("\n--- Filter Tickets by Priority and Status ---");
        System.out.print("Enter Priority (LOW / MEDIUM / HIGH / CRITICAL or 'ALL'): ");
        String priority = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Status (OPEN / ASSIGNED / IN_PROGRESS / RESOLVED / CLOSED or 'ALL'): ");
        String status = scanner.nextLine().trim().toUpperCase();

        List<Ticket> filtered = ticketDAO.getTicketsByPriorityAndStatus(priority, status);
        if (filtered.isEmpty()) {
            System.out.println("\nNo tickets match the specified criteria.");
            return;
        }

        System.out.println("\nFound " + filtered.size() + " matching ticket(s):\n");
        for (Ticket t : filtered) {
            System.out.println(t);
        }
    }

    private static void adminCreateUser() throws SQLException {
        System.out.println("\n--- Create New User Account ---");
        String username = readNonEmptyString("Enter Unique Username: ");
        String password = readNonEmptyString("Enter Initial Password: ");
        String name = readNonEmptyString("Enter Full Name: ");
        String email = readNonEmptyString("Enter Unique Email: ");

        String role;
        while (true) {
            System.out.print("Enter Role (EMPLOYEE / TECHNICIAN): ");
            role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("EMPLOYEE") || role.equals("TECHNICIAN")) {
                break;
            }
            System.out.println("[!] Role must be either EMPLOYEE or TECHNICIAN.");
        }

        int newUserId = userDAO.createUser(username, password, name, email, role);
        System.out.println("\n[SUCCESS] User account created successfully! User ID: #" + newUserId);
        System.out.println("Username: " + username + " | Role: " + role);
    }

    private static void adminViewAllUsers() throws SQLException {
        System.out.println("\n--- Registered Users in System ---");
        List<User> users = userDAO.getAllUsers();
        System.out.println(String.format("%-8s | %-15s | %-30s | %-12s | %s", "USER ID", "USERNAME", "NAME", "ROLE", "EMAIL"));
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (User u : users) {
            System.out.println(String.format("%-8d | %-15s | %-30s | %-12s | %s",
                u.getUserId(), u.getUsername(), u.getName(), u.getRole(), u.getEmail()));
        }
    }

    private static void adminViewReports() throws SQLException {
        System.out.println("\n==================== SYSTEM SUMMARY REPORTS ====================");
        
        List<Ticket> unresolved = ticketDAO.getUnresolvedTickets();
        System.out.println(" 1. Unresolved Tickets Backlog: " + unresolved.size() + " ticket(s)");
        for (Ticket t : unresolved) {
            System.out.println(String.format("    - #%d [%s] Status: %-12s | %s",
                t.getTicketId(), t.getPriority(), t.getStatus(), t.getTitle()));
        }

        System.out.println("\n 2. Technician Workload Breakdown:");
        List<User> techs = userDAO.getUsersByRole("TECHNICIAN");
        for (User tech : techs) {
            List<Ticket> techTickets = ticketDAO.getTicketsByTechnician(tech.getUserId());
            System.out.println(String.format("    - Tech #%d (%s): %d assigned ticket(s)",
                tech.getUserId(), tech.getName(), techTickets.size()));
        }
        System.out.println("================================================================");
    }

    private static void adminDeleteTicket() throws SQLException {
        System.out.println("\n--- Delete Ticket (OPEN Tickets Only) ---");
        int ticketId = readInt("Enter Ticket ID to delete: ");

        int result = ticketDAO.deleteTicketIfOpen(ticketId);
        if (result == 1) {
            System.out.println("\n[SUCCESS] Ticket #" + ticketId + " deleted successfully.");
        } else if (result == 0) {
            System.out.println("\n[!] Ticket #" + ticketId + " does not exist.");
        } else if (result == -1) {
            System.out.println("\n[!] CANNOT DELETE: Ticket #" + ticketId + " is not in 'OPEN' status.");
            System.out.println("    (Only freshly opened tickets can be deleted to maintain audit trail).");
        }
    }

    // ==========================================================
    // Helper Methods
    // ==========================================================

    private static void displayComments(int ticketId) throws SQLException {
        List<TicketComment> comments = commentDAO.getCommentsByTicketId(ticketId);
        System.out.println("\n Comments & Troubleshooting History (" + comments.size() + "):");
        if (comments.isEmpty()) {
            System.out.println("   (No comments recorded yet)");
        } else {
            for (TicketComment c : comments) {
                System.out.println(c);
            }
        }
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[!] Input cannot be empty. Please enter a valid value.");
        }
    }

    private static String readValidPriority() {
        while (true) {
            System.out.print("Enter Priority (LOW / MEDIUM / HIGH / CRITICAL): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("LOW") || input.equals("MEDIUM") || input.equals("HIGH") || input.equals("CRITICAL")) {
                return input;
            }
            System.out.println("[!] Invalid priority. Allowed values: LOW, MEDIUM, HIGH, CRITICAL");
        }
    }

    private static String readValidStatus() {
        while (true) {
            System.out.print("Enter New Status (OPEN / ASSIGNED / IN_PROGRESS / RESOLVED / CLOSED): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("OPEN") || input.equals("ASSIGNED") || input.equals("IN_PROGRESS") ||
                input.equals("RESOLVED") || input.equals("CLOSED")) {
                return input;
            }
            System.out.println("[!] Invalid status. Allowed values: OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid number format. Please enter a valid integer.");
            }
        }
    }
}
