package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

public class ManagersTest {
    
    @Test
    public void shouldReturnNonNullTaskManager() {
        TaskManager tm = Managers.getDefault();
        Assertions.assertNotNull(tm, "TaskManager is null");
    }
    @Test
    public void shouldReturnNonNullHistory() {
        HistoryManager history = Managers.getDefaultHistory();
        Assertions.assertNotNull(history, "HistoryManager is null");
    }

}