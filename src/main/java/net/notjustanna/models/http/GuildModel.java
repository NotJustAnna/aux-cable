package net.notjustanna.models.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.serde.annotation.Serdeable;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Comparator;
import java.util.List;

@Serdeable
@ReflectiveAccess
public record GuildModel(
    @NonNull String id,
    @NonNull String name,
    @NonNull String iconUrl,
    int memberCount,
    @NonNull List<VoiceChannelModel> voiceChannels
) {
    public static GuildModel of(Guild guild) {
        return new GuildModel(
            guild.getId(),
            guild.getName(),
            guild.getIconUrl(),
            guild.getMemberCount(),
            guild.getVoiceChannels()
                .stream()
                .map(VoiceChannelModel::of)
                .sorted(Comparator.comparing(VoiceChannelModel::id))
                .toList()
        );
    }
}
