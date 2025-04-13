package net.notjustanna.gateway.stream;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.utils.concurrent.Task;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.gateway.StreamTick;
import net.notjustanna.models.http.GuildModel;
import net.notjustanna.services.StateService;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;

@Singleton
@Named("guilds")
public class GuildStream implements GatewayStream {
    private final Flux<?> flux;
    private final Flux<Long> tick;
    private final StateService stateService;

    public GuildStream(StreamTick tick, StateService stateService) {
        this.tick = tick.getTick();
        this.stateService = stateService;
        // Optimized to use a single flux for multiple subscriptions.
        this.flux = Flux.create(this::start).distinctUntilChanged().replay(1).refCount();
    }

    @Override
    public Flux<?> stream() {
        return this.flux;
    }

    private void start(FluxSink<List<GuildModel>> emitter) {
        // Gets called on first subscriber.
        JDA jda = stateService.current().getJDA();
        if (jda == null) {
            emitter.error(Exceptions.unsupportedAction());
        }

        Task<?> shutdownTask = jda.listenOnce(ShutdownEvent.class).subscribe(e -> emitter.complete());
        Disposable tickStream = tick.subscribe(tick -> emitter.next(
            jda.getGuilds().stream().map(GuildModel::of).toList()
        ));

        emitter.onDispose(() -> {
            shutdownTask.cancel();
            tickStream.dispose();
        });
    }
}
