package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static kanban.model.TaskState.*;

// �������� ������, ������ ������ � ������

public class InMemoryTaskManager implements TaskManager {

    // �������� ������� ����������
    private final HistoryManager historyManager = Manager.getDefaultHistory();

    // ������� ��� ��������� ����������� ��������������
    private static long counter = 0;

    private HashMap<Long, Task> hmTasks = new HashMap<>();      // ������ kanban.model.Task
    private HashMap<Long, Epic> hmEpics = new HashMap<>();      // ������ kanban.model.Epic
    private HashMap<Long, Subtask> hmSubtasks = new HashMap<>();// ������ kanban.model.Subtask

    // methods

    // ������ kanban.model.Task

    // ������ ���� ������
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>( hmTasks.values() );
    }

    // ������� ��� ������
    @Override
    public void removeAllTasks() {
        hmTasks.clear();
    }

    // �������� ������ �� ��������������
    @Override
    public Task getTask(long id) {
        Task task = hmTasks.get(id);
        historyManager.add(task);
        return task;
    }

    // �������� ����� ������
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

    // �������� ������
    @Override
    public long updateTask(Task task){
        if (task == null) {
            return -1;
        }
        hmTasks.put(task.getId(),task);
        return task.getId();
    }

    // ������� ������
    @Override
    public boolean removeTask(long id) {
        Task task = hmTasks.remove(id);
        return (task != null);
    }

    // ������ kanban.model.Epic

    // ������ ���� ������
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>( hmEpics.values() );
    }

    // ������� ��� �����
    @Override
    public void removeAllEpics() {
        hmEpics.clear();
        hmSubtasks.clear();
    }

    // �������� ���� �� ��������������
    @Override
    public Epic getEpic(long id) {
        Epic epic = hmEpics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // �������� ����� ����
    @Override
    public long createEpic(Epic newEpic) {
        if (newEpic == null) {
            return -1;
        }
        long id = getNextId();
        newEpic.setId(id);
        hmEpics.put(id,newEpic);
        checkEpicState(id); // ��������� ������
        return id;
    }

    // �������� ����
    @Override
    public long updateEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }
        hmEpics.put(epic.getId(),epic);
        checkEpicState(epic.getId()); // ��������� ������
        return epic.getId();
    }

    // ������� ���� �� ��������������
    @Override
    public boolean removeEpic(long id) {
        Epic epic = hmEpics.remove(id);
        if (epic != null) {
            // ��� �������� ����� ������� ��� ���������
            for (long subtaskId: epic.getSubtaskIds()) {
                removeSubtask(subtaskId);
            }
        }
        return (epic != null);
    }

    // ������ kanban.model.Subtask

    // ������ ���� ���������
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>( hmSubtasks.values() );
    }

    // ������� ��� ���������
    @Override
    public void removeAllSubtasks() {
        hmSubtasks.clear();
        hmEpics.clear();
    }

    // �������� ��������� �� ��������������
    @Override
    public Subtask getSubtask(long id) {
        Subtask subtask = hmSubtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // �������� ����� ���������
    @Override
    public long createSubtask(Subtask newSubtask) {
        if (newSubtask == null) {
            return -1;
        }
        long epicId = newSubtask.getEpic();
        Epic epic = hmEpics.get(epicId);
        // ���� �� ������ ���� �� ���������� ������
        if (epic == null) {
            return -1;
        }
        long id = getNextId();
        newSubtask.setId(id);
        hmSubtasks.put(id,newSubtask);
        // ��������� ����� ��������� � �����
        epic.getSubtaskIds().add(id);
        // ��������� ������ �����
        checkEpicState(id);
        return id;
    }

    // �������� ���������
    @Override
    public long updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return -1;
        }
        long id = subtask.getId();
        Subtask oldSubtask = hmSubtasks.get(id);
        // ���� �� ������� ��������� �� ���������� ������
        if (oldSubtask == null) {
            return -1;
        }
        Epic epic = hmEpics.get(oldSubtask.getEpic());
        // ���� �� ������ ���� �� ���������� ������
        if (epic == null) {
            return -1;
        }
        // ���� � ��������� ��������� ����
        // �� ������� ��������� � ������� ����� � ��������� � ������
        if (oldSubtask.getEpic() != subtask.getEpic()) {
            Epic newEpic = hmEpics.get(subtask.getEpic());
            if (newEpic == null) {
                return -1;
            }
            // ���� �� ������ ���� �� ���������� ������
            epic.getSubtaskIds().remove(id);
            checkEpicState(epic.getId()); // ��������� ������ ������� �����
            newEpic.getSubtaskIds().add(id);
            epic = newEpic;
        }
        hmSubtasks.put(id,subtask);
        checkEpicState(epic.getId()); // ��������� ������ �����
        return subtask.getId();
    }

    // ������� ��������� �� ��������������
    @Override
    public boolean removeSubtask(long id) {
        Subtask subtask = hmSubtasks.get(id);
        // ���� ��������� �� ������� �� ������
        if (subtask == null) {
            return false;
        }
        Epic epic = hmEpics.get(subtask.getEpic());
        // ���� ���� �� ������ �� ������
        if (epic == null) {
            return false;
        }
        epic.getSubtaskIds().remove(id);
        checkEpicState(epic.getId()); // ��������� ������ �����
        Subtask task = hmSubtasks.remove(id);
        return (task != null);
    }

    // additional methods

    // �������� ��� ��������� �����
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

    // ��������� 10 ������������� �����
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // �������� ����� �������������
    private long getNextId() {
        return counter++;
    }

    // ������� ������� �����
    private void checkEpicState(long id){
        boolean flagNew = false;
        boolean flagInProgress = false;
        boolean flagDone = false;
        Epic epic = hmEpics.get(id);
        if (epic == null) {
            return;
        }
        //�� ��������� ������ NEW
        if (epic.getSubtaskIds().size() == 0) {
            epic.setState(NEW);
        }
        //��������� ������� ���������
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
        // ���� ��� ��������� � ������� NEW, �� ������ ����� NEW
        if (!flagInProgress && !flagDone) {
            epic.setState(TaskState.NEW);
        } else if (!flagNew && !flagInProgress){
            // ���� ��� ��������� � ������� DONE, �� ������ ����� DONE
            epic.setState(TaskState.DONE);
        } else {
            // ����� ������ ����� IN_PROGRESS
            epic.setState(TaskState.IN_PROGRESS);
        }
    }
}
