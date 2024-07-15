package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected Integer idCounter = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    private final TaskStartTimeComparator taskStartTimeComparator = new TaskStartTimeComparator();

    protected final TreeSet<Task> sortedTasks = new TreeSet<>(taskStartTimeComparator);

    @Override
    public List<Task> getAllTasks() {
        return tasks.values()
                .stream()
                .map(Task::new)
                .toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtasks.values()
                .stream()
                .map(Subtask::new)
                .toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        return epics.values()
                .stream()
                .map(Epic::new)
                .toList();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        sortedTasks.removeAll(tasks.values());
    }

    @Override
    public void removeAllSubtasks() {
        epics.values()
                .stream()
                .forEach(epic -> {
                    epic.clearSubTasks();
                    epic.setTaskStatus(TaskStatus.NEW);
                });
        subtasks.clear();
        sortedTasks.removeAll(subtasks.values());
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        sortedTasks.removeAll(epics.values());
        sortedTasks.removeAll(subtasks.values());
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
        if (timeIntersectionCheck(taskInput)) {
            System.out.println("Временные интервалы задач пересекаются, " +
                    "добавлние невозможно");
            return null;
        }
        Task task = new Task(taskInput);
        setId(task);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
        return new Task(task);
    }

    @Override
    public Subtask addSubtask(Subtask subtaskInput) {
        if (timeIntersectionCheck(subtaskInput)) {
            System.out.println("Временные интервалы задач пересекаются," +
                    "добавлние невозможно");
            return null;
        }
        Subtask subtask = new Subtask(subtaskInput);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Не найден эпик с указанным идентификатором, добавление подзадачи невозможно.");
            return null;
        }
        setId(subtask);
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            sortedTasks.add(subtask);
        }
        epic.addSubTask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(subtask.getEpicId());
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
        updateEpicTime(epic.getId());
        return new Epic(epic);
    }

    @Override
    public Task updateTask(Task taskInput) {
        if (timeIntersectionCheck(taskInput)) {
            System.out.println("Временные интервалы задач пересекаются," +
                    "добавлние невозможно");
            return null;
        }
        if (taskInput.getId() == null) {
            System.out.println("Не задан идентификатор задачи.");
            return null;
        }
        if (!tasks.containsKey(taskInput.getId())) {
            System.out.println("Не найдена задача с указанным идентификатором, обновление невозможно.");
            return null;
        }
        if (tasks.get(taskInput.getId()).getStartTime() != null) {
            sortedTasks.remove(tasks.get(taskInput.getId()));
        }
        Task task = new Task(taskInput);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
        return new Task(task);
    }

    @Override
    public Subtask updateSubtask(Subtask subtaskInput) {
        if (timeIntersectionCheck(subtaskInput)) {
            System.out.println("Временные интервалы задач пересекаются," +
                    "добавлние невозможно");
            return null;
        }
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
        if (subtasks.get(subtaskInput.getId()).getStartTime() != null) {
            sortedTasks.remove(subtasks.get(subtaskInput.getId()));
        }
        Subtask subtask = new Subtask(subtaskInput);
        removeSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTask(subtask.getId());
        if (subtask.getStartTime() != null) {
            sortedTasks.add(subtask);
        }
        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(subtask.getEpicId());
        return new Subtask(subtask);
    }

    @Override
    public Epic updateEpic(Integer epicId, String name, String description) {
        if (epicId == null) {
            System.out.println("Не задан идентификатор эпика.");
            return null;
        }
        if (!epics.containsKey(epicId)) {
            System.out.println("Не найден эпик с указанным идентификатором, обновление невозможно.");
            return null;
        }
        Epic oldEpic = epics.get(epicId);
        Epic newEpic = new Epic(oldEpic.getId(), name, description,
                oldEpic.getStatus(), oldEpic.getSubtasksId(),
                oldEpic.getDuration(), oldEpic.getStartTime(), oldEpic.getEndTime());
        epics.put(newEpic.getId(), newEpic);
        return new Epic(newEpic);
    }

    @Override
    public void removeTask(Integer id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задача с указанным идентификатором не найдена, удаление невозможно.");
            return;
        }
        Task task = tasks.get(id);
        tasks.remove(id);
        if (task.getStartTime() != null) {
            sortedTasks.remove(task);
        }
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
        if (subtask.getStartTime() != null) {
            sortedTasks.remove(subtask);
        }
        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(subtask.getEpicId());
    }

    @Override
    public void removeEpic(Integer id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с указанным идентификатором не найден, удаление невозможно.");
            return;
        }
        epics.get(id).getSubtasksId()
                .stream()
                .forEach(subtasks::remove);
        Epic epic = epics.get(id);
        epics.remove(id);
        if (epic.getStartTime() != null) {
            sortedTasks.remove(epic);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с указанным идентификатором не найден.");
            return null;
        }
        List<Subtask> subtasksList = new ArrayList<>();
        epics.get(epicId).getSubtasksId()
                .stream()
                .forEach(subtaskId -> {
                    if (subtasks.containsKey(subtaskId)) {
                        Subtask subtask = subtasks.get(subtaskId);
                        subtasksList.add(new Subtask(subtask));
                        history.add(subtask);
                    }
                });
        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        history.getHistory()
                .stream()
                .forEach(task -> {
                    if (task.getClass() == Task.class) {
                        historyList.add(new Task(task));
                    } else if (task.getClass() == Subtask.class) {
                        historyList.add(new Subtask((Subtask) task));
                    } else if (task.getClass() == Epic.class) {
                        historyList.add(new Epic((Epic) task));
                    }
                });
        return historyList;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    private boolean intersects(LocalDateTime start1, LocalDateTime end1,
                               LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private boolean timeIntersectionCheck(Task task) {
        if (task.getStartTime() != null) {
            return sortedTasks
                    .stream()
                    .filter(t -> !t.equals(task))
                    .anyMatch(other -> intersects(task.getStartTime(),
                            task.getEndTime(),
                            other.getStartTime(),
                            other.getEndTime()));
        }
        return false;
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

    private void updateEpicTime(Integer id) {
        Epic epic = epics.get(id);
        LocalDateTime epicStartTaskTime = null;
        LocalDateTime epicEndTaskTime = null;
        Duration epicDuration = Duration.ZERO;
        for (Integer subtaskId: epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStartTime() != null
                    && (epicStartTaskTime == null || subtask.getStartTime().isBefore(epicStartTaskTime))) {
                epicStartTaskTime = subtask.getStartTime();
            }
            if (subtask.getEndTime() != null
                    && (epicEndTaskTime == null || subtask.getEndTime().isAfter(epicEndTaskTime))) {
                epicEndTaskTime = subtask.getEndTime();
            }
            epicDuration = epicDuration.plus(subtask.getDuration());
        }
        epic.setStartTime(epicStartTaskTime);
        epic.setEndTime(epicEndTaskTime);
        epic.setDuration(epicDuration);
    }

}
