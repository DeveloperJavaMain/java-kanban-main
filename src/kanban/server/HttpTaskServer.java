package kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import kanban.exception.ManagerSrvrException;
import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.Arrays;

public class HttpTaskServer implements HttpHandler {

    private HttpServer httpServer;
    private int PORT = 8080;
    private TaskManager manager;
    private String fileName = "resources/example.csv";

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this);
        httpServer.start(); // запускаем сервер
        System.out.println("Server started");

        manager = Manager.getFileBackedTasksManager(fileName);
        System.out.println("Manager created");

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    doGet(exchange);
                    return;
                case "POST":
                    doPost(exchange);
                    return;
                case "DELETE":
                    doDelete(exchange);
                    return;
                default:
                    exchange.sendResponseHeaders(404, 0);
            }
        } catch (IOException | ManagerSrvrException e) {
            e.printStackTrace();
            writeResponse(exchange, "Error on processing request", 500);
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString == null || responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes();
            if (responseCode == 200) {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
            }
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private Integer getId(String query) {
        //String query = exchange.getRequestURI().getQuery();
        String idStr = (query != null) ? Arrays.stream(query.split("&"))
                .filter(s -> s.startsWith("id="))
                .map(s -> s.substring(3))
                .findFirst().orElse(null) : null;
        if (idStr == null) return null; // нет параметра в запросе
        try {
            Integer id = Integer.valueOf(idStr);
            return id;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            ManagerSrvrException ex = new ManagerSrvrException(e.getMessage());
            throw ex;
        }
    }

    public void doGet(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        String type = (path.length > 2) ? path[2] : "";
        Integer id = null;
        String result = null;
        if (type == null || type.isEmpty()) { // URI: /tasks/
            result = gson.toJson(manager.getPrioritizedTasks());
            writeResponse(exchange, result, 200);
            return;
        }
        try {
            id = getId(exchange.getRequestURI().getQuery());
        } catch (ManagerSrvrException e) {
            writeResponse(exchange, "Incorrect ID", 400);
            return;
        }
        if (id == null) { // URI: /tasks/task, /tasks/subtask, /tasks/epic, /tasks/history
            switch (type) {
                case "task":
                    result = gson.toJson(manager.getAllTasks());
                    break;
                case "subtask":
                    result = gson.toJson(manager.getAllSubtasks());
                    break;
                case "epic":
                    result = gson.toJson(manager.getAllEpics());
                    break;
                case "history":
                    result = gson.toJson(manager.getHistory());
                    break;
                default:
                    result = null;
            }
            if (result == null) {
                writeResponse(exchange, "Incorrect request", 400);
            } else {
                System.out.println(result);
                writeResponse(exchange, result, 200);
            }
            return;
        }
        //GET by id
        switch (type) {
            case "task":
                result = gson.toJson(manager.getTask(id));
                break;
            case "subtask":
                if (path.length > 3 && "epic".equalsIgnoreCase(path[3])) {
                    Epic epic = manager.getEpic(id);
                    if (epic == null) {
                        writeResponse(exchange, "Epic not exists", 400);
                        return;
                    }
                    result = gson.toJson(manager.getEpicSubtasks(epic.getId()));
                } else {
                    result = gson.toJson(manager.getSubtask(id));
                }
                break;
            case "epic":
                result = gson.toJson(manager.getEpic(id));
                break;
            default:
                result = null;
        }
        if (result == null) {
            writeResponse(exchange, "Incorrect request", 400);
        } else {
            writeResponse(exchange, result, 200);
        }
    }

    public void doPost(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (path.length < 3) {
            throw new ManagerSrvrException("Incorrect request");
        }

        String type = path[2];
        String body = new String(exchange.getRequestBody().readAllBytes());

        switch (type) {
            case "task":
                Task task = gson.fromJson(body, Task.class);
                if (manager.getTask(task.getId()) == null) {
                    manager.createTask(task);
                } else {
                    manager.updateTask(task);
                }
                break;
            case "subtask":
                Subtask subtask = gson.fromJson(body, Subtask.class);
                if (manager.getSubtask(subtask.getId()) == null) {
                    manager.createSubtask(subtask);
                } else {
                    manager.updateSubtask(subtask);
                }
                break;
            case "epic":
                Epic epic = gson.fromJson(body, Epic.class);
                if (manager.getEpic(epic.getId()) == null) {
                    manager.createEpic(epic);
                }
                break;
        }
        writeResponse(exchange, null, 200);
        return;
    }

    public void doDelete(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (path.length < 3) {
            throw new ManagerSrvrException("Incorrect request");
        }

        String type = path[2];
        Integer id = null;

        id = getId(exchange.getRequestURI().getQuery());

        if (id == null) { // URI: /tasks/task, /tasks/subtask, /tasks/epic, /tasks/history
            switch (type) {
                case "task":
                    manager.removeAllTasks();
                    break;
                case "subtask":
                    if (path.length > 3 && "epic".equalsIgnoreCase(path[3])) {
                        writeResponse(exchange, "Incorrect request", 400);
                        return;
                    } else {
                        manager.removeAllSubtasks();
                    }
                    break;
                case "epic":
                    manager.removeAllEpics();
                    break;
            }
            writeResponse(exchange, null, 200);
            return;
        }
        //DELETE by id
        switch (type) {
            case "task":
                manager.removeTask(id);
                break;
            case "subtask":
                if (path.length > 3 && "epic".equalsIgnoreCase(path[3])) {
                    Epic epic = manager.getEpic(id);
                    if (epic == null) {
                        writeResponse(exchange, "Epic not exists", 400);
                        return;
                    }
                    for (Subtask subtask : manager.getEpicSubtasks(id)) {
                        manager.removeSubtask(subtask.getId());
                    }
                } else {
                    manager.removeSubtask(id);
                }
                break;
            case "epic":
                manager.removeEpic(id);
                break;
        }
        writeResponse(exchange, null, 200);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
    }
}
