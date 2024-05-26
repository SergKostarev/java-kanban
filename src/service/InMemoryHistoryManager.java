package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        taskHistory.add(task);
        if (taskHistory.size() > 10) {
            taskHistory.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
