package kanban.exception;

public class ManagerTimeException extends RuntimeException{
    public ManagerTimeException() {
    }

    public ManagerTimeException(String message) {
        super(message);
    }

    public ManagerTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
