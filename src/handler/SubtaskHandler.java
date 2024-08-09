package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IdentifierException;
import exception.IntersectionException;
import exception.NotFoundException;
import exception.UpdateException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            sendResponse(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
        } else if (exchange.getRequestMethod().equals("GET")
                && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Subtask subtask = taskManager.getSubtask(id);
                sendResponse(exchange, gson.toJson(subtask), 200);
            } catch (NotFoundException te) {
                sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
            } catch (Exception e) {
                sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")
                && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Subtask subtask = taskManager.removeSubtask(id);
                sendResponse(exchange, gson.toJson(subtask), 200);
            } catch (Exception e) {
                sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
            }
        } else if (exchange.getRequestMethod().equals("POST") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            String json = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(json, Subtask.class);
            if (subtask.getId() == null) {
                try {
                    Subtask subtaskOutput = taskManager.addSubtask(subtask);
                    sendResponse(exchange, gson.toJson(subtaskOutput), 201);
                } catch (IntersectionException te) {
                    sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 406)), 406);
                } catch (NotFoundException te) {
                    sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
                }
            } else {
                try {
                    Subtask subtaskOutput = taskManager.updateSubtask(subtask);
                    sendResponse(exchange, gson.toJson(subtaskOutput), 201);
                } catch (IntersectionException te) {
                    sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 406)), 406);
                } catch (UpdateException | NotFoundException | IdentifierException te) {
                    sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
                }
            }
        } else {
            sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
        }
    }
}
