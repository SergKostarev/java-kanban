package service;

import model.Task;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return o1.getStartTime().isEqual(o2.getStartTime()) ? 0 :
                o1.getStartTime().isAfter(o2.getStartTime()) ? 1 : -1;
    }
}
