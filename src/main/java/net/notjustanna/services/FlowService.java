package net.notjustanna.services;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import net.notjustanna.models.common.Push;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Singleton
public class FlowService {
    private final Sinks.Many<Push> sink = Sinks.many().multicast().onBackpressureBuffer();

    public Flux<Push> getFlux() {
        return sink.asFlux();
    }

    public void push(String id) {
        push(id, null);
    }

    public void push(@NonNull String id, @Nullable Object extra) {
        if (sink.currentSubscriberCount() == 0) {
            // No subscribers, so we don't need to push anything
            return;
        }
        sink.emitNext(new Push(id, extra), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
