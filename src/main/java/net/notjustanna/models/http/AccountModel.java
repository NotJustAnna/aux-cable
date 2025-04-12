package net.notjustanna.models.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.dv8tion.jda.api.entities.SelfUser;
import net.notjustanna.models.internal.Account;

@Serdeable
@ReflectiveAccess
public record AccountModel(@NonNull String id, @NonNull String name, @Nullable String imageUrl) {
    public static AccountModel of(Account account) {
        return new AccountModel(account.id(), account.name(), account.imageUrl());
    }

    public static AccountModel ofSelfUser(SelfUser user) {
        return new AccountModel(user.getId(), user.getEffectiveName(), user.getAvatarUrl());
    }
}
