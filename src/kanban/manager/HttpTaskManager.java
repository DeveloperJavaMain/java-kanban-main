package kanban.manager;

import kanban.kvclient.KVTaskClient;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private final String key = "TasksData";

    public HttpTaskManager(String url) {
        super(null);
        client = new KVTaskClient(url);
        load();
    }

    @Override
    protected void load() {
        String data = client.load(key);
        if(data!=null){
            restore(data);
        }
    }

    @Override
    protected void save() {
        String data = storeToString();
        client.put(key,data);
    }

    public static void main(String[] args) {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
    }
}
