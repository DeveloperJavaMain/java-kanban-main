package kanban.manager;

import kanban.model.Task;

// ���� ����������� ������ ��� �������� �����
public class Node {
    Task value;
    Node prev;
    Node next;

    // constructors

    public Node(Task task, Node prev, Node next) {
        value = task;
        this.prev = prev;
        this.next = next;
    }

}
