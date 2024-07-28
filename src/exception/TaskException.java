package exception;

public abstract class TaskException extends Exception {

    private final Integer id;

    public TaskException(String message, Integer id) {
        super(message);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

}
