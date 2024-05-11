package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId;
    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksId = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubTask(Integer id) {
        if (id != null) {
            subtasksId.add(id);
        }
    }

    public void deleteSubTask(Integer id) {
        if (id != null) {
            subtasksId.remove(id);
        }
    }

    public void clearSubTasks() {
        subtasksId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksId=" + subtasksId +
                "} " + super.toString();
    }
}
