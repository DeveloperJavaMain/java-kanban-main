package kanban.manager;

import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {

    private final File testFile = new File("resources/test.csv");
    private final File baseFile = new File("resources/empty.csv");
    private Path src = Path.of("resources/template.csv");
    private Path dst = Path.of("resources/test.csv");
    private FileBackedTasksManager fileBackedTasksManager;


    @Override
    protected TaskManager getTaskManager() {
        fileBackedTasksManager = new FileBackedTasksManager(baseFile);
        return fileBackedTasksManager;
    }

    void restoreTest() {
        try {
            Files.copy(src, dst, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    File createTemporaryFile() {
        try {
            File tempFile = File.createTempFile("test_data", ".csv");
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void loadFromFile() {
        FileBackedTasksManager mgr = FileBackedTasksManager.loadFromFile(testFile);
        assertNotNull(mgr);
    }

    @Test
    void restore() throws IOException {
        String templateString = Files.readString(src);
        fileBackedTasksManager.removeAllTasks();
        fileBackedTasksManager.removeAllSubtasks();
        fileBackedTasksManager.removeAllEpics();

        assertEquals(0, fileBackedTasksManager.getAllTasks().size());
        assertEquals(0, fileBackedTasksManager.getAllSubtasks().size());
        assertEquals(0, fileBackedTasksManager.getAllEpics().size());

        fileBackedTasksManager.restore(templateString);

        assertEquals(2, fileBackedTasksManager.getAllTasks().size());
        assertEquals(3, fileBackedTasksManager.getAllSubtasks().size());
        assertEquals(2, fileBackedTasksManager.getAllEpics().size());
    }

    @Test
    void toStringTest() {
        Task task = new Task("name1", "description1");
        Epic epic = new Epic("name2", "description2");
        Subtask subtask = new Subtask("name3", "description3", epic.getId());
        Subtask subtask2 = new Subtask("name4", "description4", epic.getId());

        LocalDateTime time = LocalDateTime.of(2023,1,1,12,0);
        subtask2.setStartTime(time);
        subtask2.setDuration(1000);

        String taskStr = "0,TASK,\"name1\",NEW,\"description1\",,,0";
        String epicStr = "0,EPIC,\"name2\",NEW,\"description2\",,,0";
        String subtaskStr  = "0,SUBTASK,\"name3\",NEW,\"description3\",0,,0";
        String subtaskStr2 = "0,SUBTASK,\"name4\",NEW,\"description4\",0,2023-01-01T12:00:00,1000";

        assertEquals(taskStr, fileBackedTasksManager.toString(task));
        assertEquals(epicStr, fileBackedTasksManager.toString(epic));
        assertEquals(subtaskStr, fileBackedTasksManager.toString(subtask));
        assertEquals(subtaskStr2, fileBackedTasksManager.toString(subtask2));

    }

    @Test
    void deQuote() {
        String quoted = "\"quoted text\"";
        String unquoted = "quoted text";

        assertEquals(unquoted, fileBackedTasksManager.deQuote(quoted));
        assertEquals(unquoted, fileBackedTasksManager.deQuote(unquoted));
    }

    @Test
    void fromString() {
        Task task = new Task("name1", "description1");
        Epic epic = new Epic("name2", "description2");
        Subtask subtask = new Subtask("name3", "description3", epic.getId());

        LocalDateTime time = LocalDateTime.of(2023,1,1,12,0);
        subtask.setStartTime(time);
        subtask.setDuration(1000);

        String taskStr = "0,TASK,\"name1\",NEW,\"description1\",";
        String epicStr = "0,EPIC,\"name2\",NEW,\"description2\",";
        String subtaskStr = "0,SUBTASK,\"name3\",NEW,\"description3\",0,2023-01-01T12:00:00,1000";

        assertEquals(task, fileBackedTasksManager.fromString(taskStr));
        assertEquals(epic, fileBackedTasksManager.fromString(epicStr));
        assertEquals(subtask, fileBackedTasksManager.fromString(subtaskStr));
    }

    @Test
    void historyToString() {
        restoreTest();
        FileBackedTasksManager testManager = new FileBackedTasksManager(testFile);
        HistoryManager historyManager = testManager.getHistoryManager();
        String pattern = "5,3,1,2,4";
        String history = FileBackedTasksManager.historyToString(historyManager);

        assertEquals(pattern, history);
    }

    @Test
    void historyFromString() {
        String pattern = "5,3,1,2,4";
        List<Long> history = FileBackedTasksManager.historyFromString(pattern);
        assertEquals(List.of(5l, 3l, 1l, 2l, 4l), history);
    }

    @Test
    void save() throws IOException {
        String csv = Files.readString(src);
        File saveFile = createTemporaryFile();
        FileBackedTasksManager mgr = new FileBackedTasksManager(saveFile);
        mgr.restore(csv);
        mgr.save();
        FileBackedTasksManager mgr2 = new FileBackedTasksManager(saveFile);

        assertEquals(mgr.getAllTasks(), mgr2.getAllTasks());
        assertEquals(mgr.getAllSubtasks(), mgr2.getAllSubtasks());
        assertEquals(mgr.getAllEpics(), mgr2.getAllEpics());
    }

    @Test
    void saveEmpty() throws IOException {
        File saveFile = createTemporaryFile();
        FileBackedTasksManager mgr = new FileBackedTasksManager(saveFile);

        assertEquals(0, mgr.getAllTasks().size());
        assertEquals(0, mgr.getAllSubtasks().size());
        assertEquals(0, mgr.getAllEpics().size());

        mgr.save();
        FileBackedTasksManager mgr2 = new FileBackedTasksManager(saveFile);

        assertEquals(0, mgr2.getAllTasks().size());
        assertEquals(0, mgr2.getAllSubtasks().size());
        assertEquals(0, mgr2.getAllEpics().size());
    }

    @Test
    void saveEpic() throws IOException {
        File saveFile = createTemporaryFile();
        FileBackedTasksManager mgr = new FileBackedTasksManager(saveFile);

        Epic epic = new Epic("epic", "description");
        mgr.createEpic(epic);

        assertEquals(0, mgr.getAllTasks().size());
        assertEquals(0, mgr.getAllSubtasks().size());
        assertEquals(1, mgr.getAllEpics().size());

        mgr.save();
        FileBackedTasksManager mgr2 = new FileBackedTasksManager(saveFile);

        assertEquals(0, mgr2.getAllTasks().size());
        assertEquals(0, mgr2.getAllSubtasks().size());
        assertEquals(1, mgr2.getAllEpics().size());

        assertEquals(epic, mgr2.getAllEpics().get(0));
    }

    void saveWithEmptyHistory() throws IOException {
        String csv = Files.readString(src);
        File saveFile = createTemporaryFile();
        FileBackedTasksManager mgr = new FileBackedTasksManager(saveFile);
        mgr.restore(csv);

        List<Task> history = mgr.getHistory();
        assertTrue(history.size() > 0);

        for (Task task : history) {
            mgr.getHistoryManager().remove(task.getId());
        }

        history = mgr.getHistory();
        assertTrue(history.size() == 0);

        mgr.save();

        FileBackedTasksManager mgr2 = new FileBackedTasksManager(saveFile);

        assertEquals(mgr.getAllTasks(), mgr2.getAllTasks());
        assertEquals(mgr.getAllSubtasks(), mgr2.getAllSubtasks());
        assertEquals(mgr.getAllEpics(), mgr2.getAllEpics());

        history = mgr2.getHistory();
        assertTrue(history.size() == 0);
    }

}
