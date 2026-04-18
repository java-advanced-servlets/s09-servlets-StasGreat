<%@ page import="com.softserve.itacademy.model.Task" %>
<%@ page import="com.softserve.itacademy.model.Priority" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    Task task = (Task) request.getAttribute("task");
%>
<!DOCTYPE html>
<html lang="en" xml:lang="en">
<head>
    <title>Edit existing Task</title>
    <style>
        <%@include file="../styles/main.css"%>
    </style>
</head>
<body>
<%@include file="header.html" %>

<div class="form-container">
    <h2>Edit existing Task</h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <% if (task != null) { %>
        <form action="${pageContext.request.contextPath}/edit-task" method="post">
            <input type="hidden" name="id" value="<%= task.getId() %>">

            <div class="form-group">
                <label for="title">Title:</label>
                <input
                        type="text"
                        id="title"
                        name="title"
                        class="form-control"
                        value="<%= task.getTitle() %>"
                        placeholder="Enter task title"
                        required>
            </div>

            <div class="form-group">
                <label for="priority">Priority:</label>
                <select id="priority" name="priority" class="form-select" required>
                    <% for (Priority priority : Priority.values()) { %>
                        <option value="<%= priority %>" <%= task.getPriority() == priority ? "selected" : "" %>>
                            <%= priority %>
                        </option>
                    <% } %>
                </select>
            </div>

            <button type="submit" class="btn btn-success">Update Task</button>
            <a href="${pageContext.request.contextPath}/tasks-list" class="btn">Cancel</a>
        </form>
    <% } %>
</div>

</body>
</html>
