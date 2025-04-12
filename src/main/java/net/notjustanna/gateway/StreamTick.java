package net.notjustanna.gateway;

import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Singleton
public class StreamTick {
    private final Flux<Long> tick;

    public StreamTick() {
        this.tick = Flux.interval(Duration.ZERO, Duration.ofMillis(500)).share();
    }

    public Flux<Long> getTick() {
        return this.tick;
    }
}
