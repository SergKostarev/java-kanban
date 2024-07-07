package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.Arrays;

public class TaskConverter {

    public static String toString(Task task) {
        String taskType;
        if (task.getClass() == Task.class) {
            taskType = TaskType.TASK.name();
            return String.join(",", task.getId().toString(),
                    taskType, task.getName(),
                    task.getStatus().toString(), task.getDescription());
        } else if (task.getClass() == Subtask.class) {
            taskType = TaskType.SUBTASK.name();
            Subtask subtask = (Subtask) task;
            return String.join(",", subtask.getId().toString(),
                    taskType, subtask.getName(),
                    subtask.getStatus().toString(), subtask.getDescription(),
                    subtask.getEpicId().toString());
        } else if (task.getClass() == Epic.class) {
            taskType = TaskType.EPIC.name();
            Epic epic = (Epic) task;
            String subtasksId = epic.getSubtasksId().toString();
            subtasksId = subtasksId.substring(1, subtasksId.length() - 1)
                    .replace(",", ";");
            return String.join(",", epic.getId().toString(),
                    taskType, epic.getName(), epic.getStatus().toString(),
                    epic.getDescription(), subtasksId);
        } else {
            throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    public static Task fromString(String value) {
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
                String subtasksId = items[5];
                String[] subtasksIdSplit = subtasksId.split(";");
                Integer[] numbers = new Integer[subtasksIdSplit.length];
                for (int i = 0; i < subtasksIdSplit.length; i++) {
                    numbers[i] = Integer.parseInt(subtasksIdSplit[i]);
                }
                return new Epic(Integer.parseInt(id), name,
                        description, TaskStatus.valueOf(status), Arrays.asList(numbers));
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

}
