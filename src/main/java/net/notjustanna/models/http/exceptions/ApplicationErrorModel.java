package net.notjustanna.models.http.exceptions;


import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@ReflectiveAccess
public record ApplicationErrorModel(@NonNull String errorType) {
}
