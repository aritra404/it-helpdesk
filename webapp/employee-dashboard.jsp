<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.helpdesk.model.User, com.helpdesk.model.Ticket, java.util.List, java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"EMPLOYEE".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect("login.jsp?role=employee");
        return;
    }

    @SuppressWarnings("unchecked")
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    Integer openCount = (Integer) request.getAttribute("openCount");
    Integer inProgressCount = (Integer) request.getAttribute("inProgressCount");
    Integer resolvedCount = (Integer) request.getAttribute("resolvedCount");

    if (totalCount == null) totalCount = 0;
    if (openCount == null) openCount = 0;
    if (inProgressCount == null) inProgressCount = 0;
    if (resolvedCount == null) resolvedCount = 0;

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
    <title>Employee Workspace - IT Helpdesk</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <header class="navbar">
        <a href="employee" class="nav-brand">
            <span>💻 IT Helpdesk & Incident Portal</span>
            <span class="brand-badge">Employee</span>
        </a>
        <div class="nav-actions">
            <span style="font-size: 0.9rem; color: #cbd5e1;">
                👤 <strong><%= currentUser.getName() %></strong> (ID: <%= currentUser.getUserId() %>)
            </span>
            <a href="logout" class="btn btn-secondary btn-sm" style="background-color: #334155; color: #ffffff;">Sign Out</a>
        </div>
    </header>

    <!-- Main Content Container -->
    <main class="container">

        <!-- Top Header & Action -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--primary-dark);">My Support Workspace</h1>
                <p style="color: var(--text-muted); font-size: 0.95rem;">Track your submitted requests and raise new technical incidents</p>
            </div>
            <button type="button" class="btn btn-primary" onclick="openCreateModal()">
                <span>➕</span> Raise Support Ticket
            </button>
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

        <!-- KPI Metric Cards -->
        <div class="kpi-grid">
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #eff6ff; color: #1d4ed8;">📋</div>
                <div>
                    <div class="kpi-value"><%= totalCount %></div>
                    <div class="kpi-label">Total Tickets</div>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #dbeafe; color: #1e40af;">⏳</div>
                <div>
                    <div class="kpi-value"><%= openCount %></div>
                    <div class="kpi-label">Open / Pending</div>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #fef3c7; color: #b45309;">⚙️</div>
                <div>
                    <div class="kpi-value"><%= inProgressCount %></div>
                    <div class="kpi-label">In Progress</div>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #d1fae5; color: #047857;">✔️</div>
                <div>
                    <div class="kpi-value"><%= resolvedCount %></div>
                    <div class="kpi-label">Resolved / Closed</div>
                </div>
            </div>
        </div>

        <!-- Submitted Tickets Table -->
        <div class="table-card">
            <div class="table-header">
                <h3 class="table-title">My Raised Tickets</h3>
                <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;"><%= tickets != null ? tickets.size() : 0 %> records</span>
            </div>

            <% if (tickets == null || tickets.isEmpty()) { %>
                <div style="padding: 3rem; text-align: center; color: var(--text-muted);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📝</div>
                    <p style="font-size: 1.1rem; font-weight: 600;">No support tickets raised yet</p>
                    <p style="font-size: 0.9rem;">Click the "+ Raise Support Ticket" button above to submit your first incident.</p>
                </div>
            <% } else { %>
                <div style="overflow-x: auto;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 70px;">ID</th>
                                <th>Title</th>
                                <th>Category</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Assigned Tech</th>
                                <th>Created</th>
                                <th style="text-align: right;">Action</th>
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
                                        <div style="font-size: 0.8rem; color: var(--text-muted); max-width: 320px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"><%= t.getDescription() %></div>
                                    </td>
                                    <td><span style="font-size: 0.85rem; background: #f1f5f9; padding: 0.2rem 0.5rem; border-radius: 4px;"><%= t.getCategory() %></span></td>
                                    <td><span class="<%= priorityClass %>"><%= t.getPriority() %></span></td>
                                    <td><span class="badge <%= statusBadgeClass %>"><%= t.getStatus() %></span></td>
                                    <td>
                                        <%= (t.getTechnicianName() != null && !t.getTechnicianName().isEmpty()) ? t.getTechnicianName() : "<span style='color: var(--text-muted);'>Unassigned</span>" %>
                                    </td>
                                    <td style="font-size: 0.85rem; color: var(--text-muted);">
                                        <%= t.getCreatedAt() != null ? sdf.format(t.getCreatedAt()) : "-" %>
                                    </td>
                                    <td style="text-align: right;">
                                        <a href="ticket?id=<%= t.getTicketId() %>" class="btn btn-secondary btn-sm">
                                            💬 Details & Comments
                                        </a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>

    </main>

    <!-- Create Ticket Modal -->
    <div class="modal-overlay" id="createModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title">➕ Raise New Support Ticket</h3>
                <button type="button" class="modal-close" onclick="closeCreateModal()">&times;</button>
            </div>

            <form action="employee" method="POST">
                <input type="hidden" name="action" value="create">

                <div class="form-group">
                    <label for="ticketTitle">Ticket Title *</label>
                    <input type="text" id="ticketTitle" name="title" class="form-control" placeholder="Brief summary of the issue (e.g. Laptop battery failing)" required>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label for="ticketCategory">Category *</label>
                        <select id="ticketCategory" name="category" class="form-control" required>
                            <option value="Hardware">Hardware</option>
                            <option value="Software">Software</option>
                            <option value="Network">Network</option>
                            <option value="Access">Access / Permissions</option>
                            <option value="Other" selected>Other</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="ticketPriority">Priority *</label>
                        <select id="ticketPriority" name="priority" class="form-control" required>
                            <option value="LOW">LOW</option>
                            <option value="MEDIUM" selected>MEDIUM</option>
                            <option value="HIGH">HIGH</option>
                            <option value="CRITICAL">CRITICAL</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="ticketDescription">Detailed Description *</label>
                    <textarea id="ticketDescription" name="description" class="form-control" rows="4" placeholder="Explain the symptoms, error messages, and reproduction steps..." required></textarea>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-secondary" onclick="closeCreateModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Submit Ticket</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal Script -->
    <script>
        function openCreateModal() {
            document.getElementById('createModal').classList.add('active');
            document.getElementById('ticketTitle').focus();
        }

        function closeCreateModal() {
            document.getElementById('createModal').classList.remove('active');
        }

        window.onclick = function(event) {
            var modal = document.getElementById('createModal');
            if (event.target === modal) {
                closeCreateModal();
            }
        };
    </script>

    <!-- Footer -->
    <footer class="footer">
        <p>IT Helpdesk & Incident Management System &copy; 2026. Built with Java, Servlets, JSP & MySQL.</p>
    </footer>

</body>
</html>
