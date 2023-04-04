package kanban.model;
// Задача

import java.util.Objects;

import static kanban.model.TaskState.NEW;

public class Task {
    // идентификатор
    private long id;
    // название и описание
    private String name;
    private String description;
    // статус
    private TaskState state = NEW;

    // constructors

    public Task() {
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

// get / set

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    // toString
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                '}';
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id &&
                state == task.state &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

}
