package kanban.manager;

import kanban.exception.ManagerLoadException;
import kanban.exception.ManagerSaveException;
import kanban.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File storeFile;
    private DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;

    // default constructor
    public FileBackedTasksManager() {
        String fileName = "resources/taskmanager.csv";
        storeFile = new File(fileName);
        load();
    }

    // constructor with backed file
    public FileBackedTasksManager(File storeFile) {
        this.storeFile = storeFile;
        if(storeFile!=null) {
            load();
        }
    }

    // create FileBackedTasksManager
    public static FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file);
    }

    // load stored tasks
    protected void load() {
        if (!storeFile.exists()) {
            return;
        }
        try {
            String line = Files.readString(Path.of(storeFile.toURI()));
            restore(line);
        } catch (IOException e) {
            throw new ManagerLoadException("Error on load data", e);
        }
    }

    // restore tasks from String
    protected void restore(String str) {
        String[] lines = str.split("\n");
        long maxId = -1;
        int num = 1;
        // read tasks
        for (; num < lines.length && !lines[num].isBlank(); num++) {
            Task task = fromString(lines[num]);
            maxId = Math.max(maxId, task.getId());
            if (task instanceof Epic) {
                createEpic((Epic) task, task.getId());
            } else if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                createSubtask(subtask, subtask.getId());
            } else {
                createTask(task, task.getId());
            }
        }
        setCounter(maxId + 1);
        // read history
        num++; // skip blank line
        if (num < lines.length && !lines[num].isBlank()) {
            List<Long> historyIds = historyFromString(lines[num]);
            HistoryManager historyManager = getHistoryManager();
            for (Long id : historyIds) {
                Task task = getById(id);
                if (task != null) {
                    historyManager.add(task);
                }
            }
        }
    }

    // Convert task to String
    protected String toString(Task task) {
        TaskType type = TaskType.TASK;
        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK;
        }
        // id,type,name,status,description,epic
        String line = String.format("%d,%s,\"%s\",%s,\"%s\",%s,%s,%d",
                task.getId(), type, task.getName(), task.getState(), task.getDescription(),
                (type == TaskType.SUBTASK) ? ((Subtask) task).getEpic() : "",
                (task.getStartTime()!=null) ? task.getStartTime().format(fmt) : "",
                task.getDuration()
                );
        return line;
    }

    // remove quotas
    protected String deQuote(String line) {
        return (line != null && line.startsWith("\"") && line.endsWith("\""))
                ? line.substring(1, line.length() - 1)
                : line;
    }

    // restore Task from String
    protected Task fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            return null;
        }
        long id = Long.parseLong(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = deQuote(parts[2]);
        TaskState state = TaskState.valueOf(parts[3]);
        String desc = deQuote(parts[4]);
        long epicId = (parts.length > 5 && !parts[5].isBlank()) ? Long.parseLong(parts[5]) : -1;
        LocalDateTime startTime = (parts.length>6 && !parts[6].isBlank()) ? LocalDateTime.parse(parts[6],fmt) : null;
        int duration = (parts.length>7 && !parts[7].isBlank()) ? Integer.parseInt(parts[7]) : 0;

        Task result;
        switch (type) {
            case SUBTASK:
                Subtask subtask = new Subtask();
                subtask.setEpic(epicId);
                result = subtask;
                break;
            case EPIC:
                result = new Epic();
                break;
            default:
                result = new Task();
        }

        result.setId(id);
        result.setName(name);
        result.setDescription(desc);
        result.setState(state);
        result.setStartTime(startTime);
        result.setDuration(duration);

        return result;
    }

    // store history to String
    protected static String historyToString(HistoryManager manager) {
        String result = manager.getHistory().stream()
                .map(t -> String.valueOf(t.getId()))
                .collect(Collectors.joining(","));
        return result;
    }

    // restore history Ids from String
    protected static List<Long> historyFromString(String value) {
        List<Long> result = Arrays.stream(value.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return result;
    }

    // write Task to
    private void writeTask(Task task, Writer fw) {
        try {
            fw.write(toString(task));
            fw.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected String storeToString() {
        StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic,startTime,duration\n");
            getAllTasks().forEach(t -> sb.append(toString(t)).append("\n"));
            getAllEpics().forEach(t -> sb.append(toString(t)).append("\n"));
            getAllSubtasks().forEach(t -> sb.append(toString(t)).append("\n"));
            sb.append("\n");
            sb.append(historyToString(getHistoryManager()));
            sb.append("\n");
        return sb.toString();
    }
    // save data to File
    protected void save() {
        try (FileWriter fw = new FileWriter(storeFile)) {
            fw.write(storeToString());
/*
            fw.write("id,type,name,status,description,epic,startTime,duration\n");
            getAllTasks().forEach(t -> writeTask(t, fw));
            getAllEpics().forEach(t -> writeTask(t, fw));
            getAllSubtasks().forEach(t -> writeTask(t, fw));
            fw.write("\n");
            fw.write(historyToString(getHistoryManager()));
            fw.write("\n");
 */
        } catch (IOException e) {
            throw new ManagerSaveException("Error on save data", e);
        }
    }


    // remove tasks
    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    // remove subtasks
    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    // remove epics
    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    // get Task by Id
    @Override
    public Task getTask(long id) {
        Task res = super.getTask(id);
        save();
        return res;
    }

    // get Subtask by Id
    public Subtask getSubtask(long id) {
        Subtask res = super.getSubtask(id);
        save();
        return res;
    }

    // get Epic by Id
    public Epic getEpic(long id) {
        Epic res = super.getEpic(id);
        save();
        return res;
    }

    // Create new Task
    @Override
    public long createTask(Task task) {
        long id = super.createTask(task);
        save();
        return id;
    }

    // Create new Subtask
    @Override
    public long createSubtask(Subtask subtask) {
        long id = super.createSubtask(subtask);
        save();
        return id;
    }

    // Create new Epic
    @Override
    public long createEpic(Epic epic) {
        long id = super.createEpic(epic);
        save();
        return id;
    }

    // update Task
    @Override
    public long updateTask(Task task) {
        long id = super.updateTask(task);
        save();
        return id;
    }

    // update Epic
    @Override
    public long updateEpic(Epic epic) {
        long id = super.updateEpic(epic);
        save();
        return id;
    }

    // update Subtask
    @Override
    public long updateSubtask(Subtask subtask) {
        long id = super.updateSubtask(subtask);
        save();
        return id;
    }

    // remove Task by Id
    @Override
    public boolean removeTask(long id) {
        boolean res = super.removeTask(id);
        save();
        return res;
    }

    // remove Subtask by Id
    @Override
    public boolean removeSubtask(long id) {
        boolean res = super.removeSubtask(id);
        save();
        return res;
    }

    // remove Epic by Id
    @Override
    public boolean removeEpic(long id) {
        boolean res = super.removeEpic(id);
        save();
        return res;
    }

    public static void main(String[] args) {
        File file = new File("resources/example.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();

        manager.setCounter(0);

        // создайте несколько задач разного типа

        // 2 задачи
        Task task1 = new Task("task 1", "пример простой задачи 1");
        Task task2 = new Task("task 2", "пример простой задачи 2");
        long taskId1 = manager.createTask(task1);
        long taskId2 = manager.createTask(task2);

        // один эпик с 2 подзадачами
        Epic epic1 = new Epic("epic 1", "пример эпика 1");
        long epicId1 = manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask 1", "пример подзадачи 1", epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "пример подзадачи 2", epicId1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        long subtaskId1 = subtask1.getId();
        long subtaskId2 = subtask2.getId();

        // эпик с 1 подзадачей
        Epic epic2 = new Epic("epic 2", "пример эпика 2");
        long epicId2 = manager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask 3", "пример подзадачи 3", epicId2);
        manager.createSubtask(subtask3);
        long subtaskId3 = subtask3.getId();

        // Запросите некоторые из них, чтобы заполнилась история просмотра
        manager.getEpic(epicId2);
        manager.getSubtask(subtaskId1);
        manager.getTask(taskId2);
        manager.getEpic(epicId1);
        manager.getSubtask(subtaskId2);

        // Создайте новый FileBackedTasksManager менеджер из этого же файла
        FileBackedTasksManager managerNew = new FileBackedTasksManager(file);

        // Проверьте, что история просмотра восстановилась верно и все задачи,
        // эпики, подзадачи, которые были в старом, есть в новом менеджере
        List<Task> tasksSrc = manager.getAllTasks();
        List<Task> tasksNew = managerNew.getAllTasks();

        System.out.println("Tasks :\t" + Objects.equals(tasksSrc, tasksNew));

        List<Subtask> subtasksSrc = manager.getAllSubtasks();
        List<Subtask> subtasksNew = managerNew.getAllSubtasks();

        System.out.println("Subtasks :\t" + Objects.equals(subtasksSrc, subtasksNew));

        List<Epic> epicsSrc = manager.getAllEpics();
        List<Epic> epicsNew = managerNew.getAllEpics();

        System.out.println("Epics :\t" + Objects.equals(epicsSrc, epicsNew));

        List<Task> historySrc = manager.getHistory();
        List<Task> historyNew = managerNew.getHistory();

        System.out.println("History :\t" + Objects.equals(historySrc, historyNew));

    }
}
