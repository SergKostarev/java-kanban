package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class TaskConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String toString(Task task) {
        String taskType;
        String duration = Long.toString(task.getDuration().toMinutes());
        String startTime = task.getStartTime() == null ? "0" : task.getStartTime().format(formatter);
        if (task.getClass() == Task.class) {
            taskType = TaskType.TASK.name();
            return String.join(",", task.getId().toString(),
                    taskType, task.getName(),
                    task.getStatus().toString(), task.getDescription(),
                    duration, startTime);
        } else if (task.getClass() == Subtask.class) {
            taskType = TaskType.SUBTASK.name();
            Subtask subtask = (Subtask) task;
            return String.join(",", subtask.getId().toString(),
                    taskType, subtask.getName(),
                    subtask.getStatus().toString(), subtask.getDescription(),
                    duration, startTime,
                    subtask.getEpicId().toString());
        } else if (task.getClass() == Epic.class) {
            taskType = TaskType.EPIC.name();
            Epic epic = (Epic) task;
            String subtasksId = epic.getSubtasksId().toString();
            String endTime = task.getEndTime() == null ? "0" : task.getEndTime().format(formatter);
            subtasksId = subtasksId.substring(1, subtasksId.length() - 1)
                    .replace(",", ";");
            return String.join(",", epic.getId().toString(),
                    taskType, epic.getName(), epic.getStatus().toString(),
                    epic.getDescription(),
                    duration, startTime,
                    subtasksId, endTime);
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
        Duration duration = Duration.ofMinutes(Long.parseLong(items[5]));
        LocalDateTime startTime = items[6].equals("0") ? null : LocalDateTime.parse(items[6], formatter);
        switch (type) {
            case "TASK" -> {
                return new Task(Integer.parseInt(id), name,
                        description, TaskStatus.valueOf(status),
                        duration, startTime);
            }
            case "SUBTASK" -> {
                String epicId = items[7];
                return new Subtask(Integer.parseInt(id), name,
                        description, TaskStatus.valueOf(status),
                        Integer.parseInt(epicId), duration, startTime);
            }
            case "EPIC" -> {
                String subtasksId = items[7];
                String[] subtasksIdSplit = subtasksId.split(";");
                Integer[] numbers = new Integer[subtasksIdSplit.length];
                for (int i = 0; i < subtasksIdSplit.length; i++) {
                    numbers[i] = Integer.parseInt(subtasksIdSplit[i]);
                }
                LocalDateTime endTime = items[8].equals("0") ? null : LocalDateTime.parse(items[8], formatter);
                return new Epic(Integer.parseInt(id), name,
                        description, TaskStatus.valueOf(status), Arrays.asList(numbers),
                        duration, startTime, endTime);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

}
