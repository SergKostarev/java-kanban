package service;

import exception.IdentifierException;
import exception.IntersectionException;
import exception.NotFoundException;
import exception.UpdateException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<model.Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTask(Integer id) throws NotFoundException;

    Subtask getSubtask(Integer id) throws NotFoundException;

    Epic getEpic(Integer id) throws NotFoundException;

    Task addTask(Task task) throws IntersectionException;

    Subtask addSubtask(Subtask subtask) throws IntersectionException, NotFoundException;

    Epic addEpic(Epic epic) throws NotFoundException, IdentifierException;

    Task updateTask(Task task) throws IntersectionException, NotFoundException, IdentifierException;

    Subtask updateSubtask(Subtask subtask) throws IntersectionException, NotFoundException, UpdateException, IdentifierException;

    Epic updateEpic(Epic epicInput) throws NotFoundException, IdentifierException;

    Task removeTask(Integer id) throws NotFoundException;

    Subtask removeSubtask(Integer id) throws NotFoundException;

    Epic removeEpic(Integer id) throws NotFoundException;

    List<Subtask> getEpicSubtasks(Integer id) throws NotFoundException;

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
