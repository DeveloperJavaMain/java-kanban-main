package kanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void emptyHistory() {
        List<Task> list = historyManager.getHistory();
        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    void doubleTasksHistory() {
        List<Task> list = historyManager.getHistory();
        assertEquals(0, list.size());

        Task task = new Task("task", "info");
        task.setId(1);
        historyManager.add(task);
        list = historyManager.getHistory();
        assertEquals(List.of(task), list);

        historyManager.add(task);
        list = historyManager.getHistory();
        assertEquals(List.of(task), list);

        Task task2 = new Task("task2", "info2");
        task2.setId(2);
        historyManager.add(task2);
        list = historyManager.getHistory();
        assertEquals(List.of(task, task2), list);

        historyManager.add(task2);
        list = historyManager.getHistory();
        assertEquals(List.of(task, task2), list);

        historyManager.add(task);
        list = historyManager.getHistory();
        assertEquals(List.of(task2, task), list);

    }

    @Test
    void removeFirstHistory() {
        List<Task> list = historyManager.getHistory();
        assertNotNull(list);
        assertEquals(0, list.size());

        Task task = new Task("task", "info");
        task.setId(1);
        Task task2 = new Task("task2", "info2");
        task2.setId(2);
        Task task3 = new Task("task3", "info3");
        task2.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        list = historyManager.getHistory();
        assertEquals(List.of(task, task2, task3), list);

        historyManager.remove(task.getId());

        list = historyManager.getHistory();
        assertEquals(List.of(task2, task3), list);
    }

    @Test
    void removeMiddleHistory() {
        List<Task> list = historyManager.getHistory();
        assertNotNull(list);
        assertEquals(0, list.size());

        Task task = new Task("task", "info");
        task.setId(1);
        Task task2 = new Task("task2", "info2");
        task2.setId(2);
        Task task3 = new Task("task3", "info3");
        task2.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        list = historyManager.getHistory();
        assertEquals(List.of(task, task2, task3), list);

        historyManager.remove(task2.getId());

        list = historyManager.getHistory();
        assertEquals(List.of(task, task3), list);
    }

    @Test
    void removeLastHistory() {
        List<Task> list = historyManager.getHistory();
        assertNotNull(list);
        assertEquals(0, list.size());

        Task task = new Task("task", "info");
        task.setId(1);
        Task task2 = new Task("task2", "info2");
        task2.setId(2);
        Task task3 = new Task("task3", "info3");
        task2.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);

        list = historyManager.getHistory();
        assertEquals(List.of(task, task2, task3), list);

        historyManager.remove(task3.getId());

        list = historyManager.getHistory();
        assertEquals(List.of(task, task2), list);
    }

}