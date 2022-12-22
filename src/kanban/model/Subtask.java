package kanban.model;
// Подзадача

import java.util.Objects;

public class Subtask extends Task {
    // идентификатор эпика
    private long epicId;


    public Subtask() {
    }

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, long epicId) {
        super(name, description);
        setEpic(epicId);
    }

    // get / set

    public long getEpic() {
        return epicId;
    }

    public void setEpic(long epic) {
        this.epicId = epic;
    }

    // toString

    @Override
    public String toString() {
        return "kanban.model.Subtask{" + super.toString() + ", " +
                "epic=" + epicId +
                '}';
    }

    // equals

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subtask)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

}
