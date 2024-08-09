package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            sendResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
        } else {
            sendResponse(exchange, gson.toJson(getErrorMessage("Эндпойнт не существует", null, 404)), 404);
        }
    }
}
