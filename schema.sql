-- ==========================================================
-- IT Helpdesk & Incident Management System Database Schema
-- Database Name: helpdesk_db
-- ==========================================================

CREATE DATABASE IF NOT EXISTS helpdesk_db;
USE helpdesk_db;

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS ticket_comments;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS users;

-- 1. Users Table (Stores Employees, Technicians, and Administrators)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('EMPLOYEE', 'TECHNICIAN', 'ADMIN') NOT NULL
);

-- 2. Tickets Table
CREATE TABLE tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    employee_id INT NOT NULL,
    assigned_to INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_tickets_employee FOREIGN KEY (employee_id) REFERENCES users(user_id),
    CONSTRAINT fk_tickets_technician FOREIGN KEY (assigned_to) REFERENCES users(user_id)
);

-- 3. Ticket Comments Table
CREATE TABLE ticket_comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    user_id INT NOT NULL,
    comment_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_comments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ==========================================================
-- 4. Initial Seed Data (Indian Names + PBKDF2 Password Hashes)
-- ==========================================================

-- Seed Users:
-- Admin:        username="aritra", password="aritra123" -> Aritra Seal (System Administrator)
-- Employee 1:   username="rohan",  password="rohan123"  -> Rohan Sharma
-- Employee 2:   username="priya",  password="priya123"  -> Priya Patel
-- Employee 3:   username="ananya", password="ananya123" -> Ananya Sen
-- Technician 1: username="rahul",  password="rahul123"  -> Rahul Verma (Tech Lead)
-- Technician 2: username="sneha",  password="sneha123"  -> Sneha Mukherjee (Network Specialist)

INSERT INTO users (user_id, username, password_hash, salt, name, email, role) VALUES
(1, 'aritra', 'KN5b/nqL8FcxpzJsZI9AHw==', 'iE/MwuUQVMk8WB2vxKv6jQ==', 'Aritra Seal (System Administrator)', 'aritra.seal@company.com', 'ADMIN'),
(2, 'rohan',  'zQxMzxGRxQ1w9Jclxgho4A==', 'kNLNwtQr1I7iHMXVKtKZUw==', 'Rohan Sharma',                       'rohan.sharma@company.com', 'EMPLOYEE'),
(3, 'priya',  'jStfZ3qFVqHECgvw1uDlMA==', 'B+WxklQdNzeHhZnE7Wh+KQ==', 'Priya Patel',                        'priya.patel@company.com',  'EMPLOYEE'),
(4, 'ananya', 'K+BaoID8JkI8m/OF2I0M2A==', '2UlFP0hKYwJUMAe61C9odg==', 'Ananya Sen',                         'ananya.sen@company.com',   'EMPLOYEE'),
(5, 'rahul',  '3ONwWt5IgEp+h8JcPstclA==', 'ah+NvfREmjr/ufsf+Ek4nw==', 'Rahul Verma (Tech Lead)',            'rahul.tech@company.com',   'TECHNICIAN'),
(6, 'sneha',  'y7dC+mDcYkGdEih1W4T9cw==', 'vaQFkoL75O1mUXz18klyAQ==', 'Sneha Mukherjee (Network Lead)',    'sneha.tech@company.com',   'TECHNICIAN');

-- Sample Initial Tickets
INSERT INTO tickets (ticket_id, title, description, category, priority, status, employee_id, assigned_to) VALUES
('1', 'Laptop battery discharging rapidly', 'Dell Latitude battery drains from 100% to 10% in 20 minutes.', 'Hardware', 'HIGH', 'OPEN', 2, NULL),
('2', 'VPN connection timeout from Bangalore office', 'Unable to reach internal staging server via VPN gateway.', 'Network', 'CRITICAL', 'ASSIGNED', 3, 5),
('3', 'IntelliJ IDEA License Renewal', 'Need annual license activation for Java backend development project.', 'Software', 'LOW', 'RESOLVED', 4, 5);

-- Sample Comments on Ticket #2
INSERT INTO ticket_comments (ticket_id, user_id, comment_text) VALUES
(2, 5, 'Rahul Verma: Checking Bangalore firewall and VPN tunnel NAT routing.'),
(2, 3, 'Priya Patel: Thank you Rahul, keeping team posted.');
