package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IdentifierException;
import exception.NotFoundException;
import model.Epic;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 4
                && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                List<Subtask> epic = taskManager.getEpicSubtasks(id);
                sendResponse(exchange, gson.toJson(epic), 200);
            } catch (NotFoundException te) {
                sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
            } catch (Exception e) {
                sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
            }
        } else if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2 && pathParts[1].equals("epics")) {
            sendResponse(exchange, gson.toJson(taskManager.getAllEpics()), 200);
        } else if (exchange.getRequestMethod().equals("GET")
                && pathParts.length == 3 && pathParts[1].equals("epics")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.getEpic(id);
                sendResponse(exchange, gson.toJson(epic), 200);
            } catch (NotFoundException te) {
                sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
            } catch (Exception e) {
                sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")
                && pathParts.length == 3 && pathParts[1].equals("epics")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.removeEpic(id);
                sendResponse(exchange, gson.toJson(epic), 200);
            } catch (Exception e) {
                sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
            }
        } else if (exchange.getRequestMethod().equals("POST") && pathParts.length == 2 && pathParts[1].equals("epics")) {
            String json = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(json, Epic.class);
            try {
                Epic epicOutput = taskManager.addEpic(epic);
                sendResponse(exchange, gson.toJson(epicOutput), 201);
            } catch (NotFoundException | IdentifierException te) {
                sendResponse(exchange, gson.toJson(getErrorMessage(te.getMessage(), te.getId(), 404)), 404);
            }
        } else {
            sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
        }
    }
}
