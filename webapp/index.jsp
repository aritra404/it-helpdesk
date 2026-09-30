<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IT Helpdesk & Incident Management System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <header class="navbar">
        <a href="index.jsp" class="nav-brand">
            <span>💻 IT Helpdesk & Incident Portal</span>
            <span class="brand-badge">Enterprise</span>
        </a>
    </header>

    <!-- Main Content Container -->
    <main class="container">
        
        <div class="hero-section">
            <h1 class="hero-title">Who are you?</h1>
            <p class="hero-subtitle">Select your role to access your dedicated support workspace</p>
        </div>

        <!-- Role Selection Grid -->
        <div class="role-grid">
            
            <!-- 1. Employee Card -->
            <div class="role-card">
                <div>
                    <div class="role-icon-box">👤</div>
                    <h2 class="role-title">Employee</h2>
                    <p class="role-desc">
                        Raise support tickets for hardware, software, network, or access issues. Track ticket progress in real-time and communicate with technicians.
                    </p>
                </div>
                <a href="login.jsp?role=employee" class="btn btn-primary">Continue as Employee &rarr;</a>
            </div>

            <!-- 2. Technician Card -->
            <div class="role-card">
                <div>
                    <div class="role-icon-box" style="background: #fef3c7; color: #b45309;">🛠️</div>
                    <h2 class="role-title">Technician</h2>
                    <p class="role-desc">
                        View tickets assigned to your queue, diagnose technical incidents, log troubleshooting notes, and update ticket resolution status.
                    </p>
                </div>
                <a href="login.jsp?role=technician" class="btn btn-primary" style="background-color: #0284c7;">Continue as Technician &rarr;</a>
            </div>

            <!-- 3. Administrator Card -->
            <div class="role-card">
                <div>
                    <div class="role-icon-box" style="background: #fee2e2; color: #b91c1c;">🛡️</div>
                    <h2 class="role-title">Administrator</h2>
                    <p class="role-desc">
                        Manage organization-wide tickets, triage and assign workloads to technicians, create employee accounts, and view backlog reports.
                    </p>
                </div>
                <a href="login.jsp?role=admin" class="btn btn-primary" style="background-color: #1e293b;">Continue as Admin &rarr;</a>
            </div>

        </div>

    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>IT Helpdesk & Incident Management System &copy; 2026. Built with Java, Servlets, JSP & MySQL.</p>
    </footer>

</body>
</html>
