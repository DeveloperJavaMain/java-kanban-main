package kanban.manager;

import kanban.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// хранит историю просмотров задач в памяти
public class InMemoryHistoryManager implements HistoryManager {
    // список просмотров
    //private LinkedList<Task> history = new LinkedList<>();
    // ссылка на первый элемент списка
    private Node head = null;
    // ссылки на элементы истории по id
    private final HashMap<Long, Node> index = new HashMap<>();

    @Override
    // добавить задачу в историю
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(index.get(task.getId()));
        linkLast(task);
        index.put(task.getId(), head.getPrev());
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
        Node node = new Node(task);
        if (head == null) {
            head = node;
        }
        node.insertBefore(head);
    }

    // получить историю в виде списка
    private List<Task> getTasks() {
        ArrayList<Task> res = new ArrayList<>();
        if (head != null) {
            Node node = head;
            // head - первый элемент списка
            do {
                // добавляем в списох значение из текущего узла
                res.add(node.getValue());
                // переходим к следующему узлу
                node = node.getNext();
                // если этот следующий узел head, значит мы прошли всю цепочку - заканчиваем цикл
            } while (node != head);
        }
        return res;
    }

    // удалить узел из списка
    private void removeNode(Node node) {
        if (node == null) return;
        if (node == head) {
            // поправим ссылку на первый элемент списка
            head = (head.getNext() != head) ? head.getNext() : null;
        }
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
        node.setPrev(null);
        node.setNext(null);
        node.setValue(null);
    }
}
