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
        tm.addSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.NEW, 2));
        tm.addSubtask(new Subtask("Убрать пыль", "Нет описания", TaskStatus.NEW, 2));
        tm.addEpic(new Epic("Изучить Java", "Нет описания"));
        tm.addSubtask(new Subtask("Сдать задание", "4-ый спринт", TaskStatus.NEW, 5));
        tm.updateSubtask(new Subtask("Вымыть пол", "Нет описания", TaskStatus.DONE, 2), 3);
        tm.updateSubtask(new Subtask("Убрать пыль", "Нет описания", TaskStatus.IN_PROGRESS, 2), 4);
        tm.removeSubtask(4);
        tm.removeEpic(5);
        for (Epic epic: tm.getAllEpics().values()){
            System.out.println(epic);
        }
        for (Subtask subtask: tm.getAllSubtasks().values()){
            System.out.println(subtask);
        }
    }
}
