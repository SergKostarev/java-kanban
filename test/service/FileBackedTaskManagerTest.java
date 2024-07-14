package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static service.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void prepare() {
        File file = Assertions.assertDoesNotThrow(() -> File.createTempFile("test_manager", ".csv"));
        tm = Managers.getFileBackedTaskManager(file);
        tm.addTask(new Task("Закончить пятый спринт", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 7, 18, 17, 55)));
        tm.addEpic(new Epic("Провести уборку", "До 18 мая", new ArrayList<>()));
        tm.addSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.NEW, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55)));
    }

    @BeforeEach
    public void shouldCreateFileWithTasks() {
        Assertions.assertDoesNotThrow(() -> tm.getFile().isFile());
    }

    @Test
    public void shouldDeserializeTasksAfterAdding() {
        FileBackedTaskManager newTm = Assertions.assertDoesNotThrow(() -> loadFromFile(tm.getFile()));
        Assertions.assertEquals(newTm.getAllEpics().size(), 1,  "Size of epic list differs from 1");
        Assertions.assertEquals(newTm.getAllTasks().size(), 1,  "Size of task list differs from 1");
        Assertions.assertEquals(newTm.getAllSubtasks().size(), 1,  "Size of subtask list differs from 1");
    }

    @Test
    public void shouldDeserializeTasksAfterRemoving() {
        tm.removeAllTasks();
        tm.removeAllEpics();
        tm.removeAllSubtasks();
        FileBackedTaskManager newTm = Assertions.assertDoesNotThrow(() -> loadFromFile(tm.getFile()));
        Assertions.assertTrue(newTm.getAllEpics().isEmpty(),   "Epic list is not empty");
        Assertions.assertTrue(newTm.getAllTasks().isEmpty(),  "Task list is not empty");
        Assertions.assertTrue(newTm.getAllSubtasks().isEmpty(),  "Subtask list is not empty");
    }

    @Test
    public void shouldBeEqualTasksAfterSerializingAndDeserializing() {
        FileBackedTaskManager newTm = Assertions.assertDoesNotThrow(() -> loadFromFile(tm.getFile()));
        Assertions.assertEquals(newTm.getEpic(2), tm.getEpic(2),
                "Epics with id 2 in InMemoryTaskManager and FileBackedTaskManager are not equal");
        Assertions.assertEquals(newTm.getTask(1), tm.getTask(1),
                "Tasks with id 1 in InMemoryTaskManager and FileBackedTaskManager are not equal");
        Assertions.assertEquals(newTm.getSubtask(3), tm.getSubtask(3),
                "Subtasks with id 3 in InMemoryTaskManager and FileBackedTaskManager are not equal");
    }

}
