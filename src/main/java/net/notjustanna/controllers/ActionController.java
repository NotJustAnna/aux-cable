package net.notjustanna.controllers;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import net.notjustanna.models.http.input.ConnectInput;
import net.notjustanna.models.http.input.LoginInput;
import net.notjustanna.models.http.input.StreamInput;
import net.notjustanna.services.StateService;

import java.util.concurrent.CompletableFuture;

@Controller("/api/actions")
public class ActionController {
    private static final Runnable discard = () -> {};

    private final StateService stateService;
    private final ApplicationContext application;

    public ActionController(StateService stateService, ApplicationContext application) {
        this.stateService = stateService;
        this.application = application;
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Post("/login")
    public CompletableFuture<Void> login(@Body LoginInput input) {
        return stateService.current().login(input.token(), input.remember()).thenRun(ActionController.discard);
    }

    @Post("/logout")
    public CompletableFuture<Void> logout() {
        return stateService.current().logout().thenRun(ActionController.discard);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Post("/connect")
    public CompletableFuture<Void> connect(@Body ConnectInput input) {
        return stateService.current().connect(input.channelId()).thenRun(ActionController.discard);
    }

    @Post("/disconnect")
    public CompletableFuture<Void> disconnect() {
        return stateService.current().disconnect().thenRun(ActionController.discard);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Post("/stream")
    public CompletableFuture<Void> stream(@Body StreamInput input) {
        return stateService.current().stream(input.input()).thenRun(ActionController.discard);
    }

    @Post("/shutdown")
    public void shutdown() {
        CompletableFuture.runAsync(application::stop);

    }
}
