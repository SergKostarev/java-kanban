import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Тест");
        TaskManager tm = new TaskManager();
        tm.addTask(new Task("Сходить в магазин", "Нет описания", TaskStatus.NEW));
        tm.addTask(new Task("Прочитать книгу", "Нет описания", TaskStatus.IN_PROGRESS));
        tm.addEpic(new Epic("Провести уборку", "До 18 мая"));
        tm.addSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.NEW, 3));
        tm.addSubtask(new Subtask("Убрать пыль", "Нет описания", TaskStatus.NEW, 3));
        tm.addEpic(new Epic("Изучить Java", "Нет описания"));
        tm.addSubtask(new Subtask("Сдать задание", "4-ый спринт", TaskStatus.NEW, 6));
        tm.updateSubtask(new Subtask(4, "Вымыть пол", "Нет описания", TaskStatus.DONE, 3));
        tm.updateSubtask(new Subtask(5,"Убрать пыль", "Нет описания", TaskStatus.IN_PROGRESS, 3));
        tm.removeSubtask(5);
        tm.removeEpic(6);
        for (Epic epic: tm.getAllEpics()){
            System.out.println(epic);
        }
        for (Subtask subtask: tm.getAllSubtasks()){
            System.out.println(subtask);
        }
    }
}
