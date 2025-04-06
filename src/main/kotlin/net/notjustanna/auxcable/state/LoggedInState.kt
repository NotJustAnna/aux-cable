package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.notjustanna.auxcable.state.util.HttpResponseExceptions
import net.notjustanna.auxcable.state.util.Logger
import net.notjustanna.auxcable.state.util.RememberMe
import java.util.concurrent.TimeUnit

class LoggedInState(
    private val logger: Logger,
    private val stateSubject: BehaviorSubject<State>,
    override val jda: JDA,
): State() {
    override val messageStream = logger.subject
    override val stateStream = stateSubject
    override val type: StateType = StateType.LOGGED_IN
    override val channel: VoiceChannel? = null

    init {
        stateSubject.onNext(this)
    }

    override fun logout(): State {
        logger.info("Logging out...")
        val self = jda.selfUser
        RememberMe.update(self.id, self.effectiveName, self.effectiveAvatarUrl)
        jda.shutdown()
        if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
            jda.shutdownNow()
        }
        return InitialState(logger, stateSubject)
    }

    override fun connect(channelId: String): State {
        val channel = jda.getVoiceChannelById(channelId) ?: throw HttpResponseExceptions.noSuchChannel
        logger.info("Connecting to \"${channel.name}\"...")

        val event = Single.create { emitter ->
            jda.listenOnce(GuildVoiceUpdateEvent::class.java)
                .filter { it.channelJoined?.id == channel.id && it.member.user.id == jda.selfUser.id }
                .subscribe(emitter::onSuccess)
        }

        channel.guild.audioManager.openAudioConnection(channel)
        try {
            event.timeout(10, TimeUnit.SECONDS).blockingGet()
            logger.info("Connected!")
        } catch (e: Exception) {
            logger.error("Took too long to connect to the channel.")
        }
        return ConnectedState(logger, stateSubject, jda, channelId)
    }
}
