package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    protected TaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void setCounter() {
        Task task = new Task("name", "description");
        manager.createTask(task);
        assertNotEquals(100, task.getId());
        ((InMemoryTaskManager) manager).setCounter(100);
        Task task2 = new Task("name2", "description2");
        manager.createTask(task2);
        assertEquals(100, task2.getId());
    }

    @Test
    void createTaskWithId() {
        Task task = new Task("name", "description");
        ((InMemoryTaskManager) manager).createTask(task, 10);
        assertEquals(10, task.getId());
    }

    @Test
    void createEpicWithId() {
        Epic epic = new Epic("name", "description");
        ((InMemoryTaskManager) manager).createEpic(epic, 10);
        assertEquals(10, epic.getId());
    }

    @Test
    void createSubtaskWithId() {
        Subtask subtask = new Subtask("name", "description");
        ((InMemoryTaskManager) manager).createSubtask(subtask, 10);
        assertEquals(10, subtask.getId());
    }

    @Test
    void getById() {
        Task task = new Task("name1", "description1");
        Epic epic = new Epic("name2", "description2");
        manager.createTask(task);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("name3", "description3", epic.getId());
        manager.createSubtask(subtask);
        InMemoryTaskManager inMemoryTaskManager = ((InMemoryTaskManager) manager);
        assertEquals(task, inMemoryTaskManager.getById(task.getId()));
        assertEquals(epic, inMemoryTaskManager.getById(epic.getId()));
        assertEquals(subtask, inMemoryTaskManager.getById(subtask.getId()));
    }

    @Test
    void getHistoryManager() {
        HistoryManager historyManager = ((InMemoryTaskManager) manager).getHistoryManager();
        assertNotNull(historyManager);
    }
}