package net.notjustanna.exceptions;

import io.micronaut.core.annotation.NonNull;

public class ApplicationException extends RuntimeException {
    @NonNull
    private final ExceptionType type;

    public ApplicationException(ExceptionType type) {
        super("Application exception of type " + type.name());
        this.type = type;
    }

    public ApplicationException(ExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    public ApplicationException(ExceptionType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    @NonNull
    public ExceptionType getType() {
        return type;
    }
}
