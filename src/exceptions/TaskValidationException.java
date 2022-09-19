package exceptions;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException(final String message) {
        super(message);
    }
}
