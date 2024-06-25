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
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task: tasks.values()) {
            tasksList.add(new Task(task));
        }
        return tasksList;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Subtask subtask: subtasks.values()) {
            subtasksList.add(new Subtask(subtask));
        }
        return subtasksList;
    }

    @Override
    public List<Epic> getAllEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Epic epic: epics.values()) {
            epicsList.add(new Epic(epic));
        }
        return epicsList;
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
            return null;
        }
        return new Task(task);
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            history.add(subtask);
        } else {
            System.out.println("Не найдена подзадача с указанным идентификатором");
            return null;
        }
        return new Subtask(subtask);
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            history.add(epic);
        } else {
            System.out.println("Не найден эпик с указанным идентификатором");
            return null;
        }
        return new Epic(epic);
    }

    private void setId(Task task) {
        task.setId(idCounter);
        idCounter++;
    }

    @Override
    public Task addTask(Task taskInput) {
        Task task = new Task(taskInput);
        setId(task);
        tasks.put(task.getId(), task);
        return new Task(task);
    }

    @Override
    public Subtask addSubtask(Subtask subtaskInput) {
        Subtask subtask = new Subtask(subtaskInput);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Не найден эпик с указанным идентификатором, добавление подзадачи невозможно.");
            return null;
        }
        setId(subtask);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        return new Subtask(subtask);
    }

    @Override
    public Epic addEpic(Epic epicInput) {
        Epic epic = new Epic(epicInput);
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
        return new Epic(epic);
    }

    @Override
    public Task updateTask(Task taskInput) {
        if (taskInput.getId() == null) {
            System.out.println("Не задан идентификатор задачи.");
            return null;
        }
        if (!tasks.containsKey(taskInput.getId())) {
            System.out.println("Не найдена задача с указанным идентификатором, обновление невозможно.");
            return null;
        }
        Task task = new Task(taskInput);
        tasks.put(task.getId(), task);
        return new Task(task);
    }

    @Override
    public Subtask updateSubtask(Subtask subtaskInput) {
        if (subtaskInput.getId() == null) {
            System.out.println("Не задан идентификатор подзадачи.");
            return null;
        }
        if (subtaskInput.getId().equals(subtaskInput.getEpicId())) {
            System.out.println("Идентификаторы эпика и подзадачи совпадают, добавление подзадачи невозможно.");
            return null;
        }
        if (!subtasks.containsKey(subtaskInput.getId())) {
            System.out.println("Не найдена подзадча с указанным идентификатором, обновление невозможно.");
            return null;
        }
        Epic epic = epics.get(subtaskInput.getEpicId());
        if (epic == null) {
            System.out.println("Не найден эпик с указанным идентификатором, обновление подзадачи невозможно.");
            return null;
        }
        Subtask subtask = new Subtask(subtaskInput);
        removeSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        return new Subtask(subtask);
    }

    @Override
    public Epic updateEpic(Epic epicInput) {
        if (epicInput.getSubtasksId().contains(epicInput.getId())) {
            System.out.println("Список подзадач эпика содержит идентификатор эпика, добавление невозможно.");
            return null;
        }
        if (epicInput.getId() == null) {
            System.out.println("Не задан идентификатор эпика.");
            return null;
        }
        if (!epics.containsKey(epicInput.getId())) {
            System.out.println("Не найден эпик с указанным идентификатором, обновление невозможно.");
            return null;
        }
        for (Integer subtaskId: epicInput.getSubtasksId()) {
            if (subtasks.get(subtaskId) == null) {
                System.out.println("Не найдена подзадача с идентификатором " + subtaskId + ", добавление невозможно.");
                return null;
            }
        }
        Epic newEpic = new Epic(epicInput);
        epics.put(newEpic.getId(), newEpic);
        updateEpicStatus(newEpic.getId());
        return new Epic(newEpic);
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
                subtasksList.add(new Subtask(subtask));
                history.add(subtask);
            }
        }
        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        for (Task task: history.getHistory()) {
            if (task.getClass() == Task.class) {
                historyList.add(new Task(task));
            } else if (task.getClass() == Subtask.class) {
                historyList.add(new Subtask((Subtask) task));
            } else if (task.getClass() == Epic.class) {
                historyList.add(new Epic((Epic) task));
            }
        }
        return historyList;
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
