package net.notjustanna.auxcable.api

import com.linecorp.armeria.common.websocket.WebSocket
import com.linecorp.armeria.common.websocket.WebSocketFrameType
import com.linecorp.armeria.internal.common.JacksonUtil
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.websocket.WebSocketServiceHandler
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.notjustanna.audio.system.AudioInputs
import net.notjustanna.auxcable.api.gateway.GatewayException
import net.notjustanna.auxcable.api.gateway.SubscriptionEvent
import net.notjustanna.auxcable.api.gateway.SubscriptionRequest
import net.notjustanna.auxcable.models.*
import net.notjustanna.auxcable.state.State
import net.notjustanna.auxcable.state.util.Flow
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class GatewayService(private val state: () -> State) : WebSocketServiceHandler {
    companion object {
        private val mapper = JacksonUtil.newDefaultObjectMapper()
        private val logger = LoggerFactory.getLogger(GatewayService::class.java)
    }

    private val subscriptions = mapOf<String, () -> Observable<out Any>>(
        "currentState" to ::stateStream,
        "flow" to ::pushStream,
        "guilds" to ::guildStream,
        "currentVoiceChannel" to ::voiceChannelStream,
        "audioInputs" to ::audioInputStream,
    )

    override fun handle(ctx: ServiceRequestContext, req: WebSocket): WebSocket {
        val res = WebSocket.streaming()
        val streams = mutableMapOf<String, Disposable>()

        fun send(obj: Any) {
            res.write(mapper.writeValueAsString(obj))
        }

        Flowable.fromPublisher(req)
            .filter { it.type() == WebSocketFrameType.TEXT }
            .map { it.text() }
            .subscribe ({ frame ->
                try {
                    val (type, enabled) = try {
                        mapper.readValue(frame, SubscriptionRequest::class.java)
                    } catch (e: Exception) {
                        throw GatewayException("INVALID_COMMAND")
                    }

                    if (enabled) {
                        if (type in streams.keys) {
                            streams.remove(type)?.dispose()
                        }

                        val function = subscriptions[type] ?: throw GatewayException("INVALID_SUBSCRIPTION")
                        streams[type] = function().subscribe(
                            { send(SubscriptionEvent(type, it)) },
                            {
                                if (it is GatewayException) {
                                    send(SubscriptionEvent("error", it.type))
                                } else {
                                    it.printStackTrace()
                                }
                            },
                            {
                                streams.remove(type)?.dispose()
                            }
                        )
                    } else {
                        streams.remove(type)?.dispose()
                    }
                } catch (e: GatewayException) {
                    send(SubscriptionEvent("error", e.type))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                // Ignore errors, we don't care about them.
            }, {
                streams.values.forEach { it.dispose() }
                streams.clear()
            })
        return res
    }

    fun stateStream(): Observable<StateModel> {
        return state().stateStream.map(Model::convert)
    }

    fun pushStream(): Observable<Flow.Push> {
        return state().flow.subject
    }

    fun guildStream(): Observable<List<GuildModel>> {
        val jda = state().jda ?: throw GatewayException("UNSUPPORTED_ACTION")

        return Observable.create { emitter ->
            val listener = EventListener {
                if (it is ShutdownEvent) {
                    emitter.onComplete()
                    // No need to clean up the listener, JDA is shutting down and this Flowable will be disposed.
                }
            }
            jda.addEventListener(listener)

            var latest = jda.guilds.map(Model::convert).sortedBy { it.id }

            emitter.setDisposable(
                Observable.interval(500, TimeUnit.MILLISECONDS).subscribe {
                    // I've learned it's impossible to check for these changes by listening to all the relevant events.
                    // It's way easier to just check the guilds every 500ms and emit the new list if it changed.
                    val current = jda.guilds.map(Model::convert).sortedBy { it.id }
                    if (latest != current) {
                        latest = current
                        emitter.onNext(current)
                    }
                }
            )

            emitter.onNext(latest)
        }
    }

    fun voiceChannelStream(): Observable<CurrentVoiceChannelModel> {
        val channel = state().channel ?: throw GatewayException("UNSUPPORTED_ACTION")
        val jda = channel.jda
        val channelId = channel.id

        return Observable.create { emitter ->
            val listener = EventListener {
                if (it is ShutdownEvent) {
                    emitter.onComplete()
                    // No need to clean up the listener, JDA is shutting down and this Flowable will be disposed.
                }
            }
            jda.addEventListener(listener)

            var latest = Model.convertCurrent(channel)

            emitter.setDisposable(
                Observable.interval(500, TimeUnit.MILLISECONDS).subscribe {
                    val updated = jda.getVoiceChannelById(channelId)
                    if (updated == null) {
                        emitter.onComplete()
                        return@subscribe
                    }
                    val current = Model.convertCurrent(updated)
                    if (latest != current) {
                        latest = current
                        emitter.onNext(current)
                    }
                }
            )

            emitter.onNext(latest)
        }
    }

    fun audioInputStream(): Observable<List<AudioInputModel>> {
        return Observable.create { emitter ->
            var latest = AudioInputs.all.map(Model::convert).sortedBy { "${it.name}<-${it.device}" }
            emitter.setDisposable(
                Observable.interval(500, TimeUnit.MILLISECONDS).subscribe {
                    // Once again, something which is impossible to listen to.
                    val current = try {
                        AudioInputs.all.map(Model::convert).sortedBy { "${it.name}<-${it.device}" }
                    } catch (e: Exception) {
                        logger.error("Failed to load audio inputs", e)
                        return@subscribe
                    }
                    if (latest != current) {
                        latest = current
                        emitter.onNext(current)
                    }
                }
            )
            emitter.onNext(latest)
        }
    }
}