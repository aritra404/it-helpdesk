# IT Helpdesk & Incident Management System

A dual-interface **IT Helpdesk & Incident Management System** built with **Core Java, JDBC, MySQL, Java Servlets, JSP, HTML5, and CSS3**.

Both the **Interactive Terminal Application** and the **Modern Browser-Based Web Application** operate concurrently and independently over the same MySQL database schema and shared business logic.

---

## 📂 Enterprise Package Architecture

```
JAVA_PROJECT/
├── src/                                  # Java Source Packages
│   └── com/
│       └── helpdesk/
│           ├── Main.java                 # Interactive Terminal application entry point
│           ├── model/                    # Domain POJO entities
│           │   ├── User.java             # User entity (Employee, Technician, Admin)
│           │   ├── Ticket.java           # Incident Ticket model
│           │   └── TicketComment.java    # Threaded discussion comment model
│           ├── dao/                      # Database Access Objects (CRUD & Auth)
│           │   ├── UserDAO.java          # Authentication & user management
│           │   ├── TicketDAO.java        # Ticket lifecycle & role-based filtering
│           │   └── TicketCommentDAO.java # Incident discussion thread operations
│           ├── util/                     # Utilities & Database Connection
│           │   ├── DBConnection.java     # JDBC connection manager (Local + Cloud env vars)
│           │   └── PasswordUtil.java     # Salted PBKDF2WithHmacSHA256 password security
│           └── servlet/                  # HTTP Web Controllers
│               ├── LoginServlet.java     # Role verification & session management
│               ├── LogoutServlet.java    # Session termination
│               ├── EmployeeServlet.java  # Employee portal & ticket creation
│               ├── TechnicianServlet.java# Technician workbench & diagnosis logger
│               ├── AdminServlet.java     # Admin control center & ticket dispatching
│               └── TicketServlet.java    # Incident details & comments thread
├── webapp/                               # Web Application Frontend
│   ├── css/
│   │   └── style.css                     # Modern Navy/Indigo design system
│   ├── WEB-INF/
│   │   ├── classes/                      # Compiled bytecode
│   │   ├── lib/                          # Runtime JAR dependencies
│   │   └── web.xml                       # Deployment descriptor & servlet routes
│   ├── index.jsp                         # "Who are you?" 3-card role selection
│   ├── login.jsp                         # Sign-in form + Demo Credentials modal
│   ├── employee-dashboard.jsp            # Employee workspace & ticket creation modal
│   ├── technician-dashboard.jsp          # Technician queue & status update modal
│   ├── admin-dashboard.jsp               # Admin dispatching, filters & user management
│   └── ticket-details.jsp                # Incident details & discussion thread
├── lib/                                  # External Dependencies
│   ├── mysql-connector-j-8.3.0.jar
│   ├── javax.servlet-api-4.0.1.jar
│   └── jstl-1.2.jar
├── schema.sql                            # MySQL DDL & seeded Indian accounts
├── Dockerfile                            # Production cloud container configuration
├── package_app.bat                       # 1-Click WAR build & package script
├── run_terminal.bat                      # 1-Click Terminal application runner
└── helpdesk.war                          # Production web application archive
```

---

## 🔑 Pre-Seeded Indian Demo Accounts

| Role | Name | Username | Password | User ID |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | Aritra Seal | `aritra` | `aritra123` | **#1** |
| **EMPLOYEE** | Rohan Sharma | `rohan` | `rohan123` | **#2** |
| **EMPLOYEE** | Priya Patel | `priya` | `priya123` | **#3** |
| **EMPLOYEE** | Ananya Sen | `ananya` | `ananya123` | **#4** |
| **TECHNICIAN** | Rahul Verma | `rahul` | `rahul123` | **#5** |
| **TECHNICIAN** | Sneha Mukherjee | `sneha` | `sneha123` | **#6** |

---

## 💻 How to Run Locally

### 1. Terminal Application
Double-click [`run_terminal.bat`](file:///c:/Users/aritr/OneDrive/Desktop/JAVA_PROJECT/run_terminal.bat) or run:
```bash
javac -cp ".;lib/*" -d webapp/WEB-INF/classes src/com/helpdesk/util/*.java src/com/helpdesk/model/*.java src/com/helpdesk/dao/*.java src/com/helpdesk/servlet/*.java src/com/helpdesk/*.java
java -cp ".;webapp/WEB-INF/classes;lib/*" com.helpdesk.Main
```

### 2. Web Application (Apache Tomcat)
1. Double-click [`package_app.bat`](file:///c:/Users/aritr/OneDrive/Desktop/JAVA_PROJECT/package_app.bat) to generate `helpdesk.war`.
2. Copy `helpdesk.war` into your Apache Tomcat `webapps/` folder.
3. Start Tomcat and visit `http://localhost:8080/helpdesk/`.
