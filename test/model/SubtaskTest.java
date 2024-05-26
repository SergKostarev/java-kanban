package model;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    public void shouldReturnEqualSubtasksIfIdIsEqual() {
        Subtask subtask1 = new Subtask(1, "Some task", "Some description", TaskStatus.NEW,2);
        Subtask subtask2 = new Subtask(1, "Another task", "Another description", TaskStatus.NEW,2);
        Assertions.assertEquals(subtask1, subtask2, "Tasks with equal identifier are not equal");
    }

}