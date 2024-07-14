package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final Integer epicId;

    public Subtask(String name, String description, TaskStatus status, Integer epicId,
                   Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer epicId,
                   Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicId = subtask.epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }
}
