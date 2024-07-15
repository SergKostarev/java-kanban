package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId;

    private LocalDateTime endTime;

    public Epic(String name, String description, List<Integer> subtasksId) {
        super(name, description, TaskStatus.NEW, null, null);
        this.subtasksId = subtasksId;
    }

    public Epic(Integer id, String name, String description, List<Integer> subtasksId) {
        super(id, name, description, TaskStatus.NEW, null, null);
        this.subtasksId = subtasksId;
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtasksId = new ArrayList<>(epic.subtasksId);
        this.endTime = epic.getEndTime();
    }

    public Epic(Integer id, String name, String description, TaskStatus status, List<Integer> subtasksId,
                Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, description, status, duration, startTime);
        this.subtasksId = subtasksId;
        this.endTime = endTime;
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
