package exception;

public class UpdateException extends TaskException {
    public UpdateException(String message, Integer id) {
        super(message, id);
    }
}
