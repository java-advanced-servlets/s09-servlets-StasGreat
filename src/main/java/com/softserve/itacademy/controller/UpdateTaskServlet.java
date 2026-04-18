package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.TaskRepository;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/edit-task")
public class UpdateTaskServlet extends HttpServlet {

    private TaskRepository taskRepository;

    @Override
    public void init() {
        taskRepository = TaskRepository.getTaskRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Logic to handle GET requests, possibly for fetching a task to update
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("message", "Task ID is missing!");
            request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Task task = taskRepository.read(id);
            if (task == null) {
                request.setAttribute("message", "Task with ID '" + id + "' not found in To-Do List!");
                request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
                return;
            }

            request.setAttribute("task", task);
            request.getRequestDispatcher("/WEB-INF/pages/edit-task.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid Task ID format!");
            request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Logic to handle POST requests, typically for updating task data
        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String priorityStr = request.getParameter("priority");

        if (idStr == null || idStr.isEmpty() || title == null || title.isEmpty() || priorityStr == null || priorityStr.isEmpty()) {
            request.setAttribute("message", "Task ID, title, and priority must not be empty!");
            request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Task task = taskRepository.read(id);
            if (task == null) {
                request.setAttribute("message", "Task with ID '" + id + "' not found in To-Do List!");
                request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
                return;
            }

            Priority priority = Priority.valueOf(priorityStr);
            task.setTitle(title);
            task.setPriority(priority);

            boolean updated = taskRepository.update(task);
            if (updated) {
                response.sendRedirect("/tasks-list");
                return;
            }

            request.setAttribute("error", "Task with ID '" + id + "' was not updated. A task with the same title may already exist.");
            request.setAttribute("task", task);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/pages/edit-task.jsp");
            requestDispatcher.forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("message", "Invalid request data!");
            request.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(request, response);
        }
    }
}