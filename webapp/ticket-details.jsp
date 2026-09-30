<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.helpdesk.model.User, com.helpdesk.model.Ticket, com.helpdesk.model.TicketComment, java.util.List, java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    Ticket ticket = (Ticket) request.getAttribute("ticket");
    @SuppressWarnings("unchecked")
    List<TicketComment> comments = (List<TicketComment>) request.getAttribute("comments");

    String successMsg = request.getParameter("success");
    String errorMsg = request.getParameter("error");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    String backUrl = "employee";
    String roleName = currentUser.getRole().toUpperCase();
    if ("ADMIN".equals(roleName)) {
        backUrl = "admin";
    } else if ("TECHNICIAN".equals(roleName)) {
        backUrl = "technician";
    }

    String statusBadgeClass = "badge-open";
    if (ticket != null) {
        String status = ticket.getStatus().toUpperCase();
        if ("ASSIGNED".equals(status)) statusBadgeClass = "badge-assigned";
        else if ("IN_PROGRESS".equals(status)) statusBadgeClass = "badge-inprogress";
        else if ("RESOLVED".equals(status)) statusBadgeClass = "badge-resolved";
        else if ("CLOSED".equals(status)) statusBadgeClass = "badge-closed";
    }

    String priorityClass = "prio-low";
    if (ticket != null) {
        if ("CRITICAL".equalsIgnoreCase(ticket.getPriority())) priorityClass = "prio-critical";
        else if ("HIGH".equalsIgnoreCase(ticket.getPriority())) priorityClass = "prio-high";
        else if ("MEDIUM".equalsIgnoreCase(ticket.getPriority())) priorityClass = "prio-medium";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Incident #<%= ticket != null ? ticket.getTicketId() : "" %> - IT Helpdesk</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .detail-meta-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.25rem;
            margin: 1.5rem 0;
            background: #f8fafc;
            padding: 1.25rem;
            border-radius: var(--radius-md);
            border: 1px solid var(--border);
        }
        .meta-item label {
            display: block;
            font-size: 0.75rem;
            font-weight: 700;
            color: var(--text-muted);
            text-transform: uppercase;
            margin-bottom: 0.25rem;
        }
        .meta-item value {
            display: block;
            font-size: 0.95rem;
            font-weight: 600;
            color: var(--primary-dark);
        }
        .comment-thread {
            margin-top: 2rem;
        }
        .comment-bubble {
            background: #ffffff;
            border: 1px solid var(--border);
            border-radius: var(--radius-md);
            padding: 1.25rem;
            margin-bottom: 1rem;
            box-shadow: var(--shadow-sm);
        }
        .comment-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0.5rem;
        }
        .comment-author {
            font-weight: 700;
            font-size: 0.9rem;
            color: var(--primary-dark);
        }
        .comment-date {
            font-size: 0.8rem;
            color: var(--text-muted);
        }
        .comment-body {
            font-size: 0.9rem;
            color: #334155;
            line-height: 1.5;
            white-space: pre-wrap;
        }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <header class="navbar">
        <a href="<%= backUrl %>" class="nav-brand">
            <span>💻 IT Helpdesk & Incident Portal</span>
            <span class="brand-badge"><%= currentUser.getRole() %></span>
        </a>
        <div class="nav-actions">
            <span style="font-size: 0.9rem; color: #cbd5e1;">
                👤 <strong><%= currentUser.getName() %></strong>
            </span>
            <a href="logout" class="btn btn-secondary btn-sm" style="background-color: #334155; color: #ffffff;">Sign Out</a>
        </div>
    </header>

    <!-- Main Container -->
    <main class="container">

        <!-- Back Link & Title Header -->
        <div style="margin-bottom: 1.5rem;">
            <a href="<%= backUrl %>" style="text-decoration: none; color: var(--primary); font-weight: 600; font-size: 0.9rem;">
                &larr; Back to Dashboard
            </a>
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

        <% if (ticket != null) { %>
            <!-- Ticket Overview Card -->
            <div class="table-card" style="padding: 2rem;">
                
                <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; border-bottom: 1px solid var(--border); padding-bottom: 1.5rem;">
                    <div>
                        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.5rem;">
                            <span style="font-size: 1.5rem; font-weight: 800; color: var(--primary-dark);">#<%= ticket.getTicketId() %></span>
                            <span class="badge <%= statusBadgeClass %>" style="font-size: 0.85rem; padding: 0.35rem 0.8rem;"><%= ticket.getStatus() %></span>
                            <span class="<%= priorityClass %>" style="font-size: 0.9rem;"><%= ticket.getPriority() %> Priority</span>
                        </div>
                        <h2 style="font-size: 1.4rem; color: #1e293b;"><%= ticket.getTitle() %></h2>
                    </div>

                    <% if ("TECHNICIAN".equals(roleName)) { %>
                        <button type="button" class="btn btn-primary" onclick="openTechStatusModal()">
                            ⚙️ Update Status
                        </button>
                    <% } %>
                </div>

                <!-- Metadata Grid -->
                <div class="detail-meta-grid">
                    <div class="meta-item">
                        <label>Category</label>
                        <value><%= ticket.getCategory() %></value>
                    </div>
                    <div class="meta-item">
                        <label>Submitted By</label>
                        <value>👤 <%= ticket.getEmployeeName() != null ? ticket.getEmployeeName() : ("Employee #" + ticket.getEmployeeId()) %></value>
                    </div>
                    <div class="meta-item">
                        <label>Assigned Technician</label>
                        <value>
                            <%= ticket.getTechnicianName() != null ? ("🛠️ " + ticket.getTechnicianName()) : "<span style='color: #ef4444;'>Unassigned</span>" %>
                        </value>
                    </div>
                    <div class="meta-item">
                        <label>Created On</label>
                        <value><%= ticket.getCreatedAt() != null ? sdf.format(ticket.getCreatedAt()) : "-" %></value>
                    </div>
                    <div class="meta-item">
                        <label>Last Updated</label>
                        <value><%= ticket.getUpdatedAt() != null ? sdf.format(ticket.getUpdatedAt()) : "-" %></value>
                    </div>
                </div>

                <!-- Description Box -->
                <div style="margin-top: 1.5rem;">
                    <h4 style="font-size: 1rem; font-weight: 700; color: var(--primary-dark); margin-bottom: 0.5rem;">Detailed Description</h4>
                    <div style="background: #ffffff; border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1.25rem; font-size: 0.95rem; line-height: 1.6; color: #334155; white-space: pre-wrap;"><%= ticket.getDescription() %></div>
                </div>

            </div>

            <!-- Comments & Discussion Thread -->
            <div class="comment-thread">
                <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--primary-dark); margin-bottom: 1rem;">
                    💬 Troubleshooting Timeline & Discussion (<%= comments != null ? comments.size() : 0 %>)
                </h3>

                <% if (comments == null || comments.isEmpty()) { %>
                    <div class="comment-bubble" style="text-align: center; color: var(--text-muted); padding: 2rem;">
                        No comments or troubleshooting notes added to this incident yet.
                    </div>
                <% } else { %>
                    <% for (TicketComment c : comments) { 
                        String rolePill = "role-pill employee";
                        if ("ADMIN".equalsIgnoreCase(c.getUserRole())) rolePill = "role-pill admin";
                        else if ("TECHNICIAN".equalsIgnoreCase(c.getUserRole())) rolePill = "role-pill technician";
                    %>
                        <div class="comment-bubble">
                            <div class="comment-header">
                                <div style="display: flex; align-items: center; gap: 0.5rem;">
                                    <span class="comment-author"><%= c.getUserName() %></span>
                                    <span class="<%= rolePill %>" style="font-size: 0.7rem;"><%= c.getUserRole() %></span>
                                </div>
                                <span class="comment-date"><%= c.getCreatedAt() != null ? sdf.format(c.getCreatedAt()) : "" %></span>
                            </div>
                            <div class="comment-body"><%= c.getCommentText() %></div>
                        </div>
                    <% } %>
                <% } %>

                <!-- Add Comment Card -->
                <div class="table-card" style="padding: 1.5rem; margin-top: 1.5rem;">
                    <h4 style="font-size: 1.05rem; font-weight: 700; color: var(--primary-dark); margin-bottom: 1rem;">
                        ➕ Add Troubleshooting Note / Response
                    </h4>

                    <form action="ticket" method="POST">
                        <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">

                        <div class="form-group">
                            <textarea name="commentText" class="form-control" rows="3" placeholder="Type your message, diagnostic findings, or response here..." required></textarea>
                        </div>

                        <div style="text-align: right; margin-top: 1rem;">
                            <button type="submit" class="btn btn-primary">Post Note</button>
                        </div>
                    </form>
                </div>

            </div>
        <% } %>

    </main>

    <!-- Modal for Technician Quick Status -->
    <% if ("TECHNICIAN".equals(roleName) && ticket != null) { %>
        <div class="modal-overlay" id="techStatusModal">
            <div class="modal-box">
                <div class="modal-header">
                    <h3 class="modal-title">⚙️ Update Incident Status</h3>
                    <button type="button" class="modal-close" onclick="closeTechStatusModal()">&times;</button>
                </div>

                <form action="technician" method="POST">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">

                    <div class="form-group">
                        <label for="newStatusSelect">New Status *</label>
                        <select id="newStatusSelect" name="newStatus" class="form-control" required>
                            <option value="ASSIGNED" <%= "ASSIGNED".equalsIgnoreCase(ticket.getStatus()) ? "selected" : "" %>>ASSIGNED</option>
                            <option value="IN_PROGRESS" <%= "IN_PROGRESS".equalsIgnoreCase(ticket.getStatus()) ? "selected" : "" %>>IN_PROGRESS</option>
                            <option value="RESOLVED" <%= "RESOLVED".equalsIgnoreCase(ticket.getStatus()) ? "selected" : "" %>>RESOLVED</option>
                            <option value="CLOSED" <%= "CLOSED".equalsIgnoreCase(ticket.getStatus()) ? "selected" : "" %>>CLOSED</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="diagnosisNotes">Optional Resolution Note</label>
                        <textarea id="diagnosisNotes" name="notes" class="form-control" rows="3" placeholder="Add diagnosis notes or resolution steps..."></textarea>
                    </div>

                    <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                        <button type="button" class="btn btn-secondary" onclick="closeTechStatusModal()">Cancel</button>
                        <button type="submit" class="btn btn-primary">Update Status</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openTechStatusModal() {
                document.getElementById('techStatusModal').classList.add('active');
            }
            function closeTechStatusModal() {
                document.getElementById('techStatusModal').classList.remove('active');
            }
            window.onclick = function(event) {
                var m = document.getElementById('techStatusModal');
                if (event.target === m) closeTechStatusModal();
            };
        </script>
    <% } %>

    <!-- Footer -->
    <footer class="footer">
        <p>IT Helpdesk & Incident Management System &copy; 2026. Built with Java, Servlets, JSP & MySQL.</p>
    </footer>

</body>
</html>
