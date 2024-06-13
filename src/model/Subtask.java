package model;

public class Subtask extends Task {
    private final Integer epicId;
    
    public Subtask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer epicId) {
        super(id, name, description, status);
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
