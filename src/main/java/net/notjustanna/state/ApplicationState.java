package net.notjustanna.state;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.models.common.StateType;
import net.notjustanna.models.http.AudioInputModel;
import net.notjustanna.services.FlowService;
import net.notjustanna.services.StateService;

import java.util.concurrent.CompletableFuture;

public abstract class ApplicationState {
    @NonNull
    private final StateType type;

    @NonNull
    protected final StateService service;

    @NonNull
    protected final FlowService flow;

    public ApplicationState(@NonNull StateType type, @NonNull StateService service) {
        this.type = type;
        this.service = service;
        this.flow = service.getFlowService();
    }

    @NonNull
    public StateType getType() {
        return type;
    }

    @Nullable
    public abstract JDA getJDA();

    @Nullable
    public abstract VoiceChannel getChannel();

    @NonNull
    public CompletableFuture<ApplicationState> login(@NonNull String token, boolean remember) {
        flow.push("action.login.unsupported");
        return Exceptions.unsupportedAction();
    }

    public CompletableFuture<ApplicationState> logout() {
        flow.push("action.logout.unsupported");
        return Exceptions.unsupportedAction();
    }

    @NonNull
    public CompletableFuture<ApplicationState> connect(@NonNull String channelId) {
        flow.push("action.connect.unsupported");
        return Exceptions.unsupportedAction();
    }

    public CompletableFuture<ApplicationState> disconnect() {
        flow.push("action.disconnect.unsupported");
        return Exceptions.unsupportedAction();
    }

    public CompletableFuture<ConnectedApplication> stream(@NonNull AudioInputModel input) {
        flow.push("action.stream.unsupported");
        return Exceptions.unsupportedAction();
    }
}
