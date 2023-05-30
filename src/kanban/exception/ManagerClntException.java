package kanban.exception;

public class ManagerClntException extends RuntimeException{
    public ManagerClntException() {
    }

    public ManagerClntException(String message) {
        super(message);
    }

    public ManagerClntException(String message, Throwable cause) {
        super(message, cause);
    }
}
