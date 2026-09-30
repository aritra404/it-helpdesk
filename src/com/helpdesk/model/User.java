package com.helpdesk.model;

/**
 * User represents an authenticated system user (Employee, Technician, or Admin).
 * Mirrors the 'users' table in the MySQL database.
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

    @Override
    public String toString() {
        return String.format("User [ID=%d, Username=%s, Name=%s, Role=%s, Email=%s]",
                userId, username, name, role, email);
    }
}
