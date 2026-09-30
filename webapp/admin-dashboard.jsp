<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.helpdesk.model.User, com.helpdesk.model.Ticket, java.util.List, java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect("login.jsp?role=admin");
        return;
    }

    @SuppressWarnings("unchecked")
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
    @SuppressWarnings("unchecked")
    List<User> technicians = (List<User>) request.getAttribute("technicians");
    @SuppressWarnings("unchecked")
    List<User> allUsers = (List<User>) request.getAttribute("allUsers");

    Integer totalTickets = (Integer) request.getAttribute("totalTickets");
    Integer openTickets = (Integer) request.getAttribute("openTickets");
    Integer inProgressTickets = (Integer) request.getAttribute("inProgressTickets");
    Integer resolvedTickets = (Integer) request.getAttribute("resolvedTickets");
    String filterPriority = (String) request.getAttribute("filterPriority");
    String filterStatus = (String) request.getAttribute("filterStatus");
    String activeTab = (String) request.getAttribute("activeTab");

    if (totalTickets == null) totalTickets = 0;
    if (openTickets == null) openTickets = 0;
    if (inProgressTickets == null) inProgressTickets = 0;
    if (resolvedTickets == null) resolvedTickets = 0;
    if (filterPriority == null) filterPriority = "ALL";
    if (filterStatus == null) filterStatus = "ALL";
    if (activeTab == null) activeTab = "tickets";

    String successMsg = request.getParameter("success");
    String errorMsg = (String) request.getAttribute("error");
    if (errorMsg == null) errorMsg = request.getParameter("error");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administrator Control Center - IT Helpdesk</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .tab-btn {
            padding: 0.6rem 1.25rem;
            background: #e2e8f0;
            border: none;
            border-radius: var(--radius-md);
            cursor: pointer;
            font-weight: 600;
            color: #475569;
            text-decoration: none;
            display: inline-block;
        }
        .tab-btn.active {
            background: var(--primary-dark);
            color: #ffffff;
        }
        .filter-bar {
            background: #ffffff;
            border: 1px solid var(--border);
            border-radius: var(--radius-md);
            padding: 1rem 1.25rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            flex-wrap: wrap;
        }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <header class="navbar">
        <a href="admin" class="nav-brand">
            <span>💻 IT Helpdesk & Incident Portal</span>
            <span class="brand-badge" style="background-color: #ef4444;">Administrator</span>
        </a>
        <div class="nav-actions">
            <span style="font-size: 0.9rem; color: #cbd5e1;">
                🛡️ <strong><%= currentUser.getName() %></strong> (ID: <%= currentUser.getUserId() %>)
            </span>
            <a href="logout" class="btn btn-secondary btn-sm" style="background-color: #334155; color: #ffffff;">Sign Out</a>
        </div>
    </header>

    <!-- Main Container -->
    <main class="container">

        <!-- Top Header & Tabs -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--primary-dark);">Administrator Control Center</h1>
                <p style="color: var(--text-muted); font-size: 0.95rem;">Organization-wide ticket dispatching, staff management, and system backlog</p>
            </div>
            
            <div style="display: flex; gap: 0.5rem;">
                <a href="admin?tab=tickets" class="tab-btn <%= "tickets".equals(activeTab) ? "active" : "" %>">📋 Incident Tickets</a>
                <a href="admin?tab=users" class="tab-btn <%= "users".equals(activeTab) ? "active" : "" %>">👥 User Accounts</a>
            </div>
        </div>

        <% if (successMsg != null && !successMsg.isEmpty()) { %>
            <div class="alert alert-success">
                ✅ <%= successMsg %>
            </div>
        <% } %>

        <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
            <div class="alert alert-danger">
                ⚠️ <%= errorMsg %>
            </div>
        <% } %>

        <!-- KPI Metrics -->
        <div class="kpi-grid">
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #eff6ff; color: #1d4ed8;">📊</div>
                <div>
                    <div class="kpi-value"><%= totalTickets %></div>
                    <div class="kpi-label">Total System Incidents</div>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #fee2e2; color: #b91c1c;">⏳</div>
                <div>
                    <div class="kpi-value"><%= openTickets %></div>
                    <div class="kpi-label">Open / Unassigned</div>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #fef3c7; color: #b45309;">⚙️</div>
                <div>
                    <div class="kpi-value"><%= inProgressTickets %></div>
                    <div class="kpi-label">In Progress</div>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #d1fae5; color: #047857;">✔️</div>
                <div>
                    <div class="kpi-value"><%= resolvedTickets %></div>
                    <div class="kpi-label">Resolved / Closed</div>
                </div>
            </div>
        </div>

        <% if ("tickets".equals(activeTab)) { %>

            <!-- Filter Bar -->
            <form action="admin" method="GET" class="filter-bar">
                <input type="hidden" name="tab" value="tickets">
                <span style="font-weight: 600; font-size: 0.9rem; color: var(--primary-dark);">🔍 Filter Backlog:</span>

                <div>
                    <select name="priority" class="form-control" style="padding: 0.4rem 0.8rem; font-size: 0.85rem;">
                        <option value="ALL" <%= "ALL".equalsIgnoreCase(filterPriority) ? "selected" : "" %>>All Priorities</option>
                        <option value="LOW" <%= "LOW".equalsIgnoreCase(filterPriority) ? "selected" : "" %>>LOW</option>
                        <option value="MEDIUM" <%= "MEDIUM".equalsIgnoreCase(filterPriority) ? "selected" : "" %>>MEDIUM</option>
                        <option value="HIGH" <%= "HIGH".equalsIgnoreCase(filterPriority) ? "selected" : "" %>>HIGH</option>
                        <option value="CRITICAL" <%= "CRITICAL".equalsIgnoreCase(filterPriority) ? "selected" : "" %>>CRITICAL</option>
                    </select>
                </div>

                <div>
                    <select name="status" class="form-control" style="padding: 0.4rem 0.8rem; font-size: 0.85rem;">
                        <option value="ALL" <%= "ALL".equalsIgnoreCase(filterStatus) ? "selected" : "" %>>All Statuses</option>
                        <option value="OPEN" <%= "OPEN".equalsIgnoreCase(filterStatus) ? "selected" : "" %>>OPEN</option>
                        <option value="ASSIGNED" <%= "ASSIGNED".equalsIgnoreCase(filterStatus) ? "selected" : "" %>>ASSIGNED</option>
                        <option value="IN_PROGRESS" <%= "IN_PROGRESS".equalsIgnoreCase(filterStatus) ? "selected" : "" %>>IN_PROGRESS</option>
                        <option value="RESOLVED" <%= "RESOLVED".equalsIgnoreCase(filterStatus) ? "selected" : "" %>>RESOLVED</option>
                        <option value="CLOSED" <%= "CLOSED".equalsIgnoreCase(filterStatus) ? "selected" : "" %>>CLOSED</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary btn-sm">Apply Filter</button>
                <a href="admin?tab=tickets" class="btn btn-secondary btn-sm">Reset</a>
            </form>

            <!-- All Tickets Table -->
            <div class="table-card">
                <div class="table-header">
                    <h3 class="table-title">System-Wide Tickets</h3>
                    <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;"><%= tickets != null ? tickets.size() : 0 %> displayed</span>
                </div>

                <% if (tickets == null || tickets.isEmpty()) { %>
                    <div style="padding: 3rem; text-align: center; color: var(--text-muted);">
                        <p style="font-size: 1.1rem; font-weight: 600;">No tickets match the selected criteria</p>
                    </div>
                <% } else { %>
                    <div style="overflow-x: auto;">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="width: 60px;">ID</th>
                                    <th>Title</th>
                                    <th>Category</th>
                                    <th>Priority</th>
                                    <th>Status</th>
                                    <th>Employee</th>
                                    <th>Assigned Technician</th>
                                    <th>Created</th>
                                    <th style="text-align: right;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Ticket t : tickets) { 
                                    String status = t.getStatus().toUpperCase();
                                    String statusBadgeClass = "badge-open";
                                    if ("ASSIGNED".equals(status)) statusBadgeClass = "badge-assigned";
                                    else if ("IN_PROGRESS".equals(status)) statusBadgeClass = "badge-inprogress";
                                    else if ("RESOLVED".equals(status)) statusBadgeClass = "badge-resolved";
                                    else if ("CLOSED".equals(status)) statusBadgeClass = "badge-closed";

                                    String priorityClass = "prio-low";
                                    if ("CRITICAL".equalsIgnoreCase(t.getPriority())) priorityClass = "prio-critical";
                                    else if ("HIGH".equalsIgnoreCase(t.getPriority())) priorityClass = "prio-high";
                                    else if ("MEDIUM".equalsIgnoreCase(t.getPriority())) priorityClass = "prio-medium";
                                %>
                                    <tr>
                                        <td><strong>#<%= t.getTicketId() %></strong></td>
                                        <td>
                                            <div style="font-weight: 600; color: var(--primary-dark);"><%= t.getTitle() %></div>
                                            <div style="font-size: 0.8rem; color: var(--text-muted); max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"><%= t.getDescription() %></div>
                                        </td>
                                        <td><span style="font-size: 0.85rem; background: #f1f5f9; padding: 0.2rem 0.5rem; border-radius: 4px;"><%= t.getCategory() %></span></td>
                                        <td><span class="<%= priorityClass %>"><%= t.getPriority() %></span></td>
                                        <td><span class="badge <%= statusBadgeClass %>"><%= t.getStatus() %></span></td>
                                        <td><%= t.getEmployeeName() != null ? t.getEmployeeName() : ("Emp #" + t.getEmployeeId()) %></td>
                                        <td>
                                            <% if (t.getTechnicianName() != null && !t.getTechnicianName().isEmpty()) { %>
                                                <span style="color: #0369a1; font-weight: 600;">🛠️ <%= t.getTechnicianName() %></span>
                                            <% } else { %>
                                                <span style="color: #ef4444; font-weight: 600;">⚠️ Unassigned</span>
                                            <% } %>
                                        </td>
                                        <td style="font-size: 0.85rem; color: var(--text-muted);">
                                            <%= t.getCreatedAt() != null ? sdf.format(t.getCreatedAt()) : "-" %>
                                        </td>
                                        <td style="text-align: right; white-space: nowrap;">
                                            <!-- Assign Button -->
                                            <button type="button" class="btn btn-primary btn-sm" style="background-color: #4f46e5;" onclick="openAssignModal(<%= t.getTicketId() %>, '<%= t.getAssignedTo() != null ? t.getAssignedTo() : "" %>')">
                                                👤 Assign
                                            </button>

                                            <!-- Status Button -->
                                            <button type="button" class="btn btn-secondary btn-sm" onclick="openAdminStatusModal(<%= t.getTicketId() %>, '<%= t.getStatus() %>')">
                                                ⚙️ Status
                                            </button>

                                            <!-- Details -->
                                            <a href="ticket?id=<%= t.getTicketId() %>" class="btn btn-secondary btn-sm">
                                                💬
                                            </a>

                                            <!-- Delete (Only if OPEN) -->
                                            <% if ("OPEN".equalsIgnoreCase(t.getStatus())) { %>
                                                <form action="admin" method="POST" style="display: inline;" onsubmit="return confirm('Are you sure you want to delete OPEN Ticket #<%= t.getTicketId() %>?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="ticketId" value="<%= t.getTicketId() %>">
                                                    <button type="submit" class="btn btn-secondary btn-sm" style="color: #ef4444;" title="Delete Open Ticket">🗑️</button>
                                                </form>
                                            <% } %>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </div>

        <% } else if ("users".equals(activeTab)) { %>

            <!-- User Management View -->
            <div class="table-card">
                <div class="table-header">
                    <h3 class="table-title">Registered System Users</h3>
                    <button type="button" class="btn btn-primary btn-sm" onclick="openCreateUserModal()">
                        ➕ Add New User
                    </button>
                </div>

                <div style="overflow-x: auto;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 70px;">User ID</th>
                                <th>Full Name</th>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (allUsers != null) { 
                                for (User u : allUsers) { 
                                    String roleClass = "role-pill employee";
                                    if ("ADMIN".equalsIgnoreCase(u.getRole())) roleClass = "role-pill admin";
                                    else if ("TECHNICIAN".equalsIgnoreCase(u.getRole())) roleClass = "role-pill technician";
                            %>
                                <tr>
                                    <td><strong>#<%= u.getUserId() %></strong></td>
                                    <td><strong><%= u.getName() %></strong></td>
                                    <td><code><%= u.getUsername() %></code></td>
                                    <td><%= u.getEmail() %></td>
                                    <td><span class="<%= roleClass %>"><%= u.getRole() %></span></td>
                                </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>

        <% } %>

    </main>

    <!-- Assign Modal -->
    <div class="modal-overlay" id="assignModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title">👤 Assign Incident to Technician</h3>
                <button type="button" class="modal-close" onclick="closeAssignModal()">&times;</button>
            </div>

            <form action="admin" method="POST">
                <input type="hidden" name="action" value="assign">
                <input type="hidden" name="ticketId" id="assignTicketId">

                <div style="margin-bottom: 1.25rem;">
                    <p style="font-weight: 600; color: var(--primary-dark);" id="assignTicketLabel">Ticket #</p>
                </div>

                <div class="form-group">
                    <label for="technicianSelect">Select Technician *</label>
                    <select id="technicianSelect" name="technicianId" class="form-control" required>
                        <option value="">-- Choose a Technician --</option>
                        <% if (technicians != null) {
                            for (User tech : technicians) { %>
                                <option value="<%= tech.getUserId() %>"><%= tech.getName() %> (Tech ID: <%= tech.getUserId() %>)</option>
                        <% } } %>
                    </select>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-secondary" onclick="closeAssignModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary" style="background-color: #4f46e5;">Confirm Assignment</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Admin Status Modal -->
    <div class="modal-overlay" id="adminStatusModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title">⚙️ Update Incident Status</h3>
                <button type="button" class="modal-close" onclick="closeAdminStatusModal()">&times;</button>
            </div>

            <form action="admin" method="POST">
                <input type="hidden" name="action" value="updateStatus">
                <input type="hidden" name="ticketId" id="adminStatusTicketId">

                <div class="form-group">
                    <label for="adminStatusSelect">Select Status *</label>
                    <select id="adminStatusSelect" name="newStatus" class="form-control" required>
                        <option value="OPEN">OPEN</option>
                        <option value="ASSIGNED">ASSIGNED</option>
                        <option value="IN_PROGRESS">IN_PROGRESS</option>
                        <option value="RESOLVED">RESOLVED</option>
                        <option value="CLOSED">CLOSED</option>
                    </select>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-secondary" onclick="closeAdminStatusModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Status</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Create User Modal -->
    <div class="modal-overlay" id="createUserModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title">➕ Create System User Account</h3>
                <button type="button" class="modal-close" onclick="closeCreateUserModal()">&times;</button>
            </div>

            <form action="admin" method="POST">
                <input type="hidden" name="action" value="createUser">

                <div class="form-group">
                    <label for="newFullName">Full Name *</label>
                    <input type="text" id="newFullName" name="name" class="form-control" placeholder="e.g. Vikram Malhotra" required>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="newUsername">Username *</label>
                        <input type="text" id="newUsername" name="username" class="form-control" placeholder="e.g. vikram" required>
                    </div>

                    <div class="form-group">
                        <label for="newPassword">Initial Password *</label>
                        <input type="password" id="newPassword" name="password" class="form-control" placeholder="••••••••" required>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="newEmail">Email Address *</label>
                        <input type="email" id="newEmail" name="email" class="form-control" placeholder="vikram@example.com" required>
                    </div>

                    <div class="form-group">
                        <label for="newRole">System Role *</label>
                        <select id="newRole" name="role" class="form-control" required>
                            <option value="EMPLOYEE" selected>EMPLOYEE</option>
                            <option value="TECHNICIAN">TECHNICIAN</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                    </div>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-secondary" onclick="closeCreateUserModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create User Account</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Scripts -->
    <script>
        function openAssignModal(ticketId, currentTechId) {
            document.getElementById('assignTicketId').value = ticketId;
            document.getElementById('assignTicketLabel').textContent = 'Assigning Technician for Incident #' + ticketId;
            var select = document.getElementById('technicianSelect');
            select.value = currentTechId || '';
            document.getElementById('assignModal').classList.add('active');
        }

        function closeAssignModal() {
            document.getElementById('assignModal').classList.remove('active');
        }

        function openAdminStatusModal(ticketId, currentStatus) {
            document.getElementById('adminStatusTicketId').value = ticketId;
            document.getElementById('adminStatusSelect').value = currentStatus;
            document.getElementById('adminStatusModal').classList.add('active');
        }

        function closeAdminStatusModal() {
            document.getElementById('adminStatusModal').classList.remove('active');
        }

        function openCreateUserModal() {
            document.getElementById('createUserModal').classList.add('active');
        }

        function closeCreateUserModal() {
            document.getElementById('createUserModal').classList.remove('active');
        }

        window.onclick = function(event) {
            if (event.target === document.getElementById('assignModal')) closeAssignModal();
            if (event.target === document.getElementById('adminStatusModal')) closeAdminStatusModal();
            if (event.target === document.getElementById('createUserModal')) closeCreateUserModal();
        };
    </script>

    <!-- Footer -->
    <footer class="footer">
        <p>IT Helpdesk & Incident Management System &copy; 2026. Built with Java, Servlets, JSP & MySQL.</p>
    </footer>

</body>
</html>
