package service;

import exception.*;
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
    public Task addTask(Task taskInput) throws IntersectionException {
        Task task = super.addTask(taskInput);
        save();
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtaskInput) throws IntersectionException, NotFoundException {
        Subtask subtask = super.addSubtask(subtaskInput);
        save();
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epicInput) throws NotFoundException, IdentifierException {
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
    public Task removeTask(Integer id) throws NotFoundException {
        super.removeTask(id);
        save();
        return null;
    }

    @Override
    public Subtask removeSubtask(Integer id) throws NotFoundException {
        super.removeSubtask(id);
        save();
        return null;
    }

    @Override
    public Epic removeEpic(Integer id) throws NotFoundException {
        super.removeEpic(id);
        save();
        return null;
    }

    @Override
    public Task updateTask(Task taskInput) throws IntersectionException, NotFoundException, IdentifierException {
        Task task = super.updateTask(taskInput);
        save();
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtaskInput) throws IntersectionException, NotFoundException, UpdateException, IdentifierException {
        Subtask subtask = super.updateSubtask(subtaskInput);
        save();
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epicInput) throws NotFoundException, IdentifierException {
        Epic epic = super.updateEpic(epicInput);
        save();
        return epic;
    }
}
