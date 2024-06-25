package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node first;

    private Node last;

    private final HashMap<Integer, Node> taskHistory = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node exLast = last;
        Node newNode = new Node(null, exLast, task);
        last = newNode;
        if (exLast == null) {
            first = newNode;
        } else {
            exLast.next = newNode;
        }
        taskHistory.put(task.getId(), newNode);
    }

    private ArrayList<Task> getTasks() {
        Node currNode = first;
        ArrayList<Task> taskList = new ArrayList<>();
        while (currNode != null) {
            taskList.add(currNode.item);
            currNode = currNode.next;
        }
        return taskList;
    }

    private void removeNode(Node node) {
        Node currNext = node.next;
        Node currPrev = node.prev;
        if (currNext != null) {
            currNext.prev = currPrev;
        } else {
            last = currPrev;
        }
        if (currPrev != null) {
            currPrev.next = currNext;
        } else {
            first = currNext;
        }
        node.next = null;
        node.prev = null;
    }

    @Override
    public void add(Task task) {
        Node node = taskHistory.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = taskHistory.get(id);
        if (node != null) {
            removeNode(node);
            taskHistory.remove(id);
        }
    }
}
