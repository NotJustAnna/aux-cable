package net.notjustanna.models.internal;

import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.notjustanna.models.http.AccountModel;

@Serdeable
@ReflectiveAccess
public record Account(String id, String name, String imageUrl, String token) {
    public AccountModel toModel() {
        return new AccountModel(id, name, imageUrl);
    }
}
