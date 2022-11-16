package kanban.model;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;

// Эпик

public class Epic extends Task {
    // список идентификаторов подзадачь
    private ArrayList<Long> subtaskIds;

    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    // get / set

    public ArrayList<Long> getSubtaskIds() {
        if(subtaskIds ==null) subtaskIds = new ArrayList<>();
        return subtaskIds;
    }

    // toString

    @Override
    public String toString() {
        return "kanban.model.Epic{" +super.toString() + ", " +
                "subtasks=" + subtaskIds +
                '}';
    }

    // equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

}
