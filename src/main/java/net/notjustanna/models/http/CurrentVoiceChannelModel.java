package net.notjustanna.models.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.notjustanna.audio.AudioSystemSendHandler;

import java.util.List;

@Serdeable
@ReflectiveAccess
public record CurrentVoiceChannelModel(
    @NonNull String id,
    @NonNull String guildId,
    @NonNull String guildName,
    @NonNull String guildIconUrl,
    @NonNull String name,
    @NonNull List<MemberModel> members,
    @Nullable AudioInputModel currentInput
) {
    public static CurrentVoiceChannelModel of(@NonNull VoiceChannel channel) {
        Guild guild = channel.getGuild();
        return new CurrentVoiceChannelModel(
            channel.getId(),
            guild.getId(),
            guild.getName(),
            guild.getIconUrl(),
            channel.getName(),
            channel.getMembers()
                .stream()
                .filter(VoiceChannelModel.notSelfUser)
                .map(MemberModel::of)
                .toList(),
            guild.getAudioManager().getSendingHandler() instanceof AudioSystemSendHandler handler
                ? AudioInputModel.of(handler.getInput())
                : null
        );
    }

}
