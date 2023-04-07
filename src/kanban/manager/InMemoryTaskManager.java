package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static kanban.model.TaskState.*;

// Менеджер задачь, хранит данные в памяти

public class InMemoryTaskManager implements TaskManager {

    // менеджер истории просмотров
    private final HistoryManager historyManager = Manager.getDefaultHistory();

    // счетчик для получения уникального идентификатора
    private static long counter = 0;

    private HashMap<Long, Task> hmTasks = new HashMap<>();      // список kanban.model.Task
    private HashMap<Long, Epic> hmEpics = new HashMap<>();      // список kanban.model.Epic
    private HashMap<Long, Subtask> hmSubtasks = new HashMap<>();// список kanban.model.Subtask

    // methods

    // методы kanban.model.Task

    // список всех задачь
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>( hmTasks.values() );
    }

    // удалить все задачи
    @Override
    public void removeAllTasks() {
        for(Long id: hmTasks.keySet()){
            historyManager.remove(id);
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
    @Override
    public long createTask(Task newTask) {
        if (newTask == null) {
            return -1;
        }
        long id = getNextId();
        newTask.setId(id);
        hmTasks.put(id,newTask);
        return id;
    }

    // обновить задачу
    @Override
    public long updateTask(Task task){
        if (task == null) {
            return -1;
        }
        hmTasks.put(task.getId(),task);
        return task.getId();
    }

    // удалить задачу
    @Override
    public boolean removeTask(long id) {
        Task task = hmTasks.remove(id);
        historyManager.remove(id);
        return (task != null);
    }

    // методы kanban.model.Epic

    // список всех эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>( hmEpics.values() );
    }

    // удалить все эпики
    @Override
    public void removeAllEpics() {
        for(Long id: hmEpics.keySet()){
            historyManager.remove(id);
        }
        for(Long id: hmSubtasks.keySet()){
            historyManager.remove(id);
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
    @Override
    public long createEpic(Epic newEpic) {
        if (newEpic == null) {
            return -1;
        }
        long id = getNextId();
        newEpic.setId(id);
        hmEpics.put(id,newEpic);
        checkEpicState(id); // обновляем статус
        return id;
    }

    // обновить эпик
    @Override
    public long updateEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }
        hmEpics.put(epic.getId(),epic);
        checkEpicState(epic.getId()); // обновляем статус
        return epic.getId();
    }

    // удалить эпик по идентификатору
    @Override
    public boolean removeEpic(long id) {
        Epic epic = hmEpics.remove(id);
        if (epic != null) {
            // при удалении эпика удаляем все подзадачи
            for (long subtaskId: epic.getSubtaskIds()) {
                removeSubtask(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(id);
        return (epic != null);
    }

    // методы kanban.model.Subtask

    // список всех подзадачь
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>( hmSubtasks.values() );
    }

    // удалить все подзадачи
    @Override
    public void removeAllSubtasks() {
        for(Long id: hmEpics.keySet()){
            historyManager.remove(id);
        }
        for(Long id: hmSubtasks.keySet()){
            historyManager.remove(id);
        }
        hmSubtasks.clear();
        hmEpics.clear();
    }

    // получить подзадачу по идентификатору
    @Override
    public Subtask getSubtask(long id) {
        Subtask subtask = hmSubtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // добавить новую подзадачу
    @Override
    public long createSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return -1;
        }
        long epicId = newSubtask.getEpic();
        Epic epic = hmEpics.get(epicId);
        // если не найден эпик то возвращаем ошибку
        if (epic == null) {
            return -1;
        }
        long id = getNextId();
        newSubtask.setId(id);
        hmSubtasks.put(id,newSubtask);
        // добавляем новую подзадачу к эпику
        epic.getSubtaskIds().add(id);
        // обновляем статус эпика
        checkEpicState(id);
        return id;
    }

    // обновить подзадачу
    @Override
    public long updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return -1;
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
            epic.getSubtaskIds().remove(id);
            checkEpicState(epic.getId()); // обновляем статус старого эпика
            newEpic.getSubtaskIds().add(id);
            epic = newEpic;
        }
        hmSubtasks.put(id,subtask);
        checkEpicState(epic.getId()); // обновляем статус эпика
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
        Epic epic = hmEpics.get(subtask.getEpic());
        // если эпик не найден то ошибка
        if (epic == null) {
            return false;
        }
        epic.getSubtaskIds().remove(id);
        checkEpicState(epic.getId()); // обновляем статус эпика
        Subtask task = hmSubtasks.remove(id);
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
        for (long id: epic.getSubtaskIds()) {
            Subtask subtask = hmSubtasks.get(id);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    // последние 10 просмотренных задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // получить новый идентификатор
    private long getNextId() {
        return counter++;
    }

    // рассчет статуса эпика
    private void checkEpicState(long id){
        boolean flagNew = false;
        boolean flagInProgress = false;
        boolean flagDone = false;
        Epic epic = hmEpics.get(id);
        if (epic == null) {
            return;
        }
        //по умолчанию статус NEW
        if (epic.getSubtaskIds().size() == 0) {
            epic.setState(NEW);
        }
        //проверяем статусы подзадачь
        for (Long subtaskId: epic.getSubtaskIds()) {
            Subtask subtask = hmSubtasks.get(subtaskId);
            if (subtask == null) {
                continue;
            }
            switch (subtask.getState()) {
                case NEW: flagNew = true;
                break;
                case IN_PROGRESS: flagInProgress = true;
                break;
                case DONE: flagDone = true;
                break;
            }
        }
        // если все подзадачи в статусе NEW, то статус эпика NEW
        if (!flagInProgress && !flagDone) {
            epic.setState(TaskState.NEW);
        } else if (!flagNew && !flagInProgress){
            // если все подзадачи в статусе DONE, то статус эпика DONE
            epic.setState(TaskState.DONE);
        } else {
            // иначе статус эпика IN_PROGRESS
            epic.setState(TaskState.IN_PROGRESS);
        }
    }
}
