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
import net.notjustanna.auxcable.state.util.Logger
import net.notjustanna.auxcable.state.util.RememberMe
import java.util.concurrent.CompletableFuture

class InitialState(
    private val logger: Logger = Logger(),
    private val stateSubject: BehaviorSubject<State> = BehaviorSubject.create(),
) : State() {
    override val messageStream = logger.subject
    override val stateStream = stateSubject
    override val jda: JDA? = null
    override val channel: VoiceChannel? = null
    override val type: StateType = StateType.IDLE

    init {
        stateSubject.onNext(this)
    }

    override fun login(token: String, remember: Boolean): State {
        if (token.isEmpty()) {
            throw HttpResponseExceptions.emptyToken
        }

        val actualToken = if (token.startsWith("RememberMe.id=")) {
            val account = RememberMe.getById(token.substringAfter("="))
                ?: throw HttpResponseExceptions.rememberMeFailed
            account.token
        } else token

        logger.info("Logging in to Discord...")

        val future = CompletableFuture<Unit>()
        val listener = EventListener { if (it is ReadyEvent) future.complete(Unit) }

        val jda = try {
            JDABuilder.createLight(actualToken)
                .setAudioSendFactory(NativeAudioSendFactory())
                .addEventListeners(listener)
                .build()
        } catch (e: InvalidTokenException) {
            throw HttpResponseExceptions.invalidToken
        } catch (e: Exception) {
            throw HttpResponseExceptions.unknown("LOGGING_IN_TO_DISCORD", e)
        }

        future.join()
        jda.removeEventListener(listener)

        val self = jda.selfUser
        if (remember) {
            RememberMe.save(actualToken, self.id, self.effectiveName, self.effectiveAvatarUrl)
        }

        logger.success("Logged in as \"${self.name}\"")

        return LoggedInState(logger, stateSubject, jda)
    }
}