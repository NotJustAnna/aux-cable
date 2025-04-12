package net.notjustanna.controllers;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.services.StateService;

import java.util.concurrent.CompletableFuture;

@Controller("/api/invite")
public class InviteController {
    private final StateService stateService;

    public InviteController(StateService stateService) {
        this.stateService = stateService;
    }

    @Get
    @Produces("application/json")
    CompletableFuture<String> getInvite() {
        JDA jda = stateService.current().getJDA();

        if (jda == null) {
            return Exceptions.unsupportedAction();
        }

        return jda.retrieveApplicationInfo()
            .submit()
            .thenApply(it -> it.getInviteUrl(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK));
    }
}
