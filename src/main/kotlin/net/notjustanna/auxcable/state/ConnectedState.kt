package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.notjustanna.audio.discord.AuxSendHandler
import net.notjustanna.audio.system.AudioInputs
import net.notjustanna.auxcable.models.AudioInputModel
import net.notjustanna.auxcable.state.util.HttpResponseExceptions
import net.notjustanna.auxcable.state.util.Flow

class ConnectedState(
    override val flow: Flow,
    private val stateSubject: BehaviorSubject<State>,
    override val jda: JDA,
    private val channelId: String,
): State() {
    override val stateStream = stateSubject
    override val type = StateType.CONNECTED

    init {
        flow.push("state.connected.init")
        stateSubject.onNext(this)
    }

    override val channel: VoiceChannel
        get() {
            val channel = jda.getVoiceChannelById(channelId)
            if (channel == null) {
                disconnect()
                throw HttpResponseExceptions.inconsistentState
            }
            return channel
        }

    override fun disconnect(): State {
        flow.push("action.disconnect.start")
        flow.push("action.disconnect.close")
        channel.guild.audioManager.closeAudioConnection()
        flow.push("action.disconnect.success")
        return LoggedInState(flow, stateSubject, jda)
    }

    override fun logout(): State {
        return this.disconnect().logout()
    }

    override fun stream(input: AudioInputModel?): State {
        flow.push("action.stream.start")
        val actualInput = input?.let { AudioInputs.all.find { it.name == input.name && it.device == input.device } }

        if (input != null && actualInput == null) {
            throw HttpResponseExceptions.noSuchAudioInput
        }

        val prev = channel.guild.audioManager.sendingHandler as? AuxSendHandler
        if (actualInput != null) {
            if (prev != null && prev.input == actualInput) {
                return this // No need to change the input if it's the same.
            }
            flow.push("action.stream.open")
            val aux = AuxSendHandler.open(actualInput)
            flow.push("action.stream.opened")
            if (aux != null) {
                channel.guild.audioManager.sendingHandler = aux
            }
        } else {
            flow.push("action.stream.close")
            channel.guild.audioManager.sendingHandler = null
        }
        prev?.close()
        flow.push("action.stream.success")

        return this
    }
}