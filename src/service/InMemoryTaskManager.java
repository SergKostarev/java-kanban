package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Integer idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    @Override
    public List<Task> getAllTasks() {
        for (Task task: tasks.values()) {
            history.add(task);
        }
        return new ArrayList<>(tasks.values());
    }
    @Override
    public List<Subtask> getAllSubtasks() {
        for (Subtask subtask: subtasks.values()) {
            history.add(subtask);
        }
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public List<Epic> getAllEpics() {
        for (Epic epic: epics.values()) {
            history.add(epic);
        }
        return new ArrayList<>(epics.values());
    }
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }
    @Override
    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.setTaskStatus(TaskStatus.NEW);
        }
        subtasks.clear();
    }
    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            history.add(task);
        } else {
            System.out.println("Не найдена задача с указанным идентификатором");
        }
        return task;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            history.add(subtask);
        } else {
            System.out.println("Не найдена подзадача с указанным идентификатором");
        }
        return subtask;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            history.add(epic);
        } else {
            System.out.println("Не найден эпик с указанным идентификатором");
        }
        return epic;
    }

    private void setId(Task task) {
        task.setId(idCounter);
        idCounter++;
    }

    @Override
    public Task addTask(Task task) {
        setId(task);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Не найден эпик с указанным идентификатором, добавление подзадачи невозможно.");
            return null;
        }
        setId(subtask);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic.getSubtasksId().contains(epic.getId())) {
            System.out.println("Список подзадач эпика содержит идентификатор эпика, добавление невозможно.");
            return null;
        }
        for (Integer subtaskId: epic.getSubtasksId()) {
            if (subtasks.get(subtaskId) == null) {
                System.out.println("Не найдена подзадача с идентификатором " + subtaskId + ", добавление невозможно.");
                return null;
            }
        }
        setId(epic);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        if (task.getId() == null) {
            System.out.println("Не задан идентификатор задачи.");
            return null;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Не найдена задача с указанным идентификатором, обновление невозможно.");
            return null;
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask.getId() == null) {
            System.out.println("Не задан идентификатор подзадачи.");
            return null;
        }
        if (subtask.getId().equals(subtask.getEpicId())){
            System.out.println("Идентификаторы эпика и подзадачи совпадают, добавление подзадачи невозможно.");
            return null;
        }
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Не найдена подзадча с указанным идентификатором, обновление невозможно.");
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Не найден эпик с указанным идентификатором, обновление подзадачи невозможно.");
            return null;
        }
        removeSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        if (newEpic.getSubtasksId().contains(newEpic.getId())) {
            System.out.println("Список подзадач эпика содержит идентификатор эпика, добавление невозможно.");
            return null;
        }
        if (newEpic.getId() == null) {
            System.out.println("Не задан идентификатор эпика.");
            return null;
        }
        if (!epics.containsKey(newEpic.getId())) {
            System.out.println("Не найден эпик с указанным идентификатором, обновление невозможно.");
            return null;
        }
        for (Integer subtaskId: newEpic.getSubtasksId()) {
            if (subtasks.get(subtaskId) == null) {
                System.out.println("Не найдена подзадача с идентификатором " + subtaskId + ", добавление невозможно.");
                return null;
            }
        }
        epics.put(newEpic.getId(), newEpic);
        updateEpicStatus(newEpic.getId());
        return newEpic;
    }

    @Override
    public void removeTask(Integer id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задача с указанным идентификатором не найдена, удаление невозможно.");
            return;
        }
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(Integer id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадача с указанным идентификатором не найдена, удаление невозможно.");
            return;
        }
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubTask(subtask.getId());
        subtasks.remove(id);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void removeEpic(Integer id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с указанным идентификатором не найден, удаление невозможно.");
            return;
        }
        for (Integer subtaskId: epics.get(id).getSubtasksId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с указанным идентификатором не найден.");
            return null;
        }
        List<Subtask> subtasksList = new ArrayList<>();
        for (Integer subtaskId: epics.get(id).getSubtasksId()) {
            if (subtasks.containsKey(subtaskId)) {
                Subtask subtask = subtasks.get(subtaskId);
                subtasksList.add(subtask);
                history.add(subtask);
            }
        }
        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
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
