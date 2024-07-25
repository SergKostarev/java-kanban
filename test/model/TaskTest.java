package model;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void shouldReturnEqualTasksIfIdIsEqual() {
        Task task1 = new Task(1, "Some task", "Some description", TaskStatus.NEW, Duration.ofMinutes(0), null);
        Task task2 = new Task(1, "Another task", "Another description", TaskStatus.NEW, Duration.ofMinutes(0), null);
        Assertions.assertEquals(task1, task2, "Tasks with equal identifier are not equal");
    }

}