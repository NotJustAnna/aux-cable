package net.notjustanna.auxcable.models

data class VoiceChannelModel(
    val id: String,
    val name: String,
    val userLimit: Int,
    val bitrate: Int,
    val position: Int,
    val canJoin: Boolean,
    val members: List<MemberModel>
)
