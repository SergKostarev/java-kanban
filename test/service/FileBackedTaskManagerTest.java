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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static service.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void prepare() {
        File file = assertDoesNotThrow(() -> File.createTempFile("test_manager", ".csv"));
        tm = Managers.getFileBackedTaskManager(file);
        super.initTasks();
    }

    @Test
    public void shouldCreateFileWithTasks() {
        assertDoesNotThrow(() -> tm.getFile().isFile());
    }

    @Test
    public void shouldDeserializeTasksAfterAdding() {
        FileBackedTaskManager newTm = assertDoesNotThrow(() -> loadFromFile(tm.getFile()));
        Assertions.assertEquals(newTm.getAllEpics().size(), 1,  "Size of epic list differs from 1");
        Assertions.assertEquals(newTm.getAllTasks().size(), 1,  "Size of task list differs from 1");
        Assertions.assertEquals(newTm.getAllSubtasks().size(), 1,  "Size of subtask list differs from 1");
    }

    @Test
    public void shouldDeserializeTasksAfterRemoving() {
        tm.removeAllTasks();
        tm.removeAllEpics();
        tm.removeAllSubtasks();
        FileBackedTaskManager newTm = assertDoesNotThrow(() -> loadFromFile(tm.getFile()));
        Assertions.assertTrue(newTm.getAllEpics().isEmpty(),   "Epic list is not empty");
        Assertions.assertTrue(newTm.getAllTasks().isEmpty(),  "Task list is not empty");
        Assertions.assertTrue(newTm.getAllSubtasks().isEmpty(),  "Subtask list is not empty");
    }

    @Test
    public void shouldBeEqualTasksAfterSerializingAndDeserializing() {
        FileBackedTaskManager newTm = assertDoesNotThrow(() -> loadFromFile(tm.getFile()));
        Epic newTmEpic = assertDoesNotThrow(() -> newTm.getEpic(2));
        Epic tmEpic = assertDoesNotThrow(() -> tm.getEpic(2));
        Assertions.assertEquals(newTmEpic, tmEpic,
                "Epics with id 2 in InMemoryTaskManager and FileBackedTaskManager are not equal");
        Task newTmTask = assertDoesNotThrow(() -> newTm.getTask(1));
        Task tmTask = assertDoesNotThrow(() -> tm.getTask(1));
        Assertions.assertEquals(newTmTask, tmTask,
                "Tasks with id 1 in InMemoryTaskManager and FileBackedTaskManager are not equal");
        Subtask newTmSubtask = assertDoesNotThrow(() -> newTm.getSubtask(3));
        Subtask tmSubtask = assertDoesNotThrow(() -> tm.getSubtask(3));
        Assertions.assertEquals(newTmSubtask, tmSubtask,
                "Subtasks with id 3 in InMemoryTaskManager and FileBackedTaskManager are not equal");
    }

}
