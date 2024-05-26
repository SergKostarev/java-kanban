import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Тест");
        TaskManager tm = Managers.getDefault();
        tm.addTask(new Task("Сходить в магазин", "Нет описания", TaskStatus.NEW));
        tm.addTask(new Task("Прочитать книгу", "Нет описания", TaskStatus.IN_PROGRESS));
        tm.addEpic(new Epic("Провести уборку", "До 18 мая", new ArrayList<>()));
        tm.addSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.NEW, 3));
        tm.addSubtask(new Subtask("Убрать пыль", "Нет описания", TaskStatus.NEW, 3));
        tm.addEpic(new Epic("Изучить Java", "Нет описания", new ArrayList<>()));
        tm.addSubtask(new Subtask("Сдать задание", "4-ый спринт", TaskStatus.NEW, 6));
        tm.updateSubtask(new Subtask(4, "Вымыть пол", "Нет описания", TaskStatus.DONE, 3));
        tm.updateSubtask(new Subtask(5,"Убрать пыль", "Нет описания", TaskStatus.IN_PROGRESS, 3));
        tm.removeSubtask(5);
        tm.removeEpic(6);
        tm.getSubtask(4);
        tm.getSubtask(5);
        tm.getEpic(3);
        tm.getEpic(3);
        tm.getEpic(3);
        tm.getEpic(3);
        tm.getEpic(3);
        tm.getEpic(3);
        tm.getSubtask(4);
        tm.getSubtask(5);
        tm.getSubtask(4);
        tm.getSubtask(5);
        System.out.println("HISTORY");
        for (Task task : tm.getHistory()) {
            System.out.println(task);
        }
    }
}
