package net.notjustanna.auxcable.models

data class GuildModel(
    val id: String,
    val name: String,
    val iconUrl: String?,
    val memberCount: Int,
    val voiceChannels: List<VoiceChannelModel>
)
