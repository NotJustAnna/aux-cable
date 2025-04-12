package net.notjustanna.gateway.stream;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.notjustanna.audio.AudioInputs;
import net.notjustanna.gateway.StreamTick;
import net.notjustanna.models.http.AudioInputModel;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

@Singleton
@Named("audioInputs")
public class AudioInputStream implements GatewayStream {
    private final Flux<?> flux;

    public AudioInputStream(StreamTick tick) {
        // Optimized to use a single flux for multiple subscriptions.
        this.flux = tick.getTick().map(ignored -> AudioInputs.all().stream().map(AudioInputModel::of).collect(Collectors.toList()))
            .distinctUntilChanged()
            .share();
    }

    @Override
    public Flux<?> stream() {
        return this.flux;
    }
}
