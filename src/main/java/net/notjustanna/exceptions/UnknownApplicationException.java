package net.notjustanna.exceptions;

import io.micronaut.core.annotation.NonNull;

public class UnknownApplicationException extends ApplicationException {
    @NonNull
    private final String at;

    public UnknownApplicationException(String at, Throwable cause) {
        super(ExceptionType.UNKNOWN, "Unknown application exception at " + at, cause);
        this.at = at;
    }

    @NonNull
    public String getAt() {
        return at;
    }
}
