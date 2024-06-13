package service;

import model.Task;

public class Node {
    Node next;
    Node prev;
    Task item;

    public Node(Node next, Node prev, Task item) {
        this.next = next;
        this.prev = prev;
        this.item = item;
    }
}
