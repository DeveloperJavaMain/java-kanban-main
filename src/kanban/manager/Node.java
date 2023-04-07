package kanban.manager;

import kanban.model.Task;

// узел двусвязного списка для хранения задач
public class Node {
    private Task value;
    private Node prev;
    private Node next;

    // constructors

    public Node() {
    }

    public Node(Task value) {
        this.value = value;
    }

    // get/set
    public Task getValue() {
        return value;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

}
