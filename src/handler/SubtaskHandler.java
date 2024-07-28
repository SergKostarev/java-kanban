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
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            super.sendText(exchange, gson.toJson(super.taskManager.getAllSubtasks()), 200);
        } else if (exchange.getRequestMethod().equals("GET")
                && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Subtask subtask = super.taskManager.getSubtask(id);
                super.sendText(exchange, gson.toJson(subtask), 200);
            } catch (NotFoundException te) {
                super.sendTaskException(exchange, te, 404);
            } catch (Exception e) {
                super.sendNoEndpoint(exchange);
            }
        } else if (exchange.getRequestMethod().equals("DELETE")
                && pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            try {
                Integer id = Integer.parseInt(pathParts[2]);
                Subtask subtask = super.taskManager.removeSubtask(id);
                super.sendText(exchange, gson.toJson(subtask), 200);
            } catch (Exception e) {
                super.sendNoEndpoint(exchange);
            }
        } else if (exchange.getRequestMethod().equals("POST") && pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            String json = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(json, Subtask.class);
            if (subtask.getId() == null) {
                try {
                    Subtask subtaskOutput = taskManager.addSubtask(subtask);
                    super.sendText(exchange, gson.toJson(subtaskOutput), 201);
                } catch (IntersectionException te) {
                    super.sendTaskException(exchange, te, 406);
                } catch (NotFoundException te) {
                    super.sendTaskException(exchange, te, 404);
                }
            } else {
                try {
                    Subtask subtaskOutput = taskManager.updateSubtask(subtask);
                    super.sendText(exchange, gson.toJson(subtaskOutput), 201);
                } catch (IntersectionException te) {
                    super.sendTaskException(exchange, te, 406);
                } catch (UpdateException | NotFoundException | IdentifierException te) {
                    super.sendTaskException(exchange, te, 404);
                }
            }
        } else {
            super.sendNoEndpoint(exchange);
        }
    }
}
