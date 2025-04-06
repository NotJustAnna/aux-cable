package net.notjustanna.auxcable.models

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.SelfUser
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.notjustanna.audio.discord.AuxSendHandler
import net.notjustanna.audio.system.AudioInput
import net.notjustanna.auxcable.state.State
import net.notjustanna.auxcable.state.util.Account

object Model {
    fun convert(state: State): StateModel {
        return StateModel(
            state.type,
            state.jda?.selfUser?.let(::convert)
        )
    }

    fun convert(guild: Guild): GuildModel {
        return GuildModel(
            id = guild.id,
            name = guild.name,
            iconUrl = guild.iconUrl,
            memberCount = guild.memberCount,
            voiceChannels = guild.voiceChannels.map(::convert).sortedBy { it.id },
        )
    }

    fun convert(voiceChannel: VoiceChannel) : VoiceChannelModel {
        return VoiceChannelModel(
            id = voiceChannel.id,
            name = voiceChannel.name,
            userLimit = voiceChannel.userLimit,
            bitrate = voiceChannel.bitrate,
            position = voiceChannel.position,
            canJoin = voiceChannel.guild.selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT),
            members = voiceChannel.members
                .filter { it.id != it.guild.selfMember.id }
                .map(::convert).sortedBy { it.id },
        )
    }

    fun convert(it: Member): MemberModel {
        return MemberModel(
            id = it.id,
            name = it.effectiveName,
            avatarUrl = it.effectiveAvatarUrl,
        )
    }

    fun convert(it: SelfUser): AccountModel {
        return AccountModel(
            it.id, it.effectiveName, it.effectiveAvatarUrl
        )
    }

    fun convert(it: Account): AccountModel {
        return AccountModel(
            it.id, it.name, it.imageUrl
        )
    }

    fun convert(it: AudioInput): AudioInputModel {
        return AudioInputModel(
            name = it.name,
            device = it.device,
        )
    }

    fun convertCurrent(channel: VoiceChannel): CurrentVoiceChannelModel {
        return CurrentVoiceChannelModel(
            id = channel.id,
            guildId = channel.guild.id,
            guildName = channel.guild.name,
            guildIconUrl = channel.guild.iconUrl,
            name = channel.name,
            members = channel.members
                .filter { it.id != it.guild.selfMember.id }
                .map(::convert).sortedBy { it.id },
            currentInput = (channel.guild.audioManager.sendingHandler as? AuxSendHandler)?.input?.let(::convert),
        )
    }
}