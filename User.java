/**
 * User represents an authenticated user in the Helpdesk System.
 * Stores user session state (ID, username, display name, email, role).
 */
public class User {

    private int userId;
    private String username;
    private String name;
    private String email;
    private String role; // EMPLOYEE, TECHNICIAN, ADMIN

    public User() {
    }

    public User(int userId, String username, String name, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equalsIgnoreCase(role);
    }

    public boolean isTechnician() {
        return "TECHNICIAN".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return String.format("User #%d [%s] - %s (%s, %s)", userId, role, name, username, email);
    }
}
