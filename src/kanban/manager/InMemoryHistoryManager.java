package kanban.manager;

import kanban.model.Task;

import java.util.LinkedList;
import java.util.List;

// ������ ������� ���������� ����� � ������
public class InMemoryHistoryManager implements HistoryManager {
    // ������ ����������
    private LinkedList<Task> history = new LinkedList<>();
    // ������������ ������� ������
    private final int LIMIT = 10;

    @Override
    // �������� ������ � �������
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
    // ��������� 10 ������������� �����
    public List<Task> getHistory() {
        return history;
    }
}
