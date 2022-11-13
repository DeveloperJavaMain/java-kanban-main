
// Задача

public class Task {
    // константы статусов
    public static final int NEW=0, IN_PROGRESS=1, DONE=2;
    // идентификатор
    private long id;
    // название и описание
    private String name, description;
    // статус
    private int state = NEW;

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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    // toString
    // возвращает текстовое название статуса
    public String getStateName()
    {
        switch (state){
            case NEW: return "New";
            case IN_PROGRESS: return "InProgress";
            case DONE: return "Done";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                '}';
    }
}
