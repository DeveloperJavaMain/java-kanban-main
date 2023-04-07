package kanban.manager;

import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// хранит историю просмотров задач в памяти
public class InMemoryHistoryManager implements HistoryManager {
    // список просмотров
    //private LinkedList<Task> history = new LinkedList<>();
    // ссылка на первый элемент списка
    Node history = null;
    // ссылки на элементы истории по id
    HashMap<Long,Node> index = new HashMap<>();

    @Override
    // добавить задачу в историю
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        Node node = linkLast(task);
        index.put(task.getId(), node);
    }

    // удалить запись по истории по id
    @Override
    public void remove(long id) {
        if(index.containsKey(id)){
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
    private Node linkLast(Task task) {
        if (history == null) {
            history = new Node(task);
            history.setPrev(history);
            history.setNext(history);
            return history;
        } else {
            Node node = new Node(task);
            node.setNext(history);
            node.setPrev(history.getPrev());
            history.getPrev().setNext(node);
            history.setPrev(node);
            return node;
        }
    }

    // получить историю в виде списка
    private List<Task> getTasks() {
        ArrayList<Task> res = new ArrayList<>();
        if (history != null) {
            Node node = history;
            do {
                res.add(node.getValue());
                node = node.getNext();
            } while (node != history);
        }
        return res;
    }

    // удалить узел из списка
    private void removeNode(Node node){
        if(node==null) return;
        if(node==history){
            // поправим ссылку на первый элемент списка
            history = (history.getNext()!=history) ? history.getNext(): null;
        }
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
        node.setPrev(null);
        node.setNext(null);
        node.setValue(null);
    }
}
