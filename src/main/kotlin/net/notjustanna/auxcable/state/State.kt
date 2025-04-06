package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.core.Observable
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.notjustanna.auxcable.models.AudioInputModel
import net.notjustanna.auxcable.state.util.HttpResponseExceptions
import net.notjustanna.auxcable.state.util.Message

sealed class State {
    companion object {
        fun start(): () -> State {
            val initialState = InitialState().stateStream
            return { initialState.blockingFirst() }
        }
    }
    abstract val type: StateType
    abstract val messageStream: Observable<Message>
    abstract val stateStream: Observable<State>
    abstract val jda: JDA?
    abstract val channel: VoiceChannel?

    open fun login(token: String, remember: Boolean): State {
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun logout(): State {
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun connect(channelId: String): State {
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun disconnect(): State {
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun stream(input: AudioInputModel?): State {
        throw HttpResponseExceptions.unsupportedAction
    }
}
