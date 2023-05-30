package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import server.KVServer;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest {

    private static final String url = "http://localhost:8078";

    @Override
    protected TaskManager getTaskManager() {
        return new HttpTaskManager(url);
    }

    private static KVServer server;

    @BeforeAll
    static void initTest() throws IOException {
        server = new KVServer();
        server.start();
    }

    @AfterAll
    static void closeTest() {
        server.stop();
    }

    @Test
    void saveEmpty() {
        HttpTaskManager mgr1 = new HttpTaskManager(url);
        mgr1.removeAllTasks();
        mgr1.removeAllSubtasks();
        mgr1.removeAllEpics();
        assertEquals(0, mgr1.getAllTasks().size());
        assertEquals(0, mgr1.getAllEpics().size());
        assertEquals(0, mgr1.getAllSubtasks().size());

        mgr1.save();

        HttpTaskManager mgr2 = new HttpTaskManager(url);
        assertEquals(0, mgr2.getAllTasks().size());
        assertEquals(0, mgr2.getAllEpics().size());
        assertEquals(0, mgr2.getAllSubtasks().size());
    }

    @Test
    void saveEmptyEpic() {
        HttpTaskManager mgr1 = new HttpTaskManager(url);
        mgr1.removeAllTasks();
        mgr1.removeAllSubtasks();
        mgr1.removeAllEpics();
        assertEquals(0, mgr1.getAllTasks().size());
        assertEquals(0, mgr1.getAllEpics().size());
        assertEquals(0, mgr1.getAllSubtasks().size());

        Epic epic = new Epic("epic", "description");
        mgr1.createEpic(epic);

        mgr1.save();

        HttpTaskManager mgr2 = new HttpTaskManager(url);
        assertEquals(0, mgr2.getAllTasks().size());
        assertEquals(1, mgr2.getAllEpics().size());
        assertEquals(0, mgr2.getAllSubtasks().size());
    }

    @Test
    void saveWithHistory() {
        HttpTaskManager mgr1 = new HttpTaskManager(url);
        mgr1.removeAllTasks();
        mgr1.removeAllSubtasks();
        mgr1.removeAllEpics();
        assertEquals(0, mgr1.getAllTasks().size());
        assertEquals(0, mgr1.getAllEpics().size());
        assertEquals(0, mgr1.getAllSubtasks().size());

        Task task = new Task("task", "info");
        Epic epic = new Epic("epic", "description");
        mgr1.createTask(task);
        mgr1.createEpic(epic);
        Subtask subtask = new Subtask("epic", "description", epic.getId());
        mgr1.createSubtask(subtask);

        mgr1.getSubtask(subtask.getId());
        mgr1.getEpic(epic.getId());
        mgr1.getTask(task.getId());

        mgr1.save();

        HttpTaskManager mgr2 = new HttpTaskManager(url);
        assertEquals(1, mgr2.getAllTasks().size());
        assertEquals(1, mgr2.getAllEpics().size());
        assertEquals(1, mgr2.getAllSubtasks().size());

        assertEquals(List.of(subtask, epic, task), mgr2.getHistory());
    }
}