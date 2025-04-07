package net.notjustanna.auxcable.state

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.hooks.EventListener
import net.notjustanna.auxcable.state.util.HttpResponseExceptions
import net.notjustanna.auxcable.state.util.Flow
import net.notjustanna.auxcable.state.util.RememberMe
import java.util.concurrent.CompletableFuture

class InitialState(
    override val flow: Flow = Flow(),
    private val stateSubject: BehaviorSubject<State> = BehaviorSubject.create(),
) : State() {
    override val stateStream = stateSubject
    override val jda: JDA? = null
    override val channel: VoiceChannel? = null
    override val type: StateType = StateType.IDLE

    init {
        flow.push("state.initial.init")
        stateSubject.onNext(this)
    }

    override fun login(token: String, remember: Boolean): State {
        flow.push("action.login.start")
        if (token.isEmpty()) {
            throw HttpResponseExceptions.emptyToken
        }

        val actualToken = if (token.startsWith("RememberMe.id=")) {
            val account = RememberMe.getById(token.substringAfter("="))
                ?: throw HttpResponseExceptions.rememberMeFailed
            account.token
        } else token

        val future = CompletableFuture<Unit>()
        val listener = EventListener { if (it is ReadyEvent) future.complete(Unit) }

        flow.push("action.login.connecting")
        val jda = try {
            JDABuilder.createLight(actualToken)
                .setAudioSendFactory(NativeAudioSendFactory())
                .addEventListeners(listener)
                .build()
        } catch (e: InvalidTokenException) {
            flow.push("action.login.invalid-token")
            throw (
                if (token.startsWith("RememberMe.id=")) HttpResponseExceptions.rememberMeInvalidToken
                else HttpResponseExceptions.invalidToken
            )
        } catch (e: Exception) {
            flow.push("action.login.unknown-error")
            throw HttpResponseExceptions.unknown("LOGGING_IN_TO_DISCORD", e)
        }

        future.join()
        flow.push("action.login.connected")
        jda.removeEventListener(listener)

        val self = jda.selfUser
        if (remember) {
            RememberMe.save(actualToken, self.id, self.effectiveName, self.effectiveAvatarUrl)
        }

        flow.push("action.login.success")
        return LoggedInState(flow, stateSubject, jda)
    }
}