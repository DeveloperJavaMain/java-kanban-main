package kanban.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void getName() {
        Epic epic = new Epic("name", "description");
        assertEquals("name", epic.getName());
    }

    @Test
    void setName() {
        Epic epic = new Epic("name", "description");
        assertEquals("name", epic.getName());
        epic.setName("new name");
        assertEquals("new name", epic.getName());
    }

    @Test
    void getDescription() {
        Epic epic = new Epic("name", "description");
        assertEquals("description", epic.getDescription());
    }

    @Test
    void setDescription() {
        Epic epic = new Epic("name", "description");
        assertEquals("description", epic.getDescription());
        epic.setDescription("new description");
        assertEquals("new description", epic.getDescription());
    }

    @Test
    void getId() {
        Epic epic = new Epic("name", "description");
        assertEquals(0, epic.getId());
    }

    @Test
    void setId() {
        Epic epic = new Epic("name", "description");
        epic.setId(10);
        assertEquals(10, epic.getId());
    }

    @Test
    void getState() {
        Epic epic = new Epic("name", "description");
        assertEquals(TaskState.NEW, epic.getState());
    }

    @Test
    void setState() {
        Epic epic = new Epic("name", "description");
        epic.setState(TaskState.DONE);
        // setState не меняет состояние эпика
        assertEquals(TaskState.NEW, epic.getState());
    }

    @Test
    void getSubtaskIds() {
        Epic epic = new Epic("name", "description");
        assertNotNull(epic.getSubtasks());
        assertEquals(0, epic.getSubtasks().size());
    }

    @Test
    void equals() {
        Epic epic1 = new Epic("name1", "description1");
        Epic epic2 = new Epic("name2", "description2");

        Subtask subtask1 = new Subtask("subtask", "info");
        epic1.getSubtasks().add(subtask1);

        assertFalse(epic1.equals(epic2));
        epic2.setName("name1");
        assertFalse(epic1.equals(epic2));
        epic2.setDescription("description1");
        assertFalse(epic1.equals(epic2));
        epic2.getSubtasks().add(subtask1);
        assertTrue(epic1.equals(epic2));
    }

    @Test
    void setStartTimeTest() {
        Epic epic = new Epic("name", "description");
        assertNull(epic.getStartTime());

        LocalDateTime time = LocalDateTime.now();
        epic.setStartTime(time);

        assertNull(epic.getStartTime());
    }

    @Test
    void getStartTimeTest() {
        Epic epic = new Epic("name", "description");
        assertNull(epic.getStartTime());

        Subtask subtask1 = new Subtask("subtask1", "info1", epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "info2", epic.getId());
        Subtask subtask3 = new Subtask("subtask3", "info3", epic.getId());

        LocalDateTime time = LocalDateTime.now();

        subtask1.setId(1);
        subtask1.setDuration(100);
        subtask1.setStartTime(time.plusMinutes(900));
        subtask2.setId(2);
        subtask2.setDuration(200);
        subtask2.setStartTime(time.plusMinutes(600));
        subtask3.setId(3);
        subtask3.setDuration(300);
        subtask3.setStartTime(time);

        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);
        epic.getSubtasks().add(subtask3);

        assertEquals(time, epic.getStartTime());
    }

    @Test
    void setDurationTest() {
        Epic epic = new Epic("name", "description");
        assertEquals(0, epic.getDuration());

        // setDuration не меняет duration
        epic.setDuration(100);
        assertEquals(0, epic.getDuration());
    }

    @Test
    void getDurationTest() {
        Epic epic = new Epic("name", "description");
        assertEquals(0, epic.getDuration());

        Subtask subtask1 = new Subtask("subtask1", "info1", epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "info2", epic.getId());
        Subtask subtask3 = new Subtask("subtask3", "info3", epic.getId());

        subtask1.setId(1);
        subtask1.setDuration(100);
        subtask2.setId(2);
        subtask2.setDuration(200);
        subtask3.setId(3);
        subtask3.setDuration(300);

        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);
        epic.getSubtasks().add(subtask3);

        int val = epic.getDuration();
        assertEquals(600, epic.getDuration());
    }

    @Test
    void getEndTimeTest() {
        Epic epic = new Epic("name", "description");
        assertEquals(0, epic.getDuration());

        Subtask subtask1 = new Subtask("subtask1", "info1", epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "info2", epic.getId());
        Subtask subtask3 = new Subtask("subtask3", "info3", epic.getId());

        LocalDateTime time = LocalDateTime.now();

        subtask1.setId(1);
        subtask1.setDuration(100);
        subtask1.setStartTime(time.plusMinutes(900));
        subtask2.setId(2);
        subtask2.setDuration(200);
        subtask2.setStartTime(time.plusMinutes(600));
        subtask3.setId(3);
        subtask3.setDuration(300);
        subtask3.setStartTime(time);

        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);
        epic.getSubtasks().add(subtask3);

        LocalDateTime endDate = epic.getEndTime();
        assertEquals(time.plusMinutes(1000), epic.getEndTime());
    }

}