package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.List;

// Менеджер задачь

public interface TaskManager {
    // список всех задачь
    List<Task> getAllTasks();

    // удалить все задачи
    void removeAllTasks();

    // получить задачу по идентификатору
    Task getTask(long id);

    // добавить новую задачу
    long createTask(Task newTask);

    // обновить задачу
    long updateTask(Task task);

    // удалить задачу
    boolean removeTask(long id);

    // список всех эпиков
    List<Epic> getAllEpics();

    // удалить все эпики
    void removeAllEpics();

    // получить эпик по идентификатору
    Epic getEpic(long id);

    // добавить новый эпик
    long createEpic(Epic newEpic);

    // обновить эпик
    long updateEpic(Epic epic);

    // удалить эпик по идентификатору
    boolean removeEpic(long id);

    // список всех подзадачь
    List<Subtask> getAllSubtasks();

    // удалить все подзадачи
    void removeAllSubtasks();

    // получить подзадачу по идентификатору
    Subtask getSubtask(long id);

    // добавить новую подзадачу
    long createSubtask(Subtask newSubtask);

    // обновить подзадачу
    long updateSubtask(Subtask subtask);

    // удалить подзадачу по идентификатору
    boolean removeSubtask(long id);

    // получить все подзадачи эпика
    List<Subtask> getEpicSubtasks(long epicId);

    // последние 10 просмотренных задач
    List<Task> getHistory();

    // список task и subtask в порядке startTime
    List<Task> getPrioritizedTasks();

    // проверка пересечений
    boolean validateIntersections(Task task);

}
