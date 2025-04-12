package net.notjustanna.state;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.models.common.StateType;
import net.notjustanna.models.internal.Account;
import net.notjustanna.services.StateService;

import java.util.concurrent.CompletableFuture;

public class InitialApplication extends ApplicationState {
    public InitialApplication(StateService service) {
        super(StateType.IDLE, service);
        this.flow.push("state.initial.init");
        this.service.updateState(this);
    }

    @Override
    public CompletableFuture<ApplicationState> login(String token, boolean remember) {
        flow.push("action.login.start");
        if (token.isEmpty()) {
            return Exceptions.emptyToken();
        }

        String actualToken;
        if (token.startsWith("RememberMe.id=")) {
            int index = token.indexOf('=') + 1;
            Account account = service.getAccountService().getById(token.substring(index));
            if (account == null) {
                return Exceptions.rememberMeFailed();
            }
            actualToken = account.token();
        } else {
            actualToken = token;
        }

        flow.push("action.login.connecting");

        JDA jda;
        try {
            jda = JDABuilder.createLight(actualToken)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .build();
        } catch (InvalidTokenException e) {
            flow.push("action.login.invalid-token");
            if (!token.startsWith("RememberMe.id=")) {
                return Exceptions.invalidToken();
            }
            return Exceptions.rememberMeInvalidToken();
        } catch (Exception e) {
            flow.push("action.login.unknown-error");
            return Exceptions.unknown("LOGGING_IN_TO_DISCORD", e);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            flow.push("action.login.connected");

            if (remember) {
                SelfUser selfUser = jda.getSelfUser();
                service.getAccountService().save(actualToken, selfUser.getId(), selfUser.getEffectiveName(), selfUser.getEffectiveAvatarUrl());
            }

            flow.push("action.login.success");
            return new LoggedInApplication(service, jda);
        });
    }

    @Override
    public JDA getJDA() {
        return null;
    }

    @Override
    public VoiceChannel getChannel() {
        return null;
    }
}
