import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO handles all database operations for the 'users' table,
 * including authentication, ID lookup, and user management.
 */
public class UserDAO {

    /**
     * Authenticates a user with username and plain-text password using PBKDF2.
     */
    public User authenticate(String username, String rawPassword) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, salt, name, email, role " +
                     "FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String storedSalt = rs.getString("salt");

                    boolean matches = PasswordUtil.verifyPassword(rawPassword, storedHash, storedSalt);
                    if (matches) {
                        return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                        );
                    }
                }
            }
        }

        return null;
    }

    /**
     * Looks up an active user by their User ID and Role.
     */
    public User getUserByIdAndRole(int userId, String expectedRole) throws SQLException {
        String sql = "SELECT user_id, username, name, email, role FROM users WHERE user_id = ? AND role = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, expectedRole.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                }
            }
        }

        return null;
    }

    /**
     * Creates a new user with a salted PBKDF2 password hash.
     */
    public int createUser(String username, String rawPassword, String name, String email, String role) throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(rawPassword, salt);

        String sql = "INSERT INTO users (username, password_hash, salt, name, email, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, salt);
            ps.setString(4, name);
            ps.setString(5, email);
            ps.setString(6, role.toUpperCase());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves all registered users.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, username, name, email, role FROM users ORDER BY user_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                ));
            }
        }

        return userList;
    }

    /**
     * Retrieves all users with a specific role (e.g. all TECHNICIANs or all EMPLOYEEs).
     */
    public List<User> getUsersByRole(String role) throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, username, name, email, role FROM users WHERE role = ? ORDER BY user_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userList.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                    ));
                }
            }
        }

        return userList;
    }
}
