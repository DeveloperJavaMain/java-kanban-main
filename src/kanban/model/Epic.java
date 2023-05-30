package kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static kanban.model.TaskState.NEW;

// Эпик

public class Epic extends Task {
    // список идентификаторов подзадачь
    private TreeSet<Subtask> subtasks = new TreeSet<>();

    public Epic() {
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    // get / set

    public Set<Subtask> getSubtasks() {
        return subtasks;
    }

//    public void setEndTime(LocalDateTime endTime) {
//        this.endTime = endTime;
//    }

    // toString
    @Override
    public String toString() {
        return "Epic{" + super.toString() + ", " +
                "subtasks=" + subtasks +
                '}';
    }

    @Override
    public void setState(TaskState state) {
        // do nothing
    }

    @Override
    public TaskState getState() {
        //по умолчанию статус NEW
        super.setState(NEW);
        if (subtasks.size() == 0) {
            return NEW;
        }

        boolean flagNew = false;
        boolean flagInProgress = false;
        boolean flagDone = false;

        //проверяем статусы подзадачь
        for (Subtask subtask : subtasks) {
            if (subtask == null) {
                continue;
            }
            switch (subtask.getState()) {
                case NEW:
                    flagNew = true;
                    break;
                case IN_PROGRESS:
                    flagInProgress = true;
                    break;
                case DONE:
                    flagDone = true;
                    break;
            }
        }
        // если все подзадачи в статусе NEW, то статус эпика NEW
        TaskState newState;
        if (!flagInProgress && !flagDone) {
            newState = TaskState.NEW;
        } else if (!flagNew && !flagInProgress) {
            // если все подзадачи в статусе DONE, то статус эпика DONE
            newState = TaskState.DONE;
        } else {
            // иначе статус эпика IN_PROGRESS
            newState = TaskState.IN_PROGRESS;
        }
        super.setState(newState);
        return newState;
    }

    @Override
    public void setDuration(int duration) {
        // can't set
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        // can't set
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime time = (subtasks.size() == 0) ? null : subtasks.first().getStartTime();
        super.setStartTime(time);
        return time;
    }

    @Override
    public int getDuration() {
        int duration = subtasks.stream().mapToInt(s -> s.getDuration()).sum();
        super.setDuration(duration);
        return duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return (subtasks.size() == 0) ? null : subtasks.last().getEndTime();
    }


    // equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }
}
