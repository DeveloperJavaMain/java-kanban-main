package kanban.manager;

import kanban.exception.ManagerTimeException;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

// Менеджер задачь, хранит данные в памяти

public class InMemoryTaskManager implements TaskManager {

    // менеджер истории просмотров
    private final HistoryManager historyManager = Manager.getDefaultHistory();

    // счетчик для получения уникального идентификатора
    private static long counter = 0;

    private HashMap<Long, Task> hmTasks = new HashMap<>();      // список kanban.model.Task
    private HashMap<Long, Epic> hmEpics = new HashMap<>();      // список kanban.model.Epic
    private HashMap<Long, Subtask> hmSubtasks = new HashMap<>();// список kanban.model.Subtask
    private TreeSet<Task> prioritizedTasks = new TreeSet<>();

    // methods

    protected void setCounter(long value) {
        counter = value;
    }

    // методы kanban.model.Task

    // список всех задачь
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(hmTasks.values());
    }

    // удалить все задачи
    @Override
    public void removeAllTasks() {
        for (Task task : hmTasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        hmTasks.clear();
    }

    // получить задачу по идентификатору
    @Override
    public Task getTask(long id) {
        Task task = hmTasks.get(id);
        historyManager.add(task);
        return task;
    }

    // добавить новую задачу
    protected long createTask(Task newTask, long id) {
        if (!validateIntersections(newTask)) {
            throw new ManagerTimeException("Not valid time");
        }
        newTask.setId(id);
        hmTasks.put(id, newTask);
        prioritizedTasks.add(newTask);
        return id;
    }

    @Override
    public long createTask(Task newTask) {
        if (newTask == null) {
            return -1;
        }
        long id = getNextId();
        return createTask(newTask, id);
    }

    // обновить задачу
    @Override
    public long updateTask(Task task) {
        if (task == null) {
            return -1;
        }
        if (!validateIntersections(task)) {
            throw new ManagerTimeException("Not valid time");
        }
        Task oldTask = hmTasks.remove(task.getId());
        hmTasks.put(task.getId(), task);
        prioritizedTasks.remove(oldTask);
        prioritizedTasks.add(task);
        return task.getId();
    }

    // удалить задачу
    @Override
    public boolean removeTask(long id) {
        Task task = hmTasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
        return (task != null);
    }

    // методы kanban.model.Epic

    // список всех эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(hmEpics.values());
    }

    // удалить все эпики
    @Override
    public void removeAllEpics() {
        for (Long id : hmEpics.keySet()) {
            historyManager.remove(id);
        }
        for (Subtask subtask : hmSubtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        hmEpics.clear();
        hmSubtasks.clear();
    }

    // получить эпик по идентификатору
    @Override
    public Epic getEpic(long id) {
        Epic epic = hmEpics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // добавить новый эпик
    protected long createEpic(Epic newEpic, long id) {
        newEpic.setId(id);
        hmEpics.put(id, newEpic);
        return id;
    }

    @Override
    public long createEpic(Epic newEpic) {
        if (newEpic == null) {
            return -1;
        }
        long id = getNextId();
        createEpic(newEpic, id);
        //checkEpicState(id); // обновляем статус
        return id;
    }

    // обновить эпик
    @Override
    public long updateEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }
        hmEpics.put(epic.getId(), epic);
        //checkEpicState(epic.getId()); // обновляем статус
        return epic.getId();
    }

    // удалить эпик по идентификатору
    @Override
    public boolean removeEpic(long id) {
        Epic epic = hmEpics.remove(id);
        if (epic != null) {
            // при удалении эпика удаляем все его подзадачи
            for (Subtask subtask : epic.getSubtasks()) {
                removeSubtask(subtask.getId());
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
        }
        historyManager.remove(id);
        return (epic != null);
    }

    // методы kanban.model.Subtask

    // список всех подзадачь
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(hmSubtasks.values());
    }

    // удалить все подзадачи
    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : hmSubtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        hmSubtasks.clear();
        //hmEpics.clear();
        for (Epic epic : hmEpics.values()) {
            epic.getSubtasks().clear();
                // обнавляем поля Task.state, Task.startTime и Task.duration
                epic.getState();
                epic.getStartTime();
                epic.getDuration();
        }
    }

    // получить подзадачу по идентификатору
    @Override
    public Subtask getSubtask(long id) {
        Subtask subtask = hmSubtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    protected long createSubtask(Subtask newSubtask, long id) {
        if (!validateIntersections(newSubtask)) {
            throw new ManagerTimeException("Not valid time");
        }
        newSubtask.setId(id);
        hmSubtasks.put(id, newSubtask);
        prioritizedTasks.add(newSubtask);
        // добавляем новую подзадачу к эпику
        Epic epic = hmEpics.get(newSubtask.getEpic());
        if (epic != null) {
            epic.getSubtasks().add(newSubtask);
            // обнавляем поля Task.state, Task.startTime и Task.duration
            epic.getState();
            epic.getStartTime();
            epic.getDuration();
        }
        return id;
    }

    // добавить новую подзадачу
    @Override
    public long createSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return -1;
        }
        long id = getNextId();
        createSubtask(newSubtask, id);
        // обновляем статус эпика
        //checkEpicState(newSubtask.getEpic());
        return id;
    }

    // обновить подзадачу
    @Override
    public long updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return -1;
        }
        if (!validateIntersections(subtask)) {
            throw new ManagerTimeException("Not valid time");
        }
        long id = subtask.getId();
        Subtask oldSubtask = hmSubtasks.get(id);
        // если не найдена подзадача то возвращаем ошибку
        if (oldSubtask == null) {
            return -1;
        }
        Epic epic = hmEpics.get(oldSubtask.getEpic());
        // если не найден эпик то возвращаем ошибку
        if (epic == null) {
            return -1;
        }
        // если у подзадачи поменялся эпик
        // то удаляем подзадачу у старого эпика и добавляем к новому
        if (oldSubtask.getEpic() != subtask.getEpic()) {
            Epic newEpic = hmEpics.get(subtask.getEpic());
            if (newEpic == null) {
                return -1;
            }
            // если не найден эпик то возвращаем ошибку
            epic.getSubtasks().remove(oldSubtask);
            //checkEpicState(epic.getId()); // обновляем статус старого эпика
            // обнавляем поля Task.state, Task.startTime и Task.duration
            epic.getState();
            epic.getStartTime();
            epic.getDuration();
            newEpic.getSubtasks().add(subtask);
            epic = newEpic;
        }
        prioritizedTasks.remove(oldSubtask);
        hmSubtasks.put(id, subtask);
        prioritizedTasks.add(subtask);
        ///checkEpicState(epic.getId()); // обновляем статус эпика
        return subtask.getId();
    }

    // удалить подзадачу по идентификатору
    @Override
    public boolean removeSubtask(long id) {
        Subtask subtask = hmSubtasks.get(id);
        historyManager.remove(id);
        // если подзадача не найдена то ошибка
        if (subtask == null) {
            return false;
        }
        Subtask task = hmSubtasks.remove(id);
        prioritizedTasks.remove(subtask);

        Epic epic = hmEpics.get(subtask.getEpic());
        // если эпик не найден то ошибка
        if (epic != null) {
            epic.getSubtasks().remove(subtask);
            // обнавляем поля Task.state, Task.startTime и Task.duration
            epic.getState();
            epic.getStartTime();
            epic.getDuration();
        }
        //checkEpicState(epic.getId()); // обновляем статус эпика

        return (task != null);
    }

    // additional methods

    // получить все подзадачи эпика
    @Override
    public List<Subtask> getEpicSubtasks(long epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = hmEpics.get(epicId);
        if (epic == null) {
            return result;
        }
        result.addAll(epic.getSubtasks());

        return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // получить новый идентификатор
    private long getNextId() {
        return counter++;
    }


    // get Task by ID
    protected Task getById(long id) {
        Task task = hmTasks.get(id);
        if (task != null) return task;
        task = hmSubtasks.get(id);
        if (task != null) return task;
        task = hmEpics.get(id);
        return task;
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    @Override
    public boolean validateIntersections(Task task) {
        if (task == null || task.getStartTime() == null || prioritizedTasks.size() == 0) return true;

        return !prioritizedTasks.stream().filter(t -> {
            if (t.getStartTime() == null) {
                return false;
            }
            LocalDateTime start = task.getStartTime();
            LocalDateTime end = task.getEndTime();
            if (!start.isAfter(t.getEndTime()) && end.isAfter(t.getStartTime())) return true;
            return false;
        }).findFirst().isPresent();
    }

}
