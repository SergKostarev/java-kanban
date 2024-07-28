package service;

import exception.IntersectionException;
import exception.UpdateException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T tm;

    @BeforeEach
    public abstract void prepare();

    public void initTasks() {
        assertDoesNotThrow(() -> tm.addTask(new Task("Закончить пятый спринт", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 7, 18, 17, 55))));
        assertDoesNotThrow(() -> tm.addEpic(new Epic("Провести уборку", "До 18 мая", new ArrayList<>())));
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.NEW, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55))));
    }

    @Test
    public void shouldReturnNonNullTasks() {
        assertDoesNotThrow(() -> tm.getTask(1));
        assertDoesNotThrow(() -> tm.getEpic(2));
        assertDoesNotThrow(() -> tm.getSubtask(3));
    }

    @Test
    public void shouldReturnImmutableTaskAfterAdding() {
        Task task = new Task(4, "Сходить в магазин", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 18, 17, 55));
        Task taskCopy = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus(),
                task.getDuration(), task.getStartTime());
        assertDoesNotThrow(() -> tm.addTask(task));
        Task taskAfter = assertDoesNotThrow(() -> tm.getTask(4));
        Assertions.assertNotNull(taskAfter, "Task is null");
        Assertions.assertEquals(taskCopy.getId(), taskAfter.getId(), "Task identifiers are not equal");
        Assertions.assertEquals(taskCopy.getName(), taskAfter.getName(), "Task names are not equal");
        Assertions.assertEquals(taskCopy.getDescription(), taskAfter.getDescription(), "Task descriptions are not equal");
        Assertions.assertEquals(taskCopy.getStatus(), taskAfter.getStatus(), "Task statuses are not equal");
        Assertions.assertEquals(taskCopy.getStartTime(), taskAfter.getStartTime(), "Task start times are not equal");
        Assertions.assertEquals(taskCopy.getDuration(), taskAfter.getDuration(), "Task durations are not equal");
    }

    @Test
    public void shouldReturnFirstVersionAfterTaskUpdate() {
        Task task = new Task(4, "Сходить в магазин", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 8, 18, 17, 55));
        Task taskCopy = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus(),
                task.getDuration(), task.getStartTime());
        assertDoesNotThrow(() -> tm.addTask(task));
        assertDoesNotThrow(() -> tm.getTask(1));
        assertDoesNotThrow(() -> tm.getTask(4));
        assertDoesNotThrow(() -> tm.updateTask(new Task(4, "Погулять с собакой", "Описание", TaskStatus.IN_PROGRESS, Duration.ofMinutes(0), null)));
        List<Task> history = tm.getHistory();
        Task taskAfter =  history.get(1);
        Assertions.assertEquals(taskCopy.getId(), taskAfter.getId(), "Task identifiers are not equal");
        Assertions.assertEquals(taskCopy.getName(), taskAfter.getName(), "Task names are not equal");
        Assertions.assertEquals(taskCopy.getDescription(), taskAfter.getDescription(), "Task descriptions are not equal");
        Assertions.assertEquals(taskCopy.getStatus(), taskAfter.getStatus(), "Task statuses are not equal");
        Assertions.assertEquals(taskCopy.getStartTime(), taskAfter.getStartTime(), "Task start times are not equal");
        Assertions.assertEquals(taskCopy.getDuration(), taskAfter.getDuration(), "Task durations are not equal");
    }

    @Test
    public void shouldReturnTwoElementsInTaskHistoryAfterRepeatedGetting() {
        Task task = new Task("Сходить в магазин", "Нет описания", TaskStatus.NEW, Duration.ofMinutes(0), null);
        assertDoesNotThrow(() -> tm.addTask(task));
        assertDoesNotThrow(() -> tm.getTask(1));
        assertDoesNotThrow(() -> tm.getTask(4));
        assertDoesNotThrow(() -> tm.getTask(1));
        List<Task> history = tm.getHistory();
        Assertions.assertEquals(2, history.size(), "History list size differs from 2");
    }

    @Test
    public void shouldReturnFirstTaskInTaskHistoryAfterRepeatedGetting() {
        Task task = new Task("Сходить в магазин", "Нет описания", TaskStatus.NEW, Duration.ofMinutes(0), null);
        assertDoesNotThrow(() -> tm.addTask(task));
        assertDoesNotThrow(() -> tm.getTask(1));
        assertDoesNotThrow(() -> tm.getTask(4));
        assertDoesNotThrow(() -> tm.getTask(1));
        List<Task> history = tm.getHistory();
        Assertions.assertEquals(history.get(1).getId(), 1, "Last task id differs from 1");
    }

    @Test
    public void shouldIgnoreGivenIdWhenAddingTask() {
        Task task = new Task(10, "Сходить в магазин", "Нет описания", TaskStatus.NEW, Duration.ofMinutes(0), null);
        Assertions.assertEquals(4, assertDoesNotThrow(() -> tm.addTask(task)).getId(),
                "Task identifier is not equal to 4");
    }

    @Test
    public void shouldNotUpdateSubtaskWithEqualIdAndEpicId() {
        Assertions.assertThrows(UpdateException.class, () -> tm.updateSubtask(new Subtask(3, "Вымыть пол", "Нет описания",
                TaskStatus.DONE, 3, Duration.ofMinutes(0), null)));
    }

    @Test
    public void shouldReturnNoOldSubtasksInEpic() {
        Subtask subtask = new Subtask("Сходить в магазин", "Нет описания", TaskStatus.NEW, 2, Duration.ofMinutes(0), null);
        assertDoesNotThrow(() -> tm.addSubtask(subtask));
        assertDoesNotThrow(() -> tm.removeSubtask(4));
        Assertions.assertEquals(assertDoesNotThrow(() -> tm.getEpic(2)).getSubtasksId().size(),
                1, "Subtask list size of epic with id 2 is not equal to 1");
        Assertions.assertEquals(assertDoesNotThrow(() -> tm.getEpic(2)).getSubtasksId().getFirst(),
                3, "Subtask id of epic with id 2 is not 3");
        tm.removeAllSubtasks();
        Assertions.assertTrue(assertDoesNotThrow(() -> tm.getEpic(2)).getSubtasksId().isEmpty(),
                "Subtask list of epic with id 2 is not empty");
    }

    @Test
    public void shouldNotAddTimeCrossingTask() {
        tm.removeAllTasks();
        tm.removeAllSubtasks();
        tm.removeAllEpics();
        assertDoesNotThrow(() -> tm.addTask(new Task("Закончить восьмой спринт", "Нет описания",
                TaskStatus.NEW, Duration.ofMinutes(300), LocalDateTime.of(2024, 7, 13, 17, 55))));
        Assertions.assertThrows(IntersectionException.class, () -> tm.addTask(new Task("Закончить девятый спринт", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(300), LocalDateTime.of(2024, 7, 13, 19, 55))));
        Assertions.assertEquals(1, tm.getAllTasks().size(), "Task list size is not equal to 1");
    }

    @Test
    public void shouldAddNonTimeCrossingTask() {
        tm.removeAllTasks();
        tm.removeAllSubtasks();
        tm.removeAllEpics();
        assertDoesNotThrow(() -> tm.addTask(new Task("Закончить восьмой спринт", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 7, 13, 17, 55))));
        assertDoesNotThrow(() -> tm.addTask(new Task("Закончить девятый спринт", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 7, 13, 23, 55))));
        Assertions.assertEquals(2, tm.getAllTasks().size(), "Task list size is not equal to 2");
    }

    @Test
    public void epicStatusShouldBeNew() {
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть посуду", "Нет описания", TaskStatus.NEW, 2,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 7, 25, 12, 0))));
        Assertions.assertEquals(TaskStatus.NEW, assertDoesNotThrow(() -> tm.getEpic(2)).getStatus(),
                "Epic status is not new");
    }

    @Test
    public void epicStatusShouldBeDone() {
        assertDoesNotThrow(() -> tm.updateSubtask(new Subtask(3, "Вымыть пол", "Нет описания", TaskStatus.DONE, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55))));
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть посуду", "Нет описания", TaskStatus.DONE, 2,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 7, 25, 12, 0))));
        Assertions.assertEquals(TaskStatus.DONE,
                assertDoesNotThrow(() -> tm.getEpic(2)).getStatus(),"Epic status is not done");
    }

    @Test
    public void epicStatusShouldBeInProgress() {
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть посуду", "Нет описания", TaskStatus.DONE, 2,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 7, 25, 12, 0))));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS,
                assertDoesNotThrow(() -> tm.getEpic(2)).getStatus(), "Epic status is not in progress");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenAllTasksAreInProgress() {
        assertDoesNotThrow(() -> tm.updateSubtask(new Subtask(3, "Вымыть пол", "Нет описания", TaskStatus.IN_PROGRESS, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55))));
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть посуду", "Нет описания", TaskStatus.IN_PROGRESS, 2,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 7, 25, 12, 0))));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS,
                assertDoesNotThrow(() -> tm.getEpic(2)).getStatus(), "Epic status is not in progress");
    }

    @Test
    public void epicStatusShouldBeNewWhenAllSubtasksAreDeleted() {
        assertDoesNotThrow(() -> tm.updateSubtask(new Subtask(3, "Вымыть пол", "Нет описания", TaskStatus.IN_PROGRESS, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55))));
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть посуду", "Нет описания", TaskStatus.IN_PROGRESS, 2,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 7, 25, 12, 0))));
        tm.removeAllSubtasks();
        Assertions.assertEquals(TaskStatus.NEW,
                assertDoesNotThrow(() -> tm.getEpic(2)).getStatus(), "Epic status is not new");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenAllDoneSubtaskIsDeleted() {
        assertDoesNotThrow(() -> tm.updateSubtask(new Subtask(3, "Вымыть пол", "Нет описания", TaskStatus.DONE, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55))));
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть посуду", "Нет описания", TaskStatus.IN_PROGRESS, 2,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 7, 25, 12, 0))));
        assertDoesNotThrow(() -> tm.removeSubtask(3));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS,
                assertDoesNotThrow(() -> tm.getEpic(2)).getStatus(), "Epic status is not in progress");
    }

    @Test
    public void epicShouldHaveCorrectTimeCharacteristics() {
        assertDoesNotThrow(() -> tm.updateSubtask(new Subtask(3, "Вымыть пол", "Нет описания", TaskStatus.DONE, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55))));
        assertDoesNotThrow(() -> tm.addSubtask(new Subtask("Вымыть посуду", "Нет описания", TaskStatus.IN_PROGRESS, 2,
                Duration.ofMinutes(20), LocalDateTime.of(2024, 7, 25, 12, 0))));
        Assertions.assertEquals(Duration.ofMinutes(200),
                assertDoesNotThrow(() -> tm.getEpic(2)).getDuration(),
                "Epic duration is not equal to 200");
        Assertions.assertEquals(LocalDateTime.of(2024, 7, 20, 15, 55),
                assertDoesNotThrow(() -> tm.getEpic(2)).getStartTime(),
                "Wrong epic start time");
        Assertions.assertEquals(LocalDateTime.of(2024, 7, 25, 12, 20),
                assertDoesNotThrow(() -> tm.getEpic(2)).getEndTime(), "Wrong epic end time");
    }

    @Test
    public void historyShouldBeEmpty() {
        tm.removeAllEpics();
        tm.removeAllTasks();
        tm.removeAllSubtasks();
        Assertions.assertTrue(tm.getHistory().isEmpty(), "History is not empty");
    }

    @Test
    public void historyShouldNotContainDuplicatedTasks() {
        assertDoesNotThrow(() -> tm.getTask(1));
        assertDoesNotThrow(() -> tm.getTask(1));
        Assertions.assertEquals(1, tm.getHistory().size(), "History size is not equal to 1");
    }

}
