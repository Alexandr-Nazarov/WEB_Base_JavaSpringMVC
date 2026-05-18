package org.example.frontend.exception;



import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> errorMessages;

    public ValidationException(String message, List<String> errorMessages) {
        super(message);
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
