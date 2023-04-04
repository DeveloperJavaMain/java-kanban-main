package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.util.List;

// �������� ������

public interface TaskManager {
    // ������ ���� ������
    List<Task> getAllTasks();

    // ������� ��� ������
    void removeAllTasks();

    // �������� ������ �� ��������������
    Task getTask(long id);

    // �������� ����� ������
    long createTask(Task newTask);

    // �������� ������
    long updateTask(Task task);

    // ������� ������
    boolean removeTask(long id);

    // ������ ���� ������
    List<Epic> getAllEpics();

    // ������� ��� �����
    void removeAllEpics();

    // �������� ���� �� ��������������
    Epic getEpic(long id);

    // �������� ����� ����
    long createEpic(Epic newEpic);

    // �������� ����
    long updateEpic(Epic epic);

    // ������� ���� �� ��������������
    boolean removeEpic(long id);

    // ������ ���� ���������
    List<Subtask> getAllSubtasks();

    // ������� ��� ���������
    void removeAllSubtasks();

    // �������� ��������� �� ��������������
    Subtask getSubtask(long id);

    // �������� ����� ���������
    long createSubtask(Subtask newSubtask);

    // �������� ���������
    long updateSubtask(Subtask subtask);

    // ������� ��������� �� ��������������
    boolean removeSubtask(long id);

    // �������� ��� ��������� �����
    List<Subtask> getEpicSubtasks(long epicId);

    // ��������� 10 ������������� �����
    List<Task> getHistory();
}
