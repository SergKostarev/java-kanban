package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public File getFile() {
        return file;
    }

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private enum TaskType {
        TASK,
        SUBTASK,
        EPIC
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8))) {
            String header = "id,type,name,status,description,epic";
            bw.write(header);
            for (Task task: super.tasks.values()) {
                bw.newLine();
                bw.write(toString(task));
            }
            for (Subtask subtask: super.subtasks.values()) {
                bw.newLine();
                bw.write(toString(subtask));
            }
            for (Epic epic: super.epics.values()) {
                bw.newLine();
                bw.write(toString(epic));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при записи данных в файл");
        }
    }

    private String toString(Task task) {
        String taskType;
        if (task.getClass() == Task.class) {
            taskType = TaskType.TASK.name();
        } else if (task.getClass() == Subtask.class) {
            taskType = TaskType.SUBTASK.name();
        } else if (task.getClass() == Epic.class) {
            taskType = TaskType.EPIC.name();
        } else {
            throw new IllegalArgumentException("Неизвестный тип задачи");
        }
        if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            return String.join(",", subtask.getId().toString(),
                    taskType, subtask.getName(),
                    subtask.getStatus().toString(), subtask.getDescription(),
                    subtask.getEpicId().toString());
        } else {
            return String.join(",", task.getId().toString(),
                    taskType, task.getName(),
                    task.getStatus().toString(), task.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] items = value.split(",");
        String id = items[0];
        String type = items[1];
        String name = items[2];
        String status = items[3];
        String description = items[4];
        switch (type) {
            case "TASK" -> {
                return new Task(Integer.parseInt(id), name,
                        description, TaskStatus.valueOf(status));
            }
            case "SUBTASK" -> {
                String epicId = items[5];
                return new Subtask(Integer.parseInt(id), name,
                        description, TaskStatus.valueOf(status),
                        Integer.parseInt(epicId));
            }
            case "EPIC" -> {
                return new Epic(Integer.parseInt(id), name,
                        description, new ArrayList<>());
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fm = new FileBackedTaskManager(file);
        String content = Files.readString(file.toPath());
        String[] lines = content.split(System.lineSeparator());
        for (int i = 0; i < lines.length; i++) {
            if (i != 0) {
                Task taskFromFile = fromString(lines[i]);
                if (taskFromFile.getClass() == Task.class) {
                    fm.tasks.put(taskFromFile.getId(), taskFromFile);
                } else if (taskFromFile.getClass() == Subtask.class) {
                    Subtask subtask = (Subtask) taskFromFile;
                    fm.subtasks.put(taskFromFile.getId(), subtask);
                } else if (taskFromFile.getClass() == Epic.class) {
                    Epic epic = (Epic) taskFromFile;
                    fm.epics.put(taskFromFile.getId(), epic);
                }
            }
        }
        return fm;
    }

    @Override
    public Task addTask(Task taskInput) {
        Task task = super.addTask(taskInput);
        save();
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtaskInput) {
        Subtask subtask = super.addSubtask(subtaskInput);
        save();
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epicInput) {
        Epic epic = super.addEpic(epicInput);
        save();
        return epic;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(Integer id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public Task updateTask(Task taskInput) {
        Task task = super.updateTask(taskInput);
        save();
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtaskInput) {
        Subtask subtask = super.updateSubtask(subtaskInput);
        save();
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epicInput) {
        Epic epic = super.updateEpic(epicInput);
        save();
        return epic;
    }
}
