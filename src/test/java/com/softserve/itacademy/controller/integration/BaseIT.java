package com.softserve.itacademy.controller.integration;

import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.TaskRepository;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for integration tests
 * Provides common setup and teardown logic
 */
public abstract class BaseIT {

    protected static Tomcat tomcat;
    protected static final String WEB_PORT = "8081";
    protected List<Task> testTasks;

    @BeforeAll
    public static void startServer() throws LifecycleException {
        String webappDirLocation = "src/main/webapp/";
        tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = WEB_PORT;
        }

        tomcat.setPort(Integer.parseInt(webPort));

        StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        ctx.getServletContext().setAttribute(Globals.ALT_DD_ATTR, webappDirLocation + "WEB-INF/web.xml");

        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        tomcat.start();
    }

    @AfterAll
    public static void stopServer() throws LifecycleException {
        tomcat.stop();
        tomcat.destroy();
    }

    @BeforeEach
    public void setUp() {

        TaskRepository.getTaskRepository().deleteAll();

        testTasks = new ArrayList<>();
        Task task1 = new Task("Test Task 1", Priority.HIGH);
        Task task2 = new Task("Test Task 2", Priority.MEDIUM);

        TaskRepository.getTaskRepository().create(task1);
        TaskRepository.getTaskRepository().create(task2);

        testTasks.add(task1);
        testTasks.add(task2);

        System.out.println("[DEBUG_LOG] Created test tasks with IDs: " + task1.getId() + ", " + task2.getId());
    }

    @AfterEach
    public void tearDown() {

        TaskRepository.getTaskRepository().deleteAll();
    }

    protected boolean isServerAvailable() {
        try {
            java.net.Socket socket = new java.net.Socket("localhost", Integer.parseInt(WEB_PORT));
            socket.close();
            return true;
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Server not available on port " + WEB_PORT + ": " + e.getMessage());
            return false;
        }
    }
}
