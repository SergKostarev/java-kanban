package service;

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

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void prepare() {
        tm = Managers.getDefault();
        tm.addTask(new Task("Закончить пятый спринт", "Нет описания", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 7, 18, 17, 55)));
        tm.addEpic(new Epic("Провести уборку", "До 18 мая", new ArrayList<>()));
        tm.addSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.NEW, 2,
                Duration.ofMinutes(180), LocalDateTime.of(2024, 7, 20, 15, 55)));
    }

}