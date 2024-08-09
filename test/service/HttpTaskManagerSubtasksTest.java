package service;

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

public class HttpTaskManagerSubtasksTest extends HttpTaskManagerTest {

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String subtaskJson = gson.toJson(subtask);
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/subtasks");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
            response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testShouldNotAddIntersectedSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
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
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(406, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Subtask subtask2 = new Subtask(2, "Test 3", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
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
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testShouldNotUpdateIntersectedSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 18, 55));
        Subtask subtask3 = new Subtask(3, "Test 4", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String subtaskJson = gson.toJson(subtask);
        String subtaskJson2 = gson.toJson(subtask2);
        String subtaskJson3 = gson.toJson(subtask3);
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
            HttpRequest request4 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson3)).build();
            response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(406, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", subtasksFromManager.getLast().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String subtaskJson = gson.toJson(subtask);
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/subtasks");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            URI url3 = URI.create("http://localhost:8080/subtasks/2");
            HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        Subtask taskDeserialized = gson.fromJson(response.body(), Subtask.class);
        assertEquals(2, taskDeserialized.getId(), "Некорректный идентификатор задачи");
        assertEquals("Test 2", taskDeserialized.getName(), "Некорректное имя задачи");
        assertEquals("Testing subtask", taskDeserialized.getDescription(), "Некорректное описание задачи");
        assertEquals(TaskStatus.NEW, taskDeserialized.getStatus(), "Некорректный статус задачи");
        assertEquals(Duration.ofMinutes(5), taskDeserialized.getDuration(), "Некорректная продолжительность задачи");
        assertEquals(LocalDateTime.of(2024, 7, 20, 15, 55), taskDeserialized.getStartTime(), "Некорректное время начала задачи");
    }

    @Test
    public void testShouldNotGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        String subtaskJson = gson.toJson(subtask);
        String epicJson = gson.toJson(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            URI url2 = URI.create("http://localhost:8080/subtasks");
            HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            URI url3 = URI.create("http://localhost:8080/subtasks/3");
            HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
            response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic",
                new ArrayList<>());
        Subtask subtask = new Subtask("Test 2", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 15, 55));
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask",
                TaskStatus.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2024, 7, 20, 18, 55));
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
            HttpRequest request4 = HttpRequest.newBuilder().uri(url2).GET().build();
            response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(200, response.statusCode());
        List<Subtask> taskListDeserialized = gson.fromJson(response.body(), new SubtaskTypeToken().getType());
        assertNotNull(taskListDeserialized, "Задачи не возвращаются");
        assertEquals(2, taskListDeserialized.size(), "Некорректное количество задач");
        assertEquals("Test 2", taskListDeserialized.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Test 3", taskListDeserialized.getLast().getName(), "Некорректное имя задачи");
    }
}
