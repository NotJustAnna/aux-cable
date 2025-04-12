package net.notjustanna.exceptions;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;

public enum ExceptionType {
    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR),
    NO_SUCH_ACCOUNT(HttpStatus.NOT_FOUND),
    INCONSISTENT_STATE(HttpStatus.INTERNAL_SERVER_ERROR),
    NO_SUCH_CHANNEL(HttpStatus.BAD_REQUEST),
    NO_SUCH_AUDIO_INPUT(HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED),
    REMEMBER_ME_FAILED(HttpStatus.BAD_REQUEST),
    REMEMBER_ME_INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_ACTION(HttpStatus.BAD_REQUEST),
    AUDIO_INPUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    NO_SUCH_STREAM(HttpStatus.NOT_FOUND);

    @NonNull
    private final HttpStatus status;

    ExceptionType(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
