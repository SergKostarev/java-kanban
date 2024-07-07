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
import java.util.ArrayList;
import java.util.List;

import static service.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest {
    private static FileBackedTaskManager tm;

    @BeforeEach
    public void prepare() {
        File file;
        try {
            file = File.createTempFile("test_manager", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tm = Managers.getFileBackedTaskManager(file);
        tm.addTask(new Task("Закончить пятый спринт", "Нет описания", TaskStatus.NEW));
        tm.addEpic(new Epic("Провести уборку", "До 18 мая", new ArrayList<>()));
        tm.addSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.NEW, 2));
    }

    @Test
    public void shouldReturnNonNullTasks() {
        Assertions.assertNotNull(tm.getTask(1), "Task is null");
        Assertions.assertNotNull(tm.getEpic(2), "Epic is null");
        Assertions.assertNotNull(tm.getSubtask(3), "Subtask is null");
    }

    @Test
    public void shouldReturnImmutableTaskAfterAdding() {
        Task task = new Task(4, "Сходить в магазин", "Нет описания", TaskStatus.NEW);
        Task taskCopy = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
        tm.addTask(task);
        Task taskAfter = tm.getTask(4);
        Assertions.assertNotNull(taskAfter, "Task is null");
        Assertions.assertEquals(taskCopy.getId(), taskAfter.getId(), "Task identifiers are not equal");
        Assertions.assertEquals(taskCopy.getName(), taskAfter.getName(), "Task names are not equal");
        Assertions.assertEquals(taskCopy.getDescription(), taskAfter.getDescription(), "Task descriptions are not equal");
        Assertions.assertEquals(taskCopy.getStatus(), taskAfter.getStatus(), "Task statuses are not equal");
    }

    @Test
    public void shouldReturnFirstVersionAfterTaskUpdate() {
        Task task = new Task(4, "Сходить в магазин", "Нет описания", TaskStatus.NEW);
        Task taskCopy = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
        tm.addTask(task);
        tm.getTask(1);
        tm.getTask(4);
        tm.updateTask(new Task(4, "Погулять с собакой", "Описание", TaskStatus.IN_PROGRESS));
        List<Task> history = tm.getHistory();
        Task taskAfter =  history.get(1);
        Assertions.assertEquals(taskCopy.getId(), taskAfter.getId(), "Task identifiers are not equal");
        Assertions.assertEquals(taskCopy.getName(), taskAfter.getName(), "Task names are not equal");
        Assertions.assertEquals(taskCopy.getDescription(), taskAfter.getDescription(), "Task descriptions are not equal");
        Assertions.assertEquals(taskCopy.getStatus(), taskAfter.getStatus(), "Task statuses are not equal");
    }

    @Test
    public void shouldReturnTwoElementsInTaskHistoryAfterRepeatedGetting() {
        Task task = new Task("Сходить в магазин", "Нет описания", TaskStatus.NEW);
        tm.addTask(task);
        tm.getTask(1);
        tm.getTask(4);
        tm.getTask(1);
        List<Task> history = tm.getHistory();
        Assertions.assertEquals(2, history.size(), "History list size differs from 2");
    }

    @Test
    public void shouldReturnFirstTaskInTaskHistoryAfterRepeatedGetting() {
        Task task = new Task("Сходить в магазин", "Нет описания", TaskStatus.NEW);
        tm.addTask(task);
        tm.getTask(1);
        tm.getTask(4);
        tm.getTask(1);
        List<Task> history = tm.getHistory();
        Assertions.assertEquals(history.get(1).getId(), 1, "Last task id differs from 1");
    }

    @Test
    public void shouldIgnoreGivenIdWhenAddingTask() {
        Task task = new Task(10, "Сходить в магазин", "Нет описания", TaskStatus.NEW);
        Assertions.assertEquals(4, tm.addTask(task).getId(), "Task identifier is not equal to 4");
    }

    @Test
    public void shouldNotUpdateSubtaskWithEqualIdAndEpicId() {
        Subtask subtask = tm.updateSubtask(new Subtask(3, "Вымыть пол", "Нет описания", TaskStatus.DONE, 3));
        Assertions.assertNull(subtask, "Subtask with equal identifier and epic identifier was updated");
    }

    @Test
    public void shouldNotUpdateEpicWithSubtaskIdListContainingEpicId() {
        List <Integer> subtasksId = new ArrayList<>();
        subtasksId.add(2);
        Epic epic = tm.updateEpic(new Epic(2,"Изучить Java", "Нет описания", subtasksId));
        Assertions.assertNull(epic, "Epic with subtask identifiers list containing epic identifier was updated");
    }

    @Test
    public void shouldReturnNoOldSubtasksInEpic() {
        Subtask subtask = new Subtask("Сходить в магазин", "Нет описания", TaskStatus.NEW, 2);
        tm.addSubtask(subtask);
        tm.removeSubtask(4);
        Assertions.assertEquals(tm.getEpic(2).getSubtasksId().size(), 1, "Subtask list size of epic with id 2 is not equal to 1");
        Assertions.assertEquals(tm.getEpic(2).getSubtasksId().getFirst(), 3, "Subtask id of epic with id 2 is not 3");
        tm.removeAllSubtasks();
        Assertions.assertTrue(tm.getEpic(2).getSubtasksId().isEmpty(), "Subtask list of epic with id 2 is not empty");
    }

    @BeforeEach
    public void shouldCreateFileWithTasks() {
        Assertions.assertTrue(tm.getFile().isFile(), "File doesn't exist");
    }

    @Test
    public void shouldDeserializeTasksAfterAdding() {
        FileBackedTaskManager newTm;
        try {
            newTm = loadFromFile(tm.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(newTm.getAllEpics().size(), 1,  "Size of epic list differs from 1");
        Assertions.assertEquals(newTm.getAllTasks().size(), 1,  "Size of task list differs from 1");
        Assertions.assertEquals(newTm.getAllSubtasks().size(), 1,  "Size of subtask list differs from 1");
    }

    @Test
    public void shouldDeserializeTasksAfterRemoving() {
        tm.removeAllTasks();
        tm.removeAllEpics();
        tm.removeAllSubtasks();
        FileBackedTaskManager newTm;
        try {
            newTm = loadFromFile(tm.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(newTm.getAllEpics().isEmpty(),   "Epic list is not empty");
        Assertions.assertTrue(newTm.getAllTasks().isEmpty(),  "Task list is not empty");
        Assertions.assertTrue(newTm.getAllSubtasks().isEmpty(),  "Subtask list is not empty");
    }

    @Test
    public void shouldBeEqualTasksAfterSerializingAndDeserializing() {
        FileBackedTaskManager newTm;
        try {
            newTm = loadFromFile(tm.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(newTm.getEpic(2), tm.getEpic(2),
                "Epics with id 2 in InMemoryTaskManager and FileBackedTaskManager are not equal");
        Assertions.assertEquals(newTm.getTask(1), tm.getTask(1),
                "Tasks with id 1 in InMemoryTaskManager and FileBackedTaskManager are not equal");
        Assertions.assertEquals(newTm.getSubtask(3), tm.getSubtask(3),
                "Subtasks with id 3 in InMemoryTaskManager and FileBackedTaskManager are not equal");
    }

}
