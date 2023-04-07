package kanban.manager;

import kanban.model.Task;

import java.util.List;

// хранит историю просмотров задач
public interface HistoryManager {
    // добавить задачу в историю
    void add(Task task);
    // удалить запись из истории
    void remove(long id);
    // последние 10 просмотренных задач
    List<Task> getHistory();


}