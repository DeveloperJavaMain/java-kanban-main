package kanban.manager;

import kanban.model.Task;

import java.util.List;

// ������ ������� ���������� �����
public interface HistoryManager {
    // �������� ������ � �������
    void add(Task task);
    // ��������� 10 ������������� �����
    List<Task> getHistory();
}