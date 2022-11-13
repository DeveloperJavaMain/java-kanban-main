// Подзадача

public class Subtask extends Task{
    // идентификатор эпика
    private long epic;

    // constructors
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
        return epic;
    }

    public void setEpic(long epic) {
        this.epic = epic;
    }

    // toString
    @Override
    public String toString() {
        return "Subtask{" + super.toString() + ", " +
                "epic=" + epic +
                '}';
    }
}
