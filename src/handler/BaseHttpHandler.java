package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskException;
import service.ErrorDTO;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected TaskManager taskManager;

    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        sendResponse(exchange, response, statusCode);
    }

    public void sendTaskException(HttpExchange exchange, TaskException exception, int statusCode) throws IOException {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.getDetails().put(exception.getMessage(), exception.getId());
        sendResponse(exchange, "Объект не найден", statusCode);
    }

    public void sendNoEndpoint(HttpExchange exchange) throws IOException {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.getDetails().put("Эндпойнт не существует", null);
        sendResponse(exchange, gson.toJson(errorDTO), 404);
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
    }
}
