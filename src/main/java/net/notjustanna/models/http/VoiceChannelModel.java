package net.notjustanna.models.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Serdeable
@ReflectiveAccess
public record VoiceChannelModel(
    @NonNull String id,
    @NonNull String name,
    int userLimit,
    int bitrate,
    int position,
    boolean canJoin,
    @NonNull List<MemberModel> members
) {
    public static VoiceChannelModel of(VoiceChannel channel) {
        return new VoiceChannelModel(
            channel.getId(),
            channel.getName(),
            channel.getUserLimit(),
            channel.getBitrate(),
            channel.getPosition(),
            channel.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT),
            channel.getMembers()
                .stream()
                .filter(notSelfUser)
                .map(MemberModel::of)
                .sorted(Comparator.comparing(MemberModel::id))
                .toList()
        );
    }

    static final Predicate<Member> notSelfUser = member -> !member.getId().equals(member.getGuild().getSelfMember().getId());
}
