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
    private final TaskManager taskManager;

    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            sendResponse(exchange, gson.toJson(taskManager.getAllTasks()), 200);
        } else if (exchange.getRequestMethod().equals("GET")
                && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Task task = taskManager.getTask(id);
                sendResponse(exchange, gson.toJson(task), 200);
            } catch (NotFoundException te) {
                sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
            } catch (Exception e) {
                sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")
                && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Task task = taskManager.removeTask(id);
                sendResponse(exchange, gson.toJson(task), 200);
            } catch (Exception e) {
                sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
            }
        } else if (exchange.getRequestMethod().equals("POST") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            String json = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(json, Task.class);
            if (task.getId() == null) {
                try {
                    Task taskOutput = taskManager.addTask(task);
                    sendResponse(exchange, gson.toJson(taskOutput), 201);
                } catch (IntersectionException te) {
                    sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 406)), 406);
                }
            } else {
                try {
                    Task taskOutput = taskManager.updateTask(task);
                    sendResponse(exchange, gson.toJson(taskOutput), 201);
                } catch (IntersectionException te) {
                    sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 406)), 406);
                } catch (NotFoundException | IdentifierException te) {
                    sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
                }
            }
        } else {
            sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
        }
    }
}
