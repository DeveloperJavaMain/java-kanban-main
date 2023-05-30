package kanban.exception;

public class ManagerSrvrException extends RuntimeException{
    public ManagerSrvrException() {
    }

    public ManagerSrvrException(String message) {
        super(message);
    }

    public ManagerSrvrException(String message, Throwable cause) {
        super(message, cause);
    }
}
