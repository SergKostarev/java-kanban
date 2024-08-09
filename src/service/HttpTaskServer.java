package service;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final HttpServer httpServer;

    public Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = getGsonBuilder();
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager,gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager,gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager,gson));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    private Gson getGsonBuilder() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public Gson getGson() {
        return gson;
    }
}
