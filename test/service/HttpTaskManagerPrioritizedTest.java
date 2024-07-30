package service;

import adapter.TaskTypeToken;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTest extends HttpTaskManagerTest {

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 18, 55));
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String taskJson2 = gson.toJson(task2);
        String taskJson = gson.toJson(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/prioritized");
            HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Task> taskListDeserialized = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertNotNull(taskListDeserialized, "Задачи не возвращаются");
        assertEquals(2, taskListDeserialized.size(), "Некорректное количество задач");
        assertEquals("Test 2", taskListDeserialized.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Test", taskListDeserialized.get(1).getName(), "Некорректное имя задачи");
    }
}
