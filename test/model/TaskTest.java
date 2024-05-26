package model;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void shouldReturnEqualTasksIfIdIsEqual() {
        Task task1 = new Task(1, "Some task", "Some description", TaskStatus.NEW);
        Task task2 = new Task(1, "Another task", "Another description", TaskStatus.NEW);
        Assertions.assertEquals(task1, task2, "Tasks with equal identifier are not equal");
    }

}