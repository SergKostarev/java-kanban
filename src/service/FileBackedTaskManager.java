package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public File getFile() {
        return file;
    }

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8))) {
            String header = "id,type,name,status,description,startTime,duration,epic,endTime";
            bw.write(header);
            for (Task task: super.tasks.values()) {
                bw.newLine();
                bw.write(TaskConverter.toString(task));
            }
            for (Subtask subtask: super.subtasks.values()) {
                bw.newLine();
                bw.write(TaskConverter.toString(subtask));
            }
            for (Epic epic: super.epics.values()) {
                bw.newLine();
                bw.write(TaskConverter.toString(epic));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при записи данных в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fm = new FileBackedTaskManager(file);
        String content = Files.readString(file.toPath());
        String[] lines = content.split(System.lineSeparator());
        int maxId = 0;
        for (int i = 0; i < lines.length; i++) {
            if (i != 0) {
                Task taskFromFile = TaskConverter.fromString(lines[i]);
                if (taskFromFile.getId() > maxId) {
                    maxId = taskFromFile.getId();
                }
                if (taskFromFile.getClass() == Task.class) {
                    fm.tasks.put(taskFromFile.getId(), taskFromFile);
                } else if (taskFromFile.getClass() == Subtask.class) {
                    Subtask subtask = (Subtask) taskFromFile;
                    fm.subtasks.put(taskFromFile.getId(), subtask);
                } else if (taskFromFile.getClass() == Epic.class) {
                    Epic epic = (Epic) taskFromFile;
                    fm.epics.put(taskFromFile.getId(), epic);
                }
                if (taskFromFile.getStartTime() != null) {
                    fm.sortedTasks.add(taskFromFile);
                }
            }
        }
        fm.idCounter = maxId + 1;
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
    public Epic updateEpic(Integer epicId, String name, String description) {
        Epic epic = super.updateEpic(epicId, name, description);
        save();
        return epic;
    }
}
