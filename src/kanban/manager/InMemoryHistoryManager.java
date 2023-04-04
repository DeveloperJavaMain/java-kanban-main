package kanban.manager;

import kanban.model.Task;

import java.util.LinkedList;
import java.util.List;

// хранит историю просмотров задач в памяти
public class InMemoryHistoryManager implements HistoryManager {
    // список просмотров
    private LinkedList<Task> history = new LinkedList<>();
    // максимальная глубина списка
    private final int LIMIT = 10;

    @Override
    // добавить задачу в историю
    public void add(Task task) {
        if (task == null){
            return;
        }
        if (history.size() >= LIMIT) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    // последние 10 просмотренных задач
    public List<Task> getHistory() {
        return history;
    }
}
