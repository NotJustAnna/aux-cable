package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.notjustanna.audio.discord.AuxSendHandler
import net.notjustanna.audio.system.AudioInputs
import net.notjustanna.auxcable.models.AudioInputModel
import net.notjustanna.auxcable.state.util.HttpResponseExceptions
import net.notjustanna.auxcable.state.util.Logger

class ConnectedState(
    private val logger: Logger,
    private val stateSubject: BehaviorSubject<State>,
    override val jda: JDA,
    private val channelId: String,
): State() {
    override val messageStream = logger.subject
    override val stateStream = stateSubject
    override val type = StateType.CONNECTED

    init {
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
        channel.guild.audioManager.closeAudioConnection()
        return LoggedInState(logger, stateSubject, jda)
    }

    override fun logout(): State {
        return this.disconnect().logout()
    }

    override fun stream(input: AudioInputModel?): State {
        val actualInput = input?.let { AudioInputs.all.find { it.name == input.name && it.device == input.device } }

        if (input != null && actualInput == null) {
            throw HttpResponseExceptions.noSuchAudioInput
        }

        val prev = channel.guild.audioManager.sendingHandler as? AuxSendHandler
        if (actualInput != null) {
            if (prev != null && prev.input == actualInput) {
                return this // No need to change the input if it's the same.
            }
            val aux = AuxSendHandler.open(actualInput)
            if (aux != null) {
                channel.guild.audioManager.sendingHandler = aux
            }
        } else {
            channel.guild.audioManager.sendingHandler = null
        }
        prev?.close()
        if (actualInput != null) {
            logger.info("Streaming \"${actualInput.name}\"...")
        } else {
            logger.info("Streaming disabled.")
        }

        return this
    }
}