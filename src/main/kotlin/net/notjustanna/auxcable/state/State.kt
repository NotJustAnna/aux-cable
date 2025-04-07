package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.core.Observable
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.notjustanna.auxcable.models.AudioInputModel
import net.notjustanna.auxcable.state.util.Flow
import net.notjustanna.auxcable.state.util.HttpResponseExceptions

sealed class State {
    companion object {
        fun start(): () -> State {
            val initialState = InitialState().stateStream
            return { initialState.blockingFirst() }
        }
    }
    abstract val type: StateType
    abstract val flow: Flow
    abstract val stateStream: Observable<State>
    abstract val jda: JDA?
    abstract val channel: VoiceChannel?

    open fun login(token: String, remember: Boolean): State {
        flow.push("action.login.unsupported")
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun logout(): State {
        flow.push("action.logout.unsupported")
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun connect(channelId: String): State {
        flow.push("action.connect.unsupported")
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun disconnect(): State {
        flow.push("action.disconnect.unsupported")
        throw HttpResponseExceptions.unsupportedAction
    }

    open fun stream(input: AudioInputModel?): State {
        flow.push("action.stream.unsupported")
        throw HttpResponseExceptions.unsupportedAction
    }
}
