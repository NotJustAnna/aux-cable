package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
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
}