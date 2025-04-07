package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.notjustanna.auxcable.state.util.HttpResponseExceptions
import net.notjustanna.auxcable.state.util.Flow
import net.notjustanna.auxcable.state.util.RememberMe
import java.util.concurrent.TimeUnit

class LoggedInState(
    override val flow: Flow,
    private val stateSubject: BehaviorSubject<State>,
    override val jda: JDA,
): State() {
    override val stateStream = stateSubject
    override val type: StateType = StateType.LOGGED_IN
    override val channel: VoiceChannel? = null

    init {
        flow.push("state.logged-in.init")
        stateSubject.onNext(this)
    }

    override fun logout(): State {
        flow.push("action.logout.start")
        val self = jda.selfUser
        RememberMe.update(self.id, self.effectiveName, self.effectiveAvatarUrl)
        flow.push("action.logout.disconnect")
        jda.shutdown()
        if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
            flow.push("action.logout.force-disconnect")
            jda.shutdownNow()
        }
        flow.push("action.logout.success")
        return InitialState(flow, stateSubject)
    }

    override fun connect(channelId: String): State {
        flow.push("action.connect.start")
        val channel = jda.getVoiceChannelById(channelId) ?: throw HttpResponseExceptions.noSuchChannel

        val event = Single.create { emitter ->
            jda.listenOnce(GuildVoiceUpdateEvent::class.java)
                .filter { it.channelJoined?.id == channel.id && it.member.user.id == jda.selfUser.id }
                .subscribe(emitter::onSuccess)
        }

        channel.guild.audioManager.openAudioConnection(channel)
        try {
            event.timeout(10, TimeUnit.SECONDS).blockingGet()
            flow.push("action.connect.joined")
        } catch (e: Exception) {
            flow.push("action.connect.timeout")
            channel.guild.audioManager.closeAudioConnection()
            return this
        }

        flow.push("action.connect.success")
        return ConnectedState(flow, stateSubject, jda, channelId)
    }
}
