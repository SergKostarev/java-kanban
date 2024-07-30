package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.ErrorDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        byte[] resp = response.getBytes(DEFAULT_CHARSET);
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
        exchange.close();
    }

    protected ErrorDTO getErrorMessage (String message, Integer id, int statusCode) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.getDetails().put(statusCode, message + ", id: " + id);
        return errorDTO;
    }
}
