package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Integer idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    public void removeAllTasks() {
        tasks.clear();
    }
    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.setTaskStatus(TaskStatus.NEW);
        }
        subtasks.clear();
    }
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

    public Epic getEpic(Integer id) {
        return epics.get(id);
    }

    private void setId(Task task) {
        task.setId(idCounter);
        idCounter++;
    }

    public void addTask(Task task) {
        setId(task);
        tasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Не найден эпик с указанным идентификатором, добавление подзадачи невозможно.");
            return;
        }
        setId(subtask);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
    }

    public void addEpic(Epic epic) {
        setId(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        if (task.getId() == null) {
            System.out.println("Не задан идентификатор задачи.");
            return;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Не найдена задача с указанным идентификатором, обновление невозможно.");
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask.getId() == null) {
            System.out.println("Не задан идентификатор подзадачи.");
            return;
        }
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Не найдена подзадча с указанным идентификатором, обновление невозможно.");
            return;
        }
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Не найден эпик с указанным идентификатором, обновление подзадачи невозможно.");
            return;
        }
        removeSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
    }

    public void updateEpic(Epic newEpic) {
        if (newEpic.getId() == null) {
            System.out.println("Не задан идентификатор подзадачи.");
            return;
        }
        if (!epics.containsKey(newEpic.getId())) {
            System.out.println("Не найден эпик с указанным идентификатором, обновление невозможно.");
            return;
        }
        Epic oldEpic = epics.get(newEpic.getId());
        for (Integer subtaskId: oldEpic.getSubtasksId()) {
            newEpic.addSubTask(subtaskId);
        }
        epics.put(newEpic.getId(), newEpic);
        updateEpicStatus(newEpic.getId());
    }

    public void removeTask(Integer id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задача с указанным идентификатором не найдена, удаление невозможно.");
            return;
        }
        tasks.remove(id);
    }

    public void removeSubtask(Integer id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадача с указанным идентификатором не найдена, удаление невозможно.");
            return;
        }
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpic(subtask.getEpicId());
        epic.deleteSubTask(subtask.getId());
        subtasks.remove(id);
        updateEpicStatus(subtask.getEpicId());
    }

    public void removeEpic(Integer id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с указанным идентификатором не найден, удаление невозможно.");
            return;
        }
        for (Integer subtaskId: getEpic(id).getSubtasksId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public List<Subtask> getEpicSubtasks(Integer id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с указанным идентификатором не найден.");
            return null;
        }
        List<Subtask> subtasksList = new ArrayList<>();
        for (Integer subtaskId: epics.get(id).getSubtasksId()) {
            if (subtasks.containsKey(subtaskId)) {
                subtasksList.add(subtasks.get(subtaskId));
            }
        }
        return subtasksList;
    }

    private void updateEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        if (epic.getSubtasksId().isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            boolean isNew = true;
            boolean isDone = true;
            for (Integer subtaskId: epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask.getStatus() != TaskStatus.NEW) {
                    isNew = false;
                }
                if (subtask.getStatus() != TaskStatus.DONE) {
                    isDone = false;
                }
            }
            if (isNew) {
                epic.setTaskStatus(TaskStatus.NEW);
                return;
            }
            if (isDone) {
                epic.setTaskStatus(TaskStatus.DONE);
                return;
            }
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
