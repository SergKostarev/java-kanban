package exception;

public class NotFoundException extends TaskException {
    public NotFoundException(String message, Integer id) {
        super(message, id);
    }
}
