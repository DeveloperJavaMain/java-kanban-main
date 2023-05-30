package kanban.manager;

import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// хранит историю просмотров задач в памяти
public class InMemoryHistoryManager implements HistoryManager {
    // ссылка на первый элемент списка
    private Node head;
    private Node tail;
    // ссылки на элементы истории по id
    private final HashMap<Long, Node> index = new HashMap<>();

    @Override
    // добавить задачу в историю
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(index.remove(task.getId()));
        linkLast(task);
        index.put(task.getId(), tail);
    }

    // удалить запись по истории по id
    @Override
    public void remove(long id) {
        if (index.containsKey(id)) {
            Node node = index.get(id);
            removeNode(node);
            index.remove(id);
        }
    }

    @Override
    // история просмотра задач
    public List<Task> getHistory() {
        return getTasks();
    }

    // CustomLinkedList methods

    // добавить запись в конец списка
    private void linkLast(Task task) {
        Node node = new Node(task, tail, null);
        if (head == null) {
            head = node;
            tail = head;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    // получить историю в виде списка
    private List<Task> getTasks() {
        ArrayList<Task> res = new ArrayList<>();
        if (head != null) {
            Node node = head;
            while (node != null) {
                res.add(node.value);
                node = node.next;
            }
        }
        return res;
    }

    // удалить узел из списка
    private void removeNode(Node node) {
        if (node == null) return;
        if (node == head) {
            // поправим ссылку на первый элемент списка
            head = head.next;
            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }
        }
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node == tail) {
                tail = node.prev;
            }
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }


    }
}
