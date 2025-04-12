package net.notjustanna.models.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.notjustanna.audio.AudioInput;

@Serdeable
@ReflectiveAccess
public record AudioInputModel(@NonNull String name, @NonNull String device) {
    public static AudioInputModel of(AudioInput input) {
        return new AudioInputModel(input.name(), input.device());
    }
}
