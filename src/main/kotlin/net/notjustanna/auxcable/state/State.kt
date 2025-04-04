package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

enum class StateType {
    IDLE,
    LOGGED_IN,
    CONNECTED,
}

sealed class State {
    abstract val type: StateType
    abstract val messageStream: Observable<Message>
    abstract val stateStream: Observable<State>
    abstract val jda: JDA?

    open fun login(token: String, remember: Boolean): State {
        throw IllegalStateException("Login is not supported in this state")
    }

    open fun logout(): State {
        throw IllegalStateException("Logout is not supported in this state")
    }

    open fun connect(channelId: String): State {
        throw IllegalStateException("Connect is not supported in this state")
    }

    open fun disconnect(): State {
        throw IllegalStateException("Disconnect is not supported in this state")
    }

    open fun stream(enabled: Boolean): State {
        throw IllegalStateException("Stream is not supported in this state")
    }
}

class InitialState(
    private val logger: Logger = Logger(),
    private val stateSubject: BehaviorSubject<State> = BehaviorSubject.create(),
) : State() {

    init {
        stateSubject.onNext(this)
    }

    override val messageStream = logger.subject
    override val stateStream = stateSubject
    override val jda: JDA? = null
    override val type: StateType = StateType.IDLE

    override fun login(token: String, remember: Boolean): State {
        if (token.isEmpty()) {
            throw IllegalStateException("Token cannot be empty!\n\nTip: You need to have a Discord Bot to proceed.\nVisit https://discord.com/developers/applications to create one.")
        }

        // TODO: Implement "remember me"

        logger.info("Logging in to Discord...")

        val future = CompletableFuture<Unit>()
        val listener = EventListener { if (it is ReadyEvent) future.complete(Unit) }

        val jda = try {
            JDABuilder.createLight(token)
                .addEventListeners(listener)
                .build()
        } catch (e: InvalidTokenException) {
            throw IllegalStateException("The provided token is invalid.\n\nTip: You need to have a Discord Bot to proceed.\nVisit https://discord.com/developers/applications to create one.")
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error while logging in to Discord.", e)
        }

        future.join()
        jda.removeEventListener(listener)

        logger.success("Logged in as \"${jda.selfUser.name}\"")

        return LoggedInState(logger, stateSubject, jda)
    }
}

class LoggedInState(
    private val logger: Logger,
    private val stateSubject: BehaviorSubject<State>,
    override val jda: JDA,
): State() {
    override val messageStream = logger.subject
    override val stateStream = stateSubject
    override val type: StateType = StateType.LOGGED_IN

    init {
        stateSubject.onNext(this)
    }

    override fun logout(): State {
        logger.info("Logging out...")
        jda.shutdown()
        if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
            jda.shutdownNow()
        }
        return InitialState(logger, stateSubject)
    }
}