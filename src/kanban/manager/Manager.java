package kanban.manager;

import java.io.File;

// управляет созданием TaskManager и HistoryManager
public class Manager {

    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078");
    }
    public static TaskManager getFileBackedTasksManager(String file) {
        return new FileBackedTasksManager(new File(file));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
