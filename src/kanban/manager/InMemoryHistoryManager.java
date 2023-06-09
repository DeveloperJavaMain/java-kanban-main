package kanban.manager;

import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// ������ ������� ���������� ����� � ������
public class InMemoryHistoryManager implements HistoryManager {
    // ������ �� ������ ������� ������
    private Node head;
    private Node tail;
    // ������ �� �������� ������� �� id
    private final HashMap<Long, Node> index = new HashMap<>();

    @Override
    // �������� ������ � �������
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(index.remove(task.getId()));
        linkLast(task);
        index.put(task.getId(), tail);
    }

    // ������� ������ �� ������� �� id
    @Override
    public void remove(long id) {
        if (index.containsKey(id)) {
            Node node = index.get(id);
            removeNode(node);
            index.remove(id);
        }
    }

    @Override
    // ������� ��������� �����
    public List<Task> getHistory() {
        return getTasks();
    }

    // CustomLinkedList methods

    // �������� ������ � ����� ������
    private void linkLast(Task task) {
        Node node = new Node(task, tail, null);
        if(head == null){
            head = node;
            tail = head;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    // �������� ������� � ���� ������
    private List<Task> getTasks() {
        ArrayList<Task> res = new ArrayList<>();
        if (head != null) {
            Node node = head;
            while(node!=null){
                res.add(node.value);
                node = node.next;
            }
        }
        return res;
    }

    // ������� ���� �� ������
    private void removeNode(Node node) {
        if (node == null) return;
        if (node == head) {
            // �������� ������ �� ������ ������� ������
            head = head.next;
            if(head==null) {
                tail = null;
            } else {
                head.prev = null;
            }
        }
        if(node.prev!=null) {
            node.prev.next = node.next;
            if(node==tail){
                tail = node.prev;
            }
        }
        if(node.next != null) {
            node.next.prev = node.prev;
        }


    }
}
