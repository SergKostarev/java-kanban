package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Integer> subtasksId;
    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksId = new ArrayList<>();
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksId=" + subtasksId +
                "} " + super.toString();
    }
}
