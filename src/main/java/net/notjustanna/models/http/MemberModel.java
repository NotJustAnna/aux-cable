package net.notjustanna.models.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.dv8tion.jda.api.entities.Member;

@Serdeable
@ReflectiveAccess
public record MemberModel(@NonNull String id, @NonNull String name, @Nullable String avatarUrl) {
    public static MemberModel of(Member member) {
        return new MemberModel(member.getId(), member.getEffectiveName(), member.getAvatarUrl());
    }
}
