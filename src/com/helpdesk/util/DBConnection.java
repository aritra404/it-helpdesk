package com.helpdesk.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection provides a centralized method to establish a connection
 * to the MySQL helpdesk_db database using JDBC.
 */
public class DBConnection {

    // Explicitly load the MySQL JDBC driver for servlet containers (Tomcat)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("[DBConnection] MySQL JDBC Driver not found in classpath: " + ex.getMessage());
            }
        }
    }

    // Default local configuration
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/helpdesk_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Aritra1209#";

    // Private constructor prevents creating objects of this utility class
    private DBConnection() {
    }

    /**
     * Creates and returns a new active Connection to the MySQL database.
     * Automatically uses environment variables (DB_URL, DB_USER, DB_PASSWORD) if deployed to the cloud,
     * or falls back to local MySQL for development and terminal use.
     * 
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");

        String url = (envUrl != null && !envUrl.trim().isEmpty()) ? envUrl : DEFAULT_URL;
        String user = (envUser != null && !envUser.trim().isEmpty()) ? envUser : DEFAULT_USER;
        String password = (envPassword != null) ? envPassword : DEFAULT_PASSWORD;

        return DriverManager.getConnection(url, user, password);
    }
}
