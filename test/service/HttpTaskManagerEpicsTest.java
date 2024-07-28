package service;

import adapter.EpicTypeToken;
import adapter.SubtaskTypeToken;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest extends HttpTaskManagerTest {

    public HttpTaskManagerEpicsTest() throws IOException {
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.IN_PROGRESS, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(25), LocalDateTime.of(2024, 7, 20, 21, 55));
        String subtaskJson = gson.toJson(subtask);
        String subtaskJson2 = gson.toJson(subtask2);
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/subtasks");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
            client.send(request3, HttpResponse.BodyHandlers.ofString());
            URI url3 = URI.create("http://localhost:8080/epics/1");
            HttpRequest request4 = HttpRequest.newBuilder().uri(url3).GET().build();
            response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        Epic taskDeserialized = gson.fromJson(response.body(), Epic.class);
        assertEquals(1, taskDeserialized.getId(), "Некорректный идентификатор задачи");
        assertEquals("Test", taskDeserialized.getName(), "Некорректное имя задачи");
        assertEquals("Testing epic", taskDeserialized.getDescription(), "Некорректное описание задачи");
        assertEquals(TaskStatus.IN_PROGRESS, taskDeserialized.getStatus(), "Некорректный статус задачи");
        assertEquals(Duration.ofMinutes(30), taskDeserialized.getDuration(), "Некорректная продолжительность задачи");
        assertEquals(LocalDateTime.of(2024, 7, 20, 15, 55), taskDeserialized.getStartTime(), "Некорректное время начала задачи");
        assertEquals(LocalDateTime.of(2024, 7, 20, 22, 20), taskDeserialized.getEndTime(), "Некорректное время окончания задачи");
    }

    @Test
    public void testShouldNotGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/epics/2");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
            response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Epic epic2 = new Epic("Test 2", "Testing epic",
                new ArrayList<>());
        String epicJson = gson.toJson(epic);
        String epicJson2 = gson.toJson(epic2);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Epic> taskListDeserialized = gson.fromJson(response.body(), new EpicTypeToken().getType());
        assertNotNull(taskListDeserialized, "Задачи не возвращаются");
        assertEquals(2, taskListDeserialized.size(), "Некорректное количество задач");
        assertEquals("Test", taskListDeserialized.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Test 2", taskListDeserialized.getLast().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.IN_PROGRESS, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(25), LocalDateTime.of(2024, 7, 20, 21, 55));
        String subtaskJson = gson.toJson(subtask);
        String subtaskJson2 = gson.toJson(subtask2);
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/subtasks");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).build();
            client.send(request3, HttpResponse.BodyHandlers.ofString());
            URI url3 = URI.create("http://localhost:8080/epics/1/subtasks");
            HttpRequest request4 = HttpRequest.newBuilder().uri(url3).GET().build();
            response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Subtask> taskListDeserialized = gson.fromJson(response.body(), new SubtaskTypeToken().getType());
        assertNotNull(taskListDeserialized, "Задачи не возвращаются");
        assertEquals(2, taskListDeserialized.size(), "Некорректное количество задач");
        assertEquals(2, taskListDeserialized.getFirst().getId(), "Некорректный идентификатор задачи");
        assertEquals(3, taskListDeserialized.getLast().getId(), "Некорректный идентификатор задачи");
    }
}
