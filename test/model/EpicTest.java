package model;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    public void shouldReturnEqualEpicsIfIdIsEqual() {
        Epic epic1 = new Epic(1, "Some task", "Some description", new ArrayList<>());
        Epic epic2 = new Epic(1, "Another task", "Another description", new ArrayList<>());
        Assertions.assertEquals(epic1, epic2, "Epics with equal identifier are not equal");
    }

}