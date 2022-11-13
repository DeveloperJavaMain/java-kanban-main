import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Менеджер задачь

public class Manager {
    // счетчик для получения уникального идентификатора
    private static long counter = 0;
    // получить новый идентификатор
    private long getNextId(){
        return counter++;
    }

    private HashMap<Long, Task> hmTasks = new HashMap<>();      // список Task
    private HashMap<Long, Epic> hmEpics = new HashMap<>();      // список Epic
    private HashMap<Long, Subtask> hmSubtasks = new HashMap<>();// список Subtask


    // methods

    // методы Task

    // список всех задачь
    public List<Task> getAllTasks(){
        return new ArrayList<>( hmTasks.values() );
    }

    // удалить все задачи
    public void removeAllTasks(){
        hmTasks.clear();
    }

    // получить задачу по идентификатору
    public Task getTask(long id){
        return hmTasks.get(id);
    }

    // добавить новую задачу
    public long createTask(Task newTask){
        if(newTask==null) return -1;
        long id = getNextId();
        newTask.setId(id);
        hmTasks.put(id,newTask);
        return id;
    }

    // обновить задачу
    public long updateTask(Task task){
        if(task==null) return -1;
        hmTasks.put(task.getId(),task);
        return task.getId();
    }

    // удалить задачу
    public boolean removeTask(long id){
        Task task = hmTasks.remove(id);
        return (task!=null);
    }

    // методы Epic

    // список всех эпиков
    public List<Epic> getAllEpics(){
        return new ArrayList<>( hmEpics.values() );
    }

    // удалить все эпики
    public void removeAllEpics(){
        hmEpics.clear();
    }

    // получить эпик по идентификатору
    public Epic getEpic(long id){
        return hmEpics.get(id);
    }

    // добавить новый эпик
    public long createEpic(Epic newEpic){
        if(newEpic==null) return -1;
        long id = getNextId();
        newEpic.setId(id);
        hmEpics.put(id,newEpic);
        checkEpicState(id); // обновляем статус
        return id;
    }

    // обновить эпик
    public long updateEpic(Epic epic){
        if(epic==null) return -1;
        hmEpics.put(epic.getId(),epic);
        checkEpicState(epic.getId()); // обновляем статус
        return epic.getId();
    }

    // удалить эпик по идентификатору
    public boolean removeEpic(long id){
        Epic task = hmEpics.remove(id);
        if(task!=null){
            // при удалении эпика удаляем все подзадачи
            for(long subtaskId: task.getSubtasks()){
                removeSubtask(subtaskId);
            }
        }
        return (task!=null);
    }

    // методы Subtask

    // список всех подзадачь
    public List<Subtask> getAllSubtasks(){
        return new ArrayList<>( hmSubtasks.values() );
    }

    // удалить все подзадачи
    public void removeAllSubtasks(){
        hmSubtasks.clear();
    }

    // получить подзадачу по идентификатору
    public Subtask getSubtask(long id){
        return hmSubtasks.get(id);
    }

    // добавить новую подзадачу
    public long createSubtask(Subtask newSubtask){
        if(newSubtask==null) return -1;
        long epicId = newSubtask.getEpic();
        Epic epic = getEpic(epicId);
        // если не найден эпик то возвращаем ошибку
        if(epic==null) return -1;
        long id = getNextId();
        newSubtask.setId(id);
        hmSubtasks.put(id,newSubtask);
        // добавляем новую подзадачу к эпику
        epic.getSubtasks().add(id);
        // обновляем статус эпика
        checkEpicState(id);
        return id;
    }

    // обновить подзадачу
    public long updateSubtask(Subtask subtask){
        if(subtask==null) return -1;
        long id = subtask.getId();
        Subtask oldSubtask = getSubtask(id);
        // если не найдена подзадача то возвращаем ошибку
        if(oldSubtask==null) return -1;
        Epic epic = getEpic(oldSubtask.getEpic());
        // если не найден эпик то возвращаем ошибку
        if(epic==null) return -1;
        // если у подзадачи поменялся эпик
        // то удаляем подзадачу у старого эпика и добавляем к новому
        if(oldSubtask.getEpic()!=subtask.getEpic()){
            Epic newEpic = getEpic(subtask.getEpic());
            if(newEpic==null) return -1;
            // если не найден эпик то возвращаем ошибку
            epic.getSubtasks().remove(id);
            checkEpicState(epic.getId()); // обновляем статус старого эпика
            newEpic.getSubtasks().add(id);
            epic = newEpic;
        }
        hmSubtasks.put(id,subtask);
        checkEpicState(epic.getId()); // обновляем статус эпика
        return subtask.getId();
    }

    // удалить подзадачу по идентификатору
    public boolean removeSubtask(long id){
        Subtask subtask = getSubtask(id);
        // если подзадача не найдена то ошибка
        if(subtask==null) return false;
        Epic epic = getEpic(subtask.getEpic());
        // если эпик не найден то ошибка
        if(epic==null) return false;
        epic.getSubtasks().remove(id);
        checkEpicState(epic.getId()); // обновляем статус эпика
        Subtask task = hmSubtasks.remove(id);
        return (task!=null);
    }

    // additional methods

    // получить все подзадачи эпика
    public List<Subtask> getEpicSubtasks(long epicId){
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = getEpic(epicId);
        if(epic==null) return result;
        for(long id: epic.getSubtasks()){
            Subtask subtask = getSubtask(id);
            if(subtask!=null) result.add(subtask);
        }
        return result;
    }

    // рассчет статуса эпика
    private void checkEpicState(long id){
        boolean flagNew = false, flagInProgress = false, flagDone = false;
        Epic epic = getEpic(id);
        if(epic == null) return;
        //по умолчанию статус NEW
        if(epic.getSubtasks().size()==0) epic.setState(Task.NEW);
        //проверяем статусы подзадачь
        for(Long subtaskId: epic.getSubtasks()){
            Subtask subtask = hmSubtasks.get(subtaskId);
            if(subtask==null) continue;
            switch (subtask.getState()) {
                case Task.NEW: flagNew = true; break;
                case Task.IN_PROGRESS: flagInProgress = true; break;
                case Task.DONE: flagDone = true; break;
            }
        }
        // если все подзадачи в статусе NEW, то статус эпика NEW
        if(!flagInProgress && !flagDone){
            epic.setState(Task.NEW);
        } else if(!flagNew && !flagInProgress){
            // если все подзадачи в статусе DONE, то статус эпика DONE
            epic.setState(Task.DONE);
        } else {
            // иначе статус эпика IN_PROGRESS
            epic.setState(Task.IN_PROGRESS);
        }
    }
}
