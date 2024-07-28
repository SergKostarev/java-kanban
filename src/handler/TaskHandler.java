package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IdentifierException;
import exception.IntersectionException;
import exception.NotFoundException;
import model.Task;
import service.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            super.sendText(exchange, gson.toJson(super.taskManager.getAllTasks()), 200);
        } else if (exchange.getRequestMethod().equals("GET")
                && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Task task = super.taskManager.getTask(id);
                super.sendText(exchange, gson.toJson(task), 200);
            } catch (NotFoundException te) {
                super.sendTaskException(exchange, te, 404);
            } catch (Exception e) {
                super.sendNoEndpoint(exchange);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")
                && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Task task = super.taskManager.removeTask(id);
                super.sendText(exchange, gson.toJson(task), 200);
            } catch (Exception e) {
                super.sendNoEndpoint(exchange);
            }
        } else if (exchange.getRequestMethod().equals("POST") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            String json = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(json, Task.class);
            if (task.getId() == null) {
                try {
                    Task taskOutput = taskManager.addTask(task);
                    super.sendText(exchange, gson.toJson(taskOutput), 201);
                } catch (IntersectionException te) {
                    super.sendTaskException(exchange, te, 406);
                }
            } else {
                try {
                    Task taskOutput = taskManager.updateTask(task);
                    super.sendText(exchange, gson.toJson(taskOutput), 201);
                } catch (IntersectionException te) {
                    super.sendTaskException(exchange, te, 406);
                } catch (NotFoundException | IdentifierException te) {
                    super.sendTaskException(exchange, te, 404);
                }
            }
        } else {
            super.sendNoEndpoint(exchange);
        }
    }
}
