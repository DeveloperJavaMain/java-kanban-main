package kanban.model;
// Задача

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

import static kanban.model.TaskState.NEW;

public class Task implements Comparable<Task> {
    private long id; // идентификатор
    private String name; // название
    private String description; //описание
    private TaskState state = NEW; // статус
    private int duration; // длительность в минутах
    private LocalDateTime startTime;

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null) ? startTime.plusMinutes(duration) : null;
    }

    @Override
    public int compareTo(Task task) {
        if (startTime != null || task.startTime != null) {
            if (startTime == null) return 1;
            if (task.startTime == null) return -1;
            if (startTime.isBefore(task.startTime)) return -1;
            if (startTime.isAfter(task.startTime)) return 1;
        }
        return (int) (id - task.id);
    }

    // toString
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && duration == task.duration
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && state == task.state &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, state, duration, startTime);
    }
}
