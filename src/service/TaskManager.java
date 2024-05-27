package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<model.Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubtask();

    void removeAllEpics();

    Task getTask(Integer id);

    Subtask getSubtask(Integer id);

    Epic getEpic(Integer id);

    Task addTask(Task task);

    Subtask addSubtask(Subtask subtask);

    Epic addEpic(Epic epic);

    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic newEpic);

    void removeTask(Integer id);

    void removeSubtask(Integer id);

    void removeEpic(Integer id);

    List<Subtask> getEpicSubtasks(Integer id);

    List <Task> getHistory();
}
