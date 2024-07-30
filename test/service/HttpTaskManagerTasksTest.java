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

public class HttpTaskManagerTasksTest extends HttpTaskManagerTest {

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String taskJson = gson.toJson(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testShouldNotAddIntersectedTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Task task2 = new Task("Test2", "Testing task2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
            response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(406, response.statusCode());
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Task task2 = new Task(1, "Test update", "Testing task update",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String taskJson2 = gson.toJson(task2);
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> response2;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
            response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response2.statusCode());
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test update", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testShouldNotUpdateIntersectedTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 18, 55));
        Task task3 = new Task(2, "Test 3", "Testing task 3",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String taskJson3 = gson.toJson(task3);
        String taskJson2 = gson.toJson(task2);
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> response3;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();
            response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(406, response3.statusCode());
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Test 2", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String taskJson = gson.toJson(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/tasks/1");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
            response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        Task taskDeserialized = gson.fromJson(response.body(), Task.class);
        assertEquals(1, taskDeserialized.getId(), "Некорректный идентификатор задачи");
        assertEquals("Test", taskDeserialized.getName(), "Некорректное имя задачи");
        assertEquals("Testing task", taskDeserialized.getDescription(), "Некорректное описание задачи");
        assertEquals(TaskStatus.NEW, taskDeserialized.getStatus(), "Некорректный статус задачи");
        assertEquals(Duration.ofMinutes(5), taskDeserialized.getDuration(), "Некорректная продолжительность задачи");
        assertEquals(LocalDateTime.of(2024, 7, 20, 15, 55), taskDeserialized.getStartTime(), "Некорректное время начала задачи");
    }

    @Test
    public void testShouldNotGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String taskJson = gson.toJson(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/tasks/2");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
            response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 18, 55));
        String taskJson2 = gson.toJson(task2);
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Task> taskListDeserialized = gson.fromJson(response.body(), new TaskTypeToken().getType());
        assertNotNull(taskListDeserialized, "Задачи не возвращаются");
        assertEquals(2, taskListDeserialized.size(), "Некорректное количество задач");
        assertEquals("Test", taskListDeserialized.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Test 2", taskListDeserialized.get(1).getName(), "Некорректное имя задачи");
    }
}