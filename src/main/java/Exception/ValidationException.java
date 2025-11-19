package Exception;

/**
 * Exception thrown when entity validation fails.
 * This is a runtime exception that carries detailed validation error messages.
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Constructs a new ValidationException with the specified error message.
     *
     * @param message the detailed validation error message
     */
    public ValidationException(String message) {
        super(message);
    }
}
