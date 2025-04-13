package net.notjustanna.services;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.notjustanna.state.ApplicationState;
import net.notjustanna.state.InitialApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Objects;

@Singleton
public class StateService {
    private final Sinks.Many<ApplicationState> sink;
    private final AccountService accountService;
    private final FlowService flowService;
    private ApplicationState currentState;

    public StateService(@NonNull AccountService accountService, @NonNull FlowService flowService) {
        this.accountService = accountService;
        this.flowService = flowService;
        this.sink = Sinks.many().replay().latest();
        this.currentState = new InitialApplication(this);
    }

    public Flux<ApplicationState> getFlux() {
        return sink.asFlux();
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public FlowService getFlowService() {
        return flowService;
    }

    public void updateState(@NonNull ApplicationState state) {
        this.currentState = Objects.requireNonNull(state);
        this.sink.emitNext(state, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public ApplicationState current() {
        return currentState;
    }


    @EventListener
    public void onShutdown(ApplicationShutdownEvent event) {
        if (currentState != null) {
            JDA jda = currentState.getJDA();

            if (jda != null) {
                jda.shutdown();
            }
        }
    }
}
