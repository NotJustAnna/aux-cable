package net.notjustanna.gateway.stream;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import net.notjustanna.models.http.StateModel;
import net.notjustanna.services.StateService;
import reactor.core.publisher.Flux;

@Singleton
@Named("currentState")
public class ApplicationStateStream implements GatewayStream {
    private final Flux<StateModel> flux;

    public ApplicationStateStream(StateService stateService) {
        this.flux = stateService.getFlux().map(StateModel::of).replay(1).refCount();
    }

    @Override
    public Flux<?> stream() {
        return this.flux;
    }
}
