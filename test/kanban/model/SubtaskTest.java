package kanban.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void getName() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals("name", task.getName());
    }

    @Test
    void setName() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals("name", task.getName());
        task.setName("new name");
        assertEquals("new name", task.getName());
    }

    @Test
    void getDescription() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals("description", task.getDescription());
    }

    @Test
    void setDescription() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals("description", task.getDescription());
        task.setDescription("new description");
        assertEquals("new description", task.getDescription());
    }

    @Test
    void getId() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals(0, task.getId());
    }

    @Test
    void setId() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals(0, task.getId());
        task.setId(10);
        assertEquals(10, task.getId());
    }

    @Test
    void getState() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals(TaskState.NEW, task.getState());
    }

    @Test
    void setState() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        task.setState(TaskState.DONE);
        assertEquals(TaskState.DONE, task.getState());
    }

    @Test
    void getEpic() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals(epic.getId(), task.getEpic());
    }

    @Test
    void setEpic() {
        Epic epic = new Epic("epic", "info");
        Subtask task = new Subtask("name", "description", epic.getId());
        assertEquals(epic.getId(), task.getEpic());
        Epic epic2 = new Epic("epic2", "info2");
        task.setEpic(epic2.getId());
        assertEquals(epic2.getId(), task.getEpic());
    }
}