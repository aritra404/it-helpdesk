# IT Helpdesk & Incident Management System

A dual-interface **IT Helpdesk & Incident Management System** built with **Core Java, JDBC, MySQL, Java Servlets, JSP, HTML5, and CSS3**.

Both the **Interactive Terminal Application** and the **Modern Browser-Based Web Application** operate concurrently and independently over the same MySQL database schema and shared business logic.

---

## 🚀 Key Features

### 👤 1. Employee Workspace
- **Raise Tickets**: Submit support requests with categorized technical details (Hardware, Software, Network, Access, Other) and urgency priority levels (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
- **Real-Time Tracking**: View status updates (`OPEN`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`) and see assigned technicians.
- **Discussion Thread**: Post and view comments and troubleshooting updates chronologically.

### 🛠️ 2. Technician Workbench
- **Assigned Queue**: View incidents assigned specifically to the logged-in technician.
- **Status Management**: Transition ticket states (`ASSIGNED` ➔ `IN_PROGRESS` ➔ `RESOLVED` ➔ `CLOSED`).
- **Diagnosis Logging**: Post diagnosis findings and troubleshooting notes directly onto the incident thread.

### 🛡️ 3. Administrator Control Center
- **Triage & Assignment**: View all organization-wide tickets and dispatch them to available technicians.
- **Lifecycle Management**: Change statuses and safely delete `OPEN` unassigned tickets.
- **User Account Management**: Create and manage Employee, Technician, and Admin accounts with salted **PBKDF2** password hashing.
- **Backlog & Priority Filtering**: Slice system tickets by status and priority with real-time KPI metrics.

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

> 💡 *On the Web Application login screen, you can also click the top-right **"Demo Credentials"** button to auto-fill any of these accounts with a single click!*

---

## 📂 Project Architecture

```
JAVA_PROJECT/
├── lib/
│   ├── mysql-connector-j-8.3.0.jar
│   ├── javax.servlet-api-4.0.1.jar
│   └── jstl-1.2.jar
├── webapp/
│   ├── css/
│   │   └── style.css                 # Modern Navy/Indigo design system & responsive layout
│   ├── WEB-INF/
│   │   ├── classes/                  # Compiled bytecode for servlet container
│   │   ├── lib/                      # Runtime dependencies
│   │   └── web.xml                   # Deployment descriptor & servlet routes
│   ├── index.jsp                     # "Who are you?" 3-card role selection landing page
│   ├── login.jsp                     # Role login form + Demo Credentials modal
│   ├── employee-dashboard.jsp        # Employee KPI cards, ticket table, and raise ticket modal
│   ├── technician-dashboard.jsp      # Technician assigned queue and status update modal
│   ├── admin-dashboard.jsp           # Admin dispatching, filters, and user management
│   └── ticket-details.jsp            # Incident metadata and chronological discussion thread
├── DBConnection.java                 # JDBC connection manager
├── PasswordUtil.java                 # Salted PBKDF2WithHmacSHA256 password security
├── User.java / UserDAO.java          # User entity and database access operations
├── Ticket.java / TicketDAO.java      # Ticket entity and CRUD queries with auth checks
├── TicketComment.java / TicketCommentDAO.java # Discussion comments DAO
├── Main.java                         # Interactive terminal application
├── LoginServlet.java                 # Role verification and session management
├── LogoutServlet.java                # Session invalidation
├── EmployeeServlet.java              # Employee portal controller
├── TechnicianServlet.java            # Technician portal controller
├── AdminServlet.java                 # Admin portal controller
├── TicketServlet.java                # Ticket details & comments controller
├── schema.sql                        # MySQL DDL and seeded Indian user data
├── run_terminal.bat                  # One-click terminal app runner
├── package_app.bat                   # One-click WAR build and packaging script
└── helpdesk.war                      # Production-ready web archive
```

---

## 💻 How to Run

### Option 1: Running the Terminal Application
Double-click [run_terminal.bat](file:///c:/Users/aritr/OneDrive/Desktop/JAVA_PROJECT/run_terminal.bat) or run in PowerShell/CMD:
```bash
javac -cp ".;lib/*" *.java
java -cp ".;lib/*" Main
```

### Option 2: Running the Web Application
1. Run [package_app.bat](file:///c:/Users/aritr/OneDrive/Desktop/JAVA_PROJECT/package_app.bat) to produce `helpdesk.war`:
   ```bash
   cmd /c package_app.bat
   ```
2. Copy `helpdesk.war` into the `webapps/` folder of your **Apache Tomcat** installation (e.g. `apache-tomcat-9.x/webapps/` or Tomcat 10 with Jakarta migration).
3. Start Tomcat (`bin/startup.bat`).
4. Open your browser and navigate to:
   ```
   http://localhost:8080/helpdesk/
   ```
5. Choose your role, use the **"Demo Credentials"** modal to sign in with 1 click, and explore!
