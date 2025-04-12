package net.notjustanna.gateway.stream;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.notjustanna.models.common.Push;
import net.notjustanna.services.FlowService;
import reactor.core.publisher.Flux;

@Singleton
@Named("flow")
public class FlowStream implements GatewayStream {
    private final Flux<Push> flux;

    public FlowStream(FlowService flowService) {
        this.flux = flowService.getFlux();
    }

    @Override
    public Flux<?> stream() {
        return this.flux;
    }
}
