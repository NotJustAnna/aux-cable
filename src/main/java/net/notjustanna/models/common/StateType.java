package net.notjustanna.models.common;

import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@ReflectiveAccess
public enum StateType {
    IDLE,
    LOGGED_IN,
    CONNECTED
}
