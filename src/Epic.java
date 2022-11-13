import java.util.TreeSet;

// ����

public class Epic extends Task {
    // ������ ��������������� ���������
    private TreeSet<Long> subtasks;

    // constructors

    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    // get / set

    public TreeSet<Long> getSubtasks() {
        if(subtasks==null) subtasks = new TreeSet<>();
        return subtasks;
    }

    // toString

    @Override
    public String toString() {
        return "Epic{" +super.toString() + ", " +
                "subtasks=" + subtasks +
                '}';
    }
}

