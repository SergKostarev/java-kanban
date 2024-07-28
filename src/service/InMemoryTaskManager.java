package service;

import exception.IdentifierException;
import exception.IntersectionException;
import exception.NotFoundException;
import exception.UpdateException;
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
    public Task getTask(Integer id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task != null) {
            history.add(task);
            return new Task(task);
        }
        throw new NotFoundException("Не найдена задача с указанным идентификатором", id);
    }

    @Override
    public Subtask getSubtask(Integer id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            history.add(subtask);
            return new Subtask(subtask);
        }
        throw new NotFoundException("Не найдена подзадача с указанным идентификатором", id);
    }

    @Override
    public Epic getEpic(Integer id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic != null) {
            history.add(epic);
            return new Epic(epic);
        }
        throw new NotFoundException("Не найден эпик с указанным идентификатором", id);
    }

    private void setId(Task task) {
        task.setId(idCounter);
        idCounter++;
    }

    @Override
    public Task addTask(Task taskInput) throws IntersectionException {
        if (timeIntersectionCheck(taskInput)) {
            throw new IntersectionException("Временные интервалы задач пересекаются, " +
                                "добавлние невозможно", taskInput.getId());
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
    public Subtask addSubtask(Subtask subtaskInput) throws IntersectionException, NotFoundException {
        if (timeIntersectionCheck(subtaskInput)) {
            throw new IntersectionException("Временные интервалы задач пересекаются, " +
                    "добавлние невозможно", subtaskInput.getId());
        }
        Subtask subtask = new Subtask(subtaskInput);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Не найдена подзадача с указанным идентификатором", subtaskInput.getId());
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
    public Epic addEpic(Epic epicInput) throws NotFoundException, IdentifierException {
        Epic epic = new Epic(epicInput);
        if (epic.getSubtasksId().contains(epic.getId())) {
            throw new IdentifierException("Список подзадач эпика содержит идентификатор эпика, добавление невозможно.", epicInput.getId());
        }
        for (Integer subtaskId: epic.getSubtasksId()) {
            if (subtasks.get(subtaskId) == null) {
                throw new NotFoundException("Не найден эпик с указанным идентификатором", epicInput.getId());
            }
        }
        setId(epic);
        epics.put(epic.getId(), epic);
        updateEpicTime(epic.getId());
        return new Epic(epic);
    }

    @Override
    public Task updateTask(Task taskInput) throws IntersectionException, NotFoundException, IdentifierException {
        if (timeIntersectionCheck(taskInput)) {
            throw new IntersectionException("Временные интервалы задач пересекаются, " +
                    "добавлние невозможно", taskInput.getId());
        }
        if (taskInput.getId() == null) {
            throw new IdentifierException("Не задан идентификатор задачи.", taskInput.getId());
        }
        if (!tasks.containsKey(taskInput.getId())) {
            throw new NotFoundException("Не найдена задача с указанным идентификатором, обновление невозможно.", taskInput.getId());
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
    public Subtask updateSubtask(Subtask subtaskInput) throws IntersectionException, NotFoundException, UpdateException, IdentifierException {
        if (timeIntersectionCheck(subtaskInput)) {
            throw new IntersectionException("Временные интервалы задач пересекаются, " +
                    "добавлние невозможно", subtaskInput.getId());
        }
        if (subtaskInput.getId() == null) {
            throw new IdentifierException("Не задан идентификатор подзадачи.", subtaskInput.getId());
        }
        if (subtaskInput.getId().equals(subtaskInput.getEpicId())) {
            throw new UpdateException("Идентификаторы эпика и подзадачи совпадают, добавление подзадачи невозможно.", subtaskInput.getId());
        }
        if (!subtasks.containsKey(subtaskInput.getId())) {
            throw new NotFoundException("Не найдена подзадча с указанным идентификатором, обновление невозможно.", subtaskInput.getId());
        }
        Epic epic = epics.get(subtaskInput.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Не найден эпик с указанным идентификатором, обновление подзадачи невозможно.", subtaskInput.getId());
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
    public Epic updateEpic(Epic epicInput) throws NotFoundException, IdentifierException {
        Integer epicId = epicInput.getId();
        if (epicId == null) {
            throw new IdentifierException("Не задан идентификатор эпика.", epicInput.getId());
        }
        if (!epics.containsKey(epicId)) {
            throw new NotFoundException("Не найден эпик с указанным идентификатором, обновление невозможно.", epicId);
        }
        Epic oldEpic = epics.get(epicId);
        Epic newEpic = new Epic(oldEpic.getId(), epicInput.getName(), epicInput.getDescription(),
                oldEpic.getStatus(), oldEpic.getSubtasksId(),
                oldEpic.getDuration(), oldEpic.getStartTime(), oldEpic.getEndTime());
        epics.put(newEpic.getId(), newEpic);
        return new Epic(newEpic);
    }

    @Override
    public Task removeTask(Integer id) throws NotFoundException {
        if (!tasks.containsKey(id)) {
            throw new NotFoundException("Задача с указанным идентификатором не найдена, удаление невозможно.", id);
        }
        Task task = tasks.get(id);
        tasks.remove(id);
        if (task.getStartTime() != null) {
            sortedTasks.remove(task);
        }
        return task;
    }

    @Override
    public Subtask removeSubtask(Integer id) throws NotFoundException {
        if (!subtasks.containsKey(id)) {
            throw new NotFoundException("Подзадача с указанным идентификатором не найдена, удаление невозможно.", id);
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
        return subtask;
    }

    @Override
    public Epic removeEpic(Integer id) throws NotFoundException {
        if (!epics.containsKey(id)) {
            throw new NotFoundException("Эпик с указанным идентификатором не найден, удаление невозможно.", id);
        }
        epics.get(id).getSubtasksId()
                .stream()
                .forEach(subtasks::remove);
        Epic epic = epics.get(id);
        epics.remove(id);
        if (epic.getStartTime() != null) {
            sortedTasks.remove(epic);
        }
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer epicId) throws NotFoundException {
        if (!epics.containsKey(epicId)) {
            throw new NotFoundException("Эпик с указанным идентификатором не найден.", epicId);
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
