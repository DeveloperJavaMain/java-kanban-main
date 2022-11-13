import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// �������� ������

public class Manager {
    // ������� ��� ��������� ����������� ��������������
    private static long counter = 0;
    // �������� ����� �������������
    private long getNextId(){
        return counter++;
    }

    private HashMap<Long, Task> hmTasks = new HashMap<>();      // ������ Task
    private HashMap<Long, Epic> hmEpics = new HashMap<>();      // ������ Epic
    private HashMap<Long, Subtask> hmSubtasks = new HashMap<>();// ������ Subtask


    // methods

    // ������ Task

    // ������ ���� ������
    public List<Task> getAllTasks(){
        return new ArrayList<>( hmTasks.values() );
    }

    // ������� ��� ������
    public void removeAllTasks(){
        hmTasks.clear();
    }

    // �������� ������ �� ��������������
    public Task getTask(long id){
        return hmTasks.get(id);
    }

    // �������� ����� ������
    public long createTask(Task newTask){
        if(newTask==null) return -1;
        long id = getNextId();
        newTask.setId(id);
        hmTasks.put(id,newTask);
        return id;
    }

    // �������� ������
    public long updateTask(Task task){
        if(task==null) return -1;
        hmTasks.put(task.getId(),task);
        return task.getId();
    }

    // ������� ������
    public boolean removeTask(long id){
        Task task = hmTasks.remove(id);
        return (task!=null);
    }

    // ������ Epic

    // ������ ���� ������
    public List<Epic> getAllEpics(){
        return new ArrayList<>( hmEpics.values() );
    }

    // ������� ��� �����
    public void removeAllEpics(){
        hmEpics.clear();
    }

    // �������� ���� �� ��������������
    public Epic getEpic(long id){
        return hmEpics.get(id);
    }

    // �������� ����� ����
    public long createEpic(Epic newEpic){
        if(newEpic==null) return -1;
        long id = getNextId();
        newEpic.setId(id);
        hmEpics.put(id,newEpic);
        checkEpicState(id); // ��������� ������
        return id;
    }

    // �������� ����
    public long updateEpic(Epic epic){
        if(epic==null) return -1;
        hmEpics.put(epic.getId(),epic);
        checkEpicState(epic.getId()); // ��������� ������
        return epic.getId();
    }

    // ������� ���� �� ��������������
    public boolean removeEpic(long id){
        Epic task = hmEpics.remove(id);
        if(task!=null){
            // ��� �������� ����� ������� ��� ���������
            for(long subtaskId: task.getSubtasks()){
                removeSubtask(subtaskId);
            }
        }
        return (task!=null);
    }

    // ������ Subtask

    // ������ ���� ���������
    public List<Subtask> getAllSubtasks(){
        return new ArrayList<>( hmSubtasks.values() );
    }

    // ������� ��� ���������
    public void removeAllSubtasks(){
        hmSubtasks.clear();
    }

    // �������� ��������� �� ��������������
    public Subtask getSubtask(long id){
        return hmSubtasks.get(id);
    }

    // �������� ����� ���������
    public long createSubtask(Subtask newSubtask){
        if(newSubtask==null) return -1;
        long epicId = newSubtask.getEpic();
        Epic epic = getEpic(epicId);
        // ���� �� ������ ���� �� ���������� ������
        if(epic==null) return -1;
        long id = getNextId();
        newSubtask.setId(id);
        hmSubtasks.put(id,newSubtask);
        // ��������� ����� ��������� � �����
        epic.getSubtasks().add(id);
        // ��������� ������ �����
        checkEpicState(id);
        return id;
    }

    // �������� ���������
    public long updateSubtask(Subtask subtask){
        if(subtask==null) return -1;
        long id = subtask.getId();
        Subtask oldSubtask = getSubtask(id);
        // ���� �� ������� ��������� �� ���������� ������
        if(oldSubtask==null) return -1;
        Epic epic = getEpic(oldSubtask.getEpic());
        // ���� �� ������ ���� �� ���������� ������
        if(epic==null) return -1;
        // ���� � ��������� ��������� ����
        // �� ������� ��������� � ������� ����� � ��������� � ������
        if(oldSubtask.getEpic()!=subtask.getEpic()){
            Epic newEpic = getEpic(subtask.getEpic());
            if(newEpic==null) return -1;
            // ���� �� ������ ���� �� ���������� ������
            epic.getSubtasks().remove(id);
            checkEpicState(epic.getId()); // ��������� ������ ������� �����
            newEpic.getSubtasks().add(id);
            epic = newEpic;
        }
        hmSubtasks.put(id,subtask);
        checkEpicState(epic.getId()); // ��������� ������ �����
        return subtask.getId();
    }

    // ������� ��������� �� ��������������
    public boolean removeSubtask(long id){
        Subtask subtask = getSubtask(id);
        // ���� ��������� �� ������� �� ������
        if(subtask==null) return false;
        Epic epic = getEpic(subtask.getEpic());
        // ���� ���� �� ������ �� ������
        if(epic==null) return false;
        epic.getSubtasks().remove(id);
        checkEpicState(epic.getId()); // ��������� ������ �����
        Subtask task = hmSubtasks.remove(id);
        return (task!=null);
    }

    // additional methods

    // �������� ��� ��������� �����
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

    // ������� ������� �����
    private void checkEpicState(long id){
        boolean flagNew = false, flagInProgress = false, flagDone = false;
        Epic epic = getEpic(id);
        if(epic == null) return;
        //�� ��������� ������ NEW
        if(epic.getSubtasks().size()==0) epic.setState(Task.NEW);
        //��������� ������� ���������
        for(Long subtaskId: epic.getSubtasks()){
            Subtask subtask = hmSubtasks.get(subtaskId);
            if(subtask==null) continue;
            switch (subtask.getState()) {
                case Task.NEW: flagNew = true; break;
                case Task.IN_PROGRESS: flagInProgress = true; break;
                case Task.DONE: flagDone = true; break;
            }
        }
        // ���� ��� ��������� � ������� NEW, �� ������ ����� NEW
        if(!flagInProgress && !flagDone){
            epic.setState(Task.NEW);
        } else if(!flagNew && !flagInProgress){
            // ���� ��� ��������� � ������� DONE, �� ������ ����� DONE
            epic.setState(Task.DONE);
        } else {
            // ����� ������ ����� IN_PROGRESS
            epic.setState(Task.IN_PROGRESS);
        }
    }
}
