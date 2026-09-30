<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"TECHNICIAN".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect("login.jsp?role=technician");
        return;
    }

    @SuppressWarnings("unchecked")
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
    Integer totalAssigned = (Integer) request.getAttribute("totalAssigned");
    Integer inProgressCount = (Integer) request.getAttribute("inProgressCount");
    Integer resolvedCount = (Integer) request.getAttribute("resolvedCount");
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");

    if (totalAssigned == null) totalAssigned = 0;
    if (inProgressCount == null) inProgressCount = 0;
    if (resolvedCount == null) resolvedCount = 0;
    if (pendingCount == null) pendingCount = 0;

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
    <title>Technician Workbench - IT Helpdesk</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <header class="navbar">
        <a href="technician" class="nav-brand">
            <span>💻 IT Helpdesk & Incident Portal</span>
            <span class="brand-badge" style="background-color: #0284c7;">Technician</span>
        </a>
        <div class="nav-actions">
            <span style="font-size: 0.9rem; color: #cbd5e1;">
                🛠️ <strong><%= currentUser.getName() %></strong> (ID: <%= currentUser.getUserId() %>)
            </span>
            <a href="logout" class="btn btn-secondary btn-sm" style="background-color: #334155; color: #ffffff;">Sign Out</a>
        </div>
    </header>

    <!-- Main Container -->
    <main class="container">

        <!-- Top Title -->
        <div style="margin-bottom: 2rem;">
            <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--primary-dark);">Technician Incident Workbench</h1>
            <p style="color: var(--text-muted); font-size: 0.95rem;">Manage assigned tickets, diagnose technical problems, and log resolution updates</p>
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
                <div class="kpi-icon" style="background: #e0f2fe; color: #0369a1;">📥</div>
                <div>
                    <div class="kpi-value"><%= totalAssigned %></div>
                    <div class="kpi-label">Assigned Queue</div>
                </div>
            </div>
            <div class="kpi-card">
                <div class="kpi-icon" style="background: #dbeafe; color: #1e40af;">⏳</div>
                <div>
                    <div class="kpi-value"><%= pendingCount %></div>
                    <div class="kpi-label">Pending / Assigned</div>
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

        <!-- Assigned Tickets Table -->
        <div class="table-card">
            <div class="table-header">
                <h3 class="table-title">My Assigned Incidents</h3>
                <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600;"><%= tickets != null ? tickets.size() : 0 %> active assignments</span>
            </div>

            <% if (tickets == null || tickets.isEmpty()) { %>
                <div style="padding: 3rem; text-align: center; color: var(--text-muted);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🎉</div>
                    <p style="font-size: 1.1rem; font-weight: 600;">Queue is currently empty</p>
                    <p style="font-size: 0.9rem;">No incidents are currently assigned to your technician profile.</p>
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
                                <th>Reported By</th>
                                <th>Created Date</th>
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
                                        <div style="font-size: 0.8rem; color: var(--text-muted); max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"><%= t.getDescription() %></div>
                                    </td>
                                    <td><span style="font-size: 0.85rem; background: #f1f5f9; padding: 0.2rem 0.5rem; border-radius: 4px;"><%= t.getCategory() %></span></td>
                                    <td><span class="<%= priorityClass %>"><%= t.getPriority() %></span></td>
                                    <td><span class="badge <%= statusBadgeClass %>"><%= t.getStatus() %></span></td>
                                    <td><%= t.getEmployeeName() != null ? t.getEmployeeName() : ("Emp #" + t.getEmployeeId()) %></td>
                                    <td style="font-size: 0.85rem; color: var(--text-muted);">
                                        <%= t.getCreatedAt() != null ? sdf.format(t.getCreatedAt()) : "-" %>
                                    </td>
                                    <td style="text-align: right; white-space: nowrap;">
                                        <button type="button" class="btn btn-primary btn-sm" style="background-color: #0284c7;" onclick="openStatusModal(<%= t.getTicketId() %>, '<%= t.getStatus() %>')">
                                            ⚙️ Update Status
                                        </button>
                                        <a href="ticket?id=<%= t.getTicketId() %>" class="btn btn-secondary btn-sm">
                                            💬 Details
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

    <!-- Update Status Modal -->
    <div class="modal-overlay" id="statusModal">
        <div class="modal-box">
            <div class="modal-header">
                <h3 class="modal-title">⚙️ Update Ticket Status</h3>
                <button type="button" class="modal-close" onclick="closeStatusModal()">&times;</button>
            </div>

            <form action="technician" method="POST">
                <input type="hidden" name="action" value="updateStatus">
                <input type="hidden" name="ticketId" id="modalTicketId">

                <div style="margin-bottom: 1.25rem;">
                    <p style="font-size: 0.95rem; font-weight: 600; color: var(--primary-dark);" id="modalTicketLabel">
                        Updating Ticket #
                    </p>
                </div>

                <div class="form-group">
                    <label for="newStatusSelect">Target Status *</label>
                    <select id="newStatusSelect" name="newStatus" class="form-control" required>
                        <option value="ASSIGNED">ASSIGNED (Acknowledged)</option>
                        <option value="IN_PROGRESS">IN_PROGRESS (Actively diagnosing)</option>
                        <option value="RESOLVED">RESOLVED (Solution applied)</option>
                        <option value="CLOSED">CLOSED (Confirmed fixed)</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="diagnosisNotes">Troubleshooting / Resolution Notes</label>
                    <textarea id="diagnosisNotes" name="notes" class="form-control" rows="3" placeholder="Describe diagnosis findings, actions taken, or instructions for the employee..."></textarea>
                </div>

                <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-secondary" onclick="closeStatusModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary" style="background-color: #0284c7;">Apply Status Update</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal Script -->
    <script>
        function openStatusModal(ticketId, currentStatus) {
            document.getElementById('modalTicketId').value = ticketId;
            document.getElementById('modalTicketLabel').textContent = 'Updating Status for Ticket #' + ticketId;
            
            var statusSelect = document.getElementById('newStatusSelect');
            for (var i = 0; i < statusSelect.options.length; i++) {
                if (statusSelect.options[i].value === currentStatus) {
                    statusSelect.selectedIndex = i;
                    break;
                }
            }
            
            document.getElementById('diagnosisNotes').value = '';
            document.getElementById('statusModal').classList.add('active');
        }

        function closeStatusModal() {
            document.getElementById('statusModal').classList.remove('active');
        }

        window.onclick = function(event) {
            var modal = document.getElementById('statusModal');
            if (event.target === modal) {
                closeStatusModal();
            }
        };
    </script>

    <!-- Footer -->
    <footer class="footer">
        <p>IT Helpdesk & Incident Management System &copy; 2026. Built with Java, Servlets, JSP & MySQL.</p>
    </footer>

</body>
</html>
