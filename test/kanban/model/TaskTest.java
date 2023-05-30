package kanban.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void getName() {
        Task task = new Task("name", "description");
        assertEquals("name", task.getName());
    }

    @Test
    void setName() {
        Task task = new Task("name", "description");
        assertEquals("name", task.getName());
        task.setName("new name");
        assertEquals("new name", task.getName());
    }

    @Test
    void getDescription() {
        Task task = new Task("name", "description");
        assertEquals("description", task.getDescription());
    }

    @Test
    void setDescription() {
        Task task = new Task("name", "description");
        assertEquals("description", task.getDescription());
        task.setDescription("new description");
        assertEquals("new description", task.getDescription());
    }

    @Test
    void getId() {
        Task task = new Task("name", "description");
        assertEquals(0, task.getId());
    }

    @Test
    void setId() {
        Task task = new Task("name", "description");
        task.setId(10);
        assertEquals(10, task.getId());
    }

    @Test
    void getState() {
        Task task = new Task("name", "description");
        assertEquals(TaskState.NEW, task.getState());
    }

    @Test
    void setState() {
        Task task = new Task("name", "description");
        task.setState(TaskState.DONE);
        assertEquals(TaskState.DONE, task.getState());
    }

    @Test
    void durationTest() {
        Task task = new Task("name", "description");
        assertEquals(0, task.getDuration());
        task.setDuration(100);
        assertEquals(100, task.getDuration());
    }

    @Test
    void startTimeTest() {
        Task task = new Task("name", "description");
        assertNull(task.getStartTime());
        LocalDateTime startTime = LocalDateTime.now();
        task.setStartTime(startTime);
        assertEquals(startTime, task.getStartTime());
    }

    @Test
    void endTimeTest() {
        Task task = new Task("name", "description");
        assertNull(task.getEndTime());
        LocalDateTime startTime = LocalDateTime.now();
        task.setStartTime(startTime);
        task.setDuration(100);
        assertEquals(startTime.plusMinutes(100), task.getEndTime());
    }

}