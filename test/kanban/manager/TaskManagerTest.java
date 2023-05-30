package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest {


    abstract protected TaskManager getTaskManager();

    protected TaskManager manager;

    @BeforeEach
    void init() {
        manager = getTaskManager();
        clear();
    }

    void clear() {
        // clear data
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
    }

    @Test
    void getAllTasks() {
        Task task1 = new Task("name1", "description");
        Task task2 = new Task("name2", "description2");
        Task task3 = new Task("name3", "description3");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks);
        assertEquals(3, tasks.size());
    }

    @Test
    void removeAllTasks() {
        Task task1 = new Task("name1", "description");
        Task task2 = new Task("name2", "description2");
        Task task3 = new Task("name3", "description3");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks);
        assertEquals(3, tasks.size());

        manager.removeAllTasks();

        tasks = manager.getAllTasks();

        assertEquals(0, tasks.size());
    }

    @Test
    void getTask() {
        Task task = new Task("name", "description");
        manager.createTask(task);
        assertEquals(task, manager.getTask(task.getId()));
        assertNull(manager.getTask(10));
    }

    @Test
    void createTask() {
        Task task = new Task("name", "description");
        task.setId(-1);
        assertEquals(-1, task.getId());
        long taskId = manager.createTask(task);
        assertEquals(task.getId(), taskId);
    }

    @Test
    void updateTask() {
        Task task = new Task("name", "description");
        long taskId = manager.createTask(task);
        task.setDescription("new description");
        manager.updateTask(task);
        Task task1 = manager.getTask(task.getId());
        assertEquals("new description", task1.getDescription());
    }

    @Test
    void removeTask() {
        Task task = new Task("name", "description");
        manager.createTask(task);
        Task task1 = manager.getTask(task.getId());
        assertNotNull(task1);

        manager.removeTask(task.getId());
        task1 = manager.getTask(task.getId());
        assertNull(task1);
    }

    @Test
    void getAllEpics() {
        Epic epic1 = new Epic("name1", "description");
        Epic epic2 = new Epic("name2", "description2");
        Epic epic3 = new Epic("name3", "description3");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics);
        assertEquals(3, epics.size());
    }

    @Test
    void removeAllEpics() {
        Epic epic1 = new Epic("name1", "description");
        Epic epic2 = new Epic("name2", "description2");
        Epic epic3 = new Epic("name3", "description3");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics);
        assertEquals(3, epics.size());

        manager.removeAllEpics();
        epics = manager.getAllEpics();

        assertEquals(0, epics.size());
    }

    @Test
    void getEpic() {
        Epic epic = new Epic("name", "description");
        manager.createEpic(epic);
        assertEquals(epic, manager.getEpic(epic.getId()));
        assertNull(manager.getEpic(10));
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("name", "description");
        epic.setId(-1);
        assertEquals(-1, epic.getId());
        long taskId = manager.createEpic(epic);
        assertEquals(epic.getId(), taskId);
    }

    @Test
    void updateEpic() {
        Epic task = new Epic("name", "description");
        long taskId = manager.createEpic(task);
        task.setDescription("new description");
        manager.updateEpic(task);
        Epic task1 = manager.getEpic(task.getId());
        assertEquals("new description", task1.getDescription());
    }

    @Test
    void removeEpic() {
        Epic task = new Epic("name", "description");
        manager.createEpic(task);
        Epic task1 = manager.getEpic(task.getId());
        assertNotNull(task1);

        manager.removeEpic(task.getId());
        task1 = manager.getEpic(task.getId());
        assertNull(task1);
    }

    @Test
    void getAllSubtasks() {
        Epic epic1 = new Epic("name1", "description");
        Epic epic2 = new Epic("name2", "description2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask task1 = new Subtask("name3", "description3", epic1.getId());
        Subtask task2 = new Subtask("name4", "description4", epic1.getId());
        Subtask task3 = new Subtask("name5", "description5", epic2.getId());

        manager.createSubtask(task1);
        manager.createSubtask(task2);
        manager.createSubtask(task3);

        List<Subtask> list = manager.getAllSubtasks();

        assertNotNull(list);
        assertEquals(3, list.size());

        for (Subtask subtask : list) {
            long epicId = subtask.getEpic();
            Epic epic = manager.getEpic(epicId);
            assertNotNull(epic);
        }
    }

    @Test
    void removeAllSubtasks() {
        Epic epic1 = new Epic("name1", "description");
        Epic epic2 = new Epic("name2", "description2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask task1 = new Subtask("name3", "description3", epic1.getId());
        Subtask task2 = new Subtask("name4", "description4", epic1.getId());
        Subtask task3 = new Subtask("name5", "description5", epic2.getId());

        manager.createSubtask(task1);
        manager.createSubtask(task2);
        manager.createSubtask(task3);

        List<Subtask> list = manager.getAllSubtasks();

        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(2, epic1.getSubtasks().size());
        assertEquals(1, epic2.getSubtasks().size());

        manager.removeAllSubtasks();
        list = manager.getAllSubtasks();

        assertEquals(0, list.size());
        assertEquals(0, epic1.getSubtasks().size());
        assertEquals(0, epic2.getSubtasks().size());
    }

    @Test
    void getSubtask() {
        Epic epic1 = new Epic("name1", "description");
        manager.createEpic(epic1);
        Subtask task = new Subtask("name3", "description3", epic1.getId());
        manager.createSubtask(task);

        assertEquals(task, manager.getSubtask(task.getId()));
        assertNull(manager.getSubtask(100));

        long epicId = task.getEpic();
        Epic epic = manager.getEpic(epicId);
        assertNotNull(epic);
        assertEquals(epic1, epic);
    }

    @Test
    void createSubtask() {
        Epic epic1 = new Epic("name1", "description");
        manager.createEpic(epic1);
        Subtask task = new Subtask("name3", "description3", epic1.getId());
        task.setId(-1);
        assertEquals(-1, task.getId());
        long taskId = manager.createSubtask(task);
        assertEquals(task.getId(), taskId);

        long epicId = task.getEpic();
        Epic epic = manager.getEpic(epicId);
        assertNotNull(epic);
        assertEquals(epic1, epic);
    }

    @Test
    void updateSubtask() {
        Epic epic1 = new Epic("name1", "description");
        manager.createEpic(epic1);
        Subtask task = new Subtask("name3", "description3", epic1.getId());
        long taskId = manager.createSubtask(task);

        task.setDescription("new description");
        manager.updateSubtask(task);
        Subtask task1 = manager.getSubtask(task.getId());
        assertEquals("new description", task1.getDescription());

        long epicId = task.getEpic();
        Epic epic = manager.getEpic(epicId);
        assertNotNull(epic);
        assertEquals(epic1, epic);
    }

    @Test
    void removeSubtask() {
        Epic epic1 = new Epic("name1", "description");
        manager.createEpic(epic1);
        Subtask task = new Subtask("name3", "description3", epic1.getId());
        long taskId = manager.createSubtask(task);

        Subtask task1 = manager.getSubtask(task.getId());
        assertNotNull(task1);

        manager.removeSubtask(task.getId());
        task1 = manager.getSubtask(task.getId());
        assertNull(task1);
    }

    @Test
    void getEpicSubtasks() {
        Epic epic1 = new Epic("name1", "description");
        Epic epic2 = new Epic("name2", "description2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask task1 = new Subtask("name3", "description3", epic1.getId());
        Subtask task2 = new Subtask("name4", "description4", epic1.getId());
        Subtask task3 = new Subtask("name5", "description5", epic2.getId());

        manager.createSubtask(task1);
        manager.createSubtask(task2);
        manager.createSubtask(task3);

        List<Subtask> list = manager.getEpicSubtasks(epic1.getId());
        assertEquals(List.of(task1, task2), list);
    }

    @Test
    void getHistory() {
        Task task = new Task("name1", "description1");
        Epic epic = new Epic("name2", "description2");
        manager.createTask(task);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("name3", "description3", epic.getId());
        manager.createSubtask(subtask);

        List<Task> history = manager.getHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

        manager.getTask(task.getId());
        history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));

        manager.getEpic(epic.getId());
        history = manager.getHistory();
        assertEquals(List.of(task, epic), history);

        manager.getSubtask(subtask.getId());
        history = manager.getHistory();
        assertEquals(List.of(task, epic, subtask), history);

        manager.getTask(task.getId());
        history = manager.getHistory();
        assertEquals(List.of(epic, subtask, task), history);


        manager.removeSubtask(subtask.getId());
        history = manager.getHistory();
        assertEquals(List.of(epic, task), history);

        manager.removeEpic(epic.getId());
        history = manager.getHistory();
        assertEquals(List.of(task), history);

        manager.removeTask(task.getId());
        history = manager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void calculateStatusEpmty() {
        Epic epic = new Epic("name1", "description1");
        manager.createEpic(epic);
        // Пустой список подзадач
        assertEquals(TaskState.NEW, epic.getState());
    }

    @Test
    public void calculateStatusAllNEW() {
        Epic epic = new Epic("name1", "description1");
        manager.createEpic(epic);

        epic.setState(TaskState.DONE);

        Subtask subTask1 = new Subtask("subtask1", "info1", epic.getId());
        Subtask subTask2 = new Subtask("subtask2", "info2", epic.getId());
        Subtask subTask3 = new Subtask("subtask3", "info3", epic.getId());

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createSubtask(subTask3);

        assertEquals(TaskState.NEW, epic.getState());
    }

    @Test
    public void calculateStatusAllDONE() {
        Epic epic = new Epic("name1", "description1");
        manager.createEpic(epic);

        Subtask subTask1 = new Subtask("subtask1", "info1", epic.getId());
        Subtask subTask2 = new Subtask("subtask2", "info2", epic.getId());
        Subtask subTask3 = new Subtask("subtask3", "info3", epic.getId());

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createSubtask(subTask3);

        subTask1.setState(TaskState.DONE);
        subTask2.setState(TaskState.DONE);
        subTask3.setState(TaskState.DONE);

        manager.updateSubtask(subTask1);
        manager.updateSubtask(subTask2);
        manager.updateSubtask(subTask3);

        assertEquals(TaskState.DONE, epic.getState());
    }

    @Test
    public void calculateStatusAllNEWandDONE() {
        Epic epic = new Epic("name1", "description1");
        manager.createEpic(epic);

        Subtask subTask1 = new Subtask("subtask1", "info1", epic.getId());
        Subtask subTask2 = new Subtask("subtask2", "info2", epic.getId());
        Subtask subTask3 = new Subtask("subtask3", "info3", epic.getId());

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createSubtask(subTask3);

        subTask2.setState(TaskState.DONE);
        manager.updateSubtask(subTask2);

        assertEquals(TaskState.IN_PROGRESS, epic.getState());
    }

    @Test
    public void calculateStatusAllInPROGRESS() {
        Epic epic = new Epic("name1", "description1");
        manager.createEpic(epic);

        Subtask subTask1 = new Subtask("subtask1", "info1", epic.getId());
        Subtask subTask2 = new Subtask("subtask2", "info2", epic.getId());
        Subtask subTask3 = new Subtask("subtask3", "info3", epic.getId());

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);
        manager.createSubtask(subTask3);

        subTask1.setState(TaskState.IN_PROGRESS);
        subTask2.setState(TaskState.IN_PROGRESS);
        subTask3.setState(TaskState.IN_PROGRESS);

        manager.updateSubtask(subTask1);
        manager.updateSubtask(subTask2);
        manager.updateSubtask(subTask3);

        assertEquals(TaskState.IN_PROGRESS, epic.getState());
    }

    @Test
    void getPrioritizedTasksTest() {
        List<Task> list = manager.getPrioritizedTasks();
        assertNotNull(list);
        assertEquals(0, list.size());

        LocalDateTime time = LocalDateTime.now();
        Task task = new Task("name1", "description1");
        task.setStartTime(time.plusMinutes(1000));
        task.setDuration(100);
        Task task2 = new Task("name2", "description2");
        Epic epic = new Epic("name3", "description3");
        manager.createTask(task);
        manager.createTask(task2);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("name4", "description4", epic.getId());
        subtask.setStartTime(time);
        subtask.setDuration(200);
        manager.createSubtask(subtask);

        assertEquals(List.of(subtask, task, task2), manager.getPrioritizedTasks());
    }

    @Test
    void validateIntersectionsTest() {
        LocalDateTime time = LocalDateTime.now();
        Task task = new Task("name1", "description1");
        task.setStartTime(time.plusMinutes(1000));
        task.setDuration(100);
        Task task2 = new Task("name2", "description2");
        Epic epic = new Epic("name3", "description3");
        manager.createTask(task);
        manager.createTask(task2);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("name4", "description4", epic.getId());
        subtask.setStartTime(time);
        subtask.setDuration(200);
        manager.createSubtask(subtask);

        Task newTask = new Task("name4", "description4");
        // before task
        newTask.setDuration(100);
        newTask.setStartTime(time.minusMinutes(200));

        boolean valid = manager.validateIntersections(newTask);
        assertTrue(valid);

        // inter task
        newTask.setStartTime(time.plusMinutes(300));
        valid = manager.validateIntersections(newTask);
        assertTrue(valid);

        // after task
        newTask.setStartTime(time.plusMinutes(1200));
        valid = manager.validateIntersections(newTask);
        assertTrue(valid);

        // intersect
        newTask.setStartTime(time.minusMinutes(50));
        valid = manager.validateIntersections(newTask);
        assertFalse(valid);

        newTask.setStartTime(time.plusMinutes(1010));
        newTask.setDuration(50);
        valid = manager.validateIntersections(newTask);
        assertFalse(valid);

        newTask.setStartTime(time.plusMinutes(1090));
        newTask.setDuration(50);
        valid = manager.validateIntersections(newTask);
        assertFalse(valid);
    }
}