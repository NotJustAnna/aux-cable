package net.notjustanna.models.http.input;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.notjustanna.models.http.AudioInputModel;

@Serdeable
@ReflectiveAccess
public record StreamInput(@Nullable AudioInputModel input) {
}
