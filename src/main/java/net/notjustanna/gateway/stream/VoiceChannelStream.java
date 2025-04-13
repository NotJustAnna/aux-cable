package net.notjustanna.gateway.stream;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.gateway.StreamTick;
import net.notjustanna.models.http.CurrentVoiceChannelModel;
import net.notjustanna.services.StateService;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
@Named("currentVoiceChannel")
public class VoiceChannelStream implements GatewayStream {
    private final Flux<?> flux;
    private final Flux<Long> tick;
    private final StateService stateService;

    public VoiceChannelStream(StreamTick tick, StateService stateService) {
        this.tick = tick.getTick();
        this.stateService = stateService;
        // Optimized to use a single flux for multiple subscriptions.
        this.flux = Flux.create(this::start).distinctUntilChanged().replay(1).refCount();
    }

    @Override
    public Flux<?> stream() {
        return this.flux;
    }

    private void start(FluxSink<CurrentVoiceChannelModel> emitter) {
        // Gets called on first subscriber.
        VoiceChannel channel = stateService.current().getChannel();
        if (channel == null) {
            emitter.error(Exceptions.unsupportedAction());
        }
        String channelId = channel.getId();
        JDA jda = channel.getJDA();

        Task<?> shutdownTask = jda.listenOnce(ShutdownEvent.class).subscribe(e -> emitter.complete());
        Disposable tickStream = tick.subscribe(tick -> {
            // Check if the channel is still valid
            VoiceChannel currentChannel = jda.getVoiceChannelById(channelId);
            if (currentChannel == null) {
                emitter.complete();
                return;
            }
            emitter.next(CurrentVoiceChannelModel.of(currentChannel));
        });

        emitter.onDispose(() -> {
            shutdownTask.cancel();
            tickStream.dispose();
        });
    }
}
