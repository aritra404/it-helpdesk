<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String role = request.getParameter("role");
    if (role == null || role.trim().isEmpty()) {
        role = "employee"; // default fallback
    }
    role = role.toLowerCase();

    String roleDisplay = "Employee";
    String roleBadgeClass = "employee";
    if ("technician".equals(role)) {
        roleDisplay = "Technician";
        roleBadgeClass = "technician";
    } else if ("admin".equals(role)) {
        roleDisplay = "Administrator";
        roleBadgeClass = "admin";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= roleDisplay %> Sign In - IT Helpdesk</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <header class="navbar">
        <a href="index.jsp" class="nav-brand">
            <span>💻 IT Helpdesk & Incident Portal</span>
            <span class="brand-badge">Enterprise</span>
        </a>
        <div class="nav-actions">
            <button type="button" class="btn btn-demo" onclick="openDemoModal()">
                <span>🔑</span>
                <span>Demo Credentials</span>
            </button>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container">
        
        <div class="login-wrapper">
            <div class="login-card">
                
                <div class="login-header">
                    <span class="role-pill <%= roleBadgeClass %>" id="rolePillDisplay"><%= roleDisplay %> Access</span>
                    <h2 style="margin-top: 0.75rem;">Sign In</h2>
                    <p class="role-desc" style="margin-bottom: 0;">Enter your credentials to access your support workspace</p>
                </div>

                <% 
                    String error = (String) request.getAttribute("error");
                    if (error == null) {
                        error = request.getParameter("error");
                    }
                    if (error != null && !error.isEmpty()) { 
                %>
                    <div class="alert alert-danger">
                        ⚠️ <%= error %>
                    </div>
                <% } %>

                <form action="login" method="POST" id="loginForm">
                    <input type="hidden" name="role" id="roleInput" value="<%= role %>">

                    <div class="form-group">
                        <label for="usernameInput">Username</label>
                        <input type="text" name="username" id="usernameInput" class="form-control" placeholder="e.g. <%= "admin".equals(role) ? "aritra" : ("technician".equals(role) ? "rahul" : "rohan") %>" required autofocus>
                    </div>

                    <div class="form-group">
                        <label for="passwordInput">Password</label>
                        <input type="password" name="password" id="passwordInput" class="form-control" placeholder="••••••••" required>
                    </div>

                    <div style="margin-top: 1.5rem;">
                        <button type="submit" class="btn btn-primary" style="width: 100%;">Sign In as <%= roleDisplay %></button>
                    </div>
                </form>

                <div style="margin-top: 1.5rem; text-align: center;">
                    <a href="index.jsp" style="color: var(--text-muted); text-decoration: none; font-size: 0.9rem;">
                        &larr; Switch Role / Back to Home
                    </a>
                </div>

            </div>
        </div>

    </main>

    <!-- Demo Credentials Modal -->
    <div class="modal-overlay" id="demoModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title">🔑 Available Demo Accounts</h3>
                <button type="button" class="modal-close" onclick="closeDemoModal()">&times;</button>
            </div>
            
            <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1rem;">
                Click <strong>"Use Account"</strong> on any user below to auto-fill the login form for that role.
            </p>

            <table class="demo-table">
                <thead>
                    <tr>
                        <th>Role</th>
                        <th>Name</th>
                        <th>Username</th>
                        <th>Password</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><span class="role-pill admin">ADMIN</span></td>
                        <td>Aritra Seal</td>
                        <td><code>aritra</code></td>
                        <td><code>aritra123</code></td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-sm" onclick="useDemoAccount('aritra', 'aritra123', 'admin', 'Administrator')">Use Account</button>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="role-pill employee">EMPLOYEE</span></td>
                        <td>Rohan Sharma</td>
                        <td><code>rohan</code></td>
                        <td><code>rohan123</code></td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-sm" onclick="useDemoAccount('rohan', 'rohan123', 'employee', 'Employee')">Use Account</button>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="role-pill employee">EMPLOYEE</span></td>
                        <td>Priya Patel</td>
                        <td><code>priya</code></td>
                        <td><code>priya123</code></td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-sm" onclick="useDemoAccount('priya', 'priya123', 'employee', 'Employee')">Use Account</button>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="role-pill employee">EMPLOYEE</span></td>
                        <td>Ananya Sen</td>
                        <td><code>ananya</code></td>
                        <td><code>ananya123</code></td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-sm" onclick="useDemoAccount('ananya', 'ananya123', 'employee', 'Employee')">Use Account</button>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="role-pill technician">TECHNICIAN</span></td>
                        <td>Rahul Verma</td>
                        <td><code>rahul</code></td>
                        <td><code>rahul123</code></td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-sm" onclick="useDemoAccount('rahul', 'rahul123', 'technician', 'Technician')">Use Account</button>
                        </td>
                    </tr>
                    <tr>
                        <td><span class="role-pill technician">TECHNICIAN</span></td>
                        <td>Sneha Mukherjee</td>
                        <td><code>sneha</code></td>
                        <td><code>sneha123</code></td>
                        <td>
                            <button type="button" class="btn btn-secondary btn-sm" onclick="useDemoAccount('sneha', 'sneha123', 'technician', 'Technician')">Use Account</button>
                        </td>
                    </tr>
                </tbody>
            </table>

            <div style="text-align: right; margin-top: 1rem;">
                <button type="button" class="btn btn-secondary" onclick="closeDemoModal()">Close</button>
            </div>
        </div>
    </div>

    <!-- Modal Script -->
    <script>
        function openDemoModal() {
            document.getElementById('demoModal').classList.add('active');
        }

        function closeDemoModal() {
            document.getElementById('demoModal').classList.remove('active');
        }

        // Close on background click
        window.onclick = function(event) {
            var modal = document.getElementById('demoModal');
            if (event.target === modal) {
                closeDemoModal();
            }
        };

        function useDemoAccount(username, password, role, roleDisplay) {
            document.getElementById('usernameInput').value = username;
            document.getElementById('passwordInput').value = password;
            document.getElementById('roleInput').value = role;
            
            var pill = document.getElementById('rolePillDisplay');
            pill.className = 'role-pill ' + role;
            pill.textContent = roleDisplay + ' Access';
            
            var submitBtn = document.querySelector('#loginForm button[type="submit"]');
            if (submitBtn) {
                submitBtn.textContent = 'Sign In as ' + roleDisplay;
            }
            
            document.title = roleDisplay + ' Sign In - IT Helpdesk';

            closeDemoModal();
        }
    </script>

    <!-- Footer -->
    <footer class="footer">
        <p>IT Helpdesk & Incident Management System &copy; 2026. Built with Java, Servlets, JSP & MySQL.</p>
    </footer>

</body>
</html>
