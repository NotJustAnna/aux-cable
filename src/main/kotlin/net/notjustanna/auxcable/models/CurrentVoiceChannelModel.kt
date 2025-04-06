package net.notjustanna.auxcable.models

data class CurrentVoiceChannelModel(
    val id: String,
    val guildId: String,
    val guildName: String,
    val guildIconUrl: String?,
    val name: String,
    val members: List<MemberModel>,
    val currentInput: AudioInputModel?,
)
