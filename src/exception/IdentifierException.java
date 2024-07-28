package exception;

public class IdentifierException extends TaskException {
    public IdentifierException(String message, Integer id) {
        super(message, id);
    }
}
