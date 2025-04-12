package net.notjustanna.state;

import io.micronaut.core.annotation.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.notjustanna.audio.AudioInput;
import net.notjustanna.audio.AudioInputs;
import net.notjustanna.audio.AudioSystemSendHandler;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.models.common.StateType;
import net.notjustanna.models.http.AudioInputModel;
import net.notjustanna.services.StateService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ConnectedApplication extends ApplicationState {
    private final JDA jda;
    private final String channelId;

    public ConnectedApplication(@NonNull StateService service, JDA jda, String channelId) {
        super(StateType.CONNECTED, service);
        this.jda = jda;
        this.channelId = channelId;
        this.flow.push("state.connected.init");
        this.service.updateState(this);
    }

    @Override
    public CompletableFuture<ConnectedApplication> stream(AudioInputModel input) {
        VoiceChannel channel = getChannel(); // this happens before flow to keep consistent state.
        AudioManager audioManager = channel.getGuild().getAudioManager();
        flow.push("action.stream.start");


        AudioInput actualInput = null;
        if (input != null) {
            List<AudioInput> all = AudioInputs.all();

            Optional<AudioInput> maybeActual = all.stream()
                .filter(it -> it.name().equals(input.name()) && it.device().equals(input.device()))
                .findFirst();

            if (maybeActual.isEmpty()) {
                flow.push("action.stream.no-such-input");
                return Exceptions.noSuchAudioInput();
            }

            actualInput = maybeActual.get();
        }

        AudioSystemSendHandler previous = audioManager.getSendingHandler() instanceof AudioSystemSendHandler s ? s : null;

        if (actualInput != null) {
            if (previous != null && previous.getInput().equals(actualInput)) {
                flow.push("action.stream.unchanged");
                return CompletableFuture.completedFuture(this); // No need to change the input if it's the same.
            }
            flow.push("action.stream.open");
            AudioSystemSendHandler handler = AudioSystemSendHandler.create(actualInput);
            if (handler == null) {
                flow.push("action.stream.open.failed");
                return Exceptions.audioInputFailed();
            }
            flow.push("action.stream.opened");
            audioManager.setSendingHandler(handler);
        } else {
            flow.push("action.stream.close");
            audioManager.setSendingHandler(null);
        }
        if (previous != null) {
            try {
                previous.close();
            } catch (IOException ignored) {
            }
        }

        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<ApplicationState> disconnect() {
        flow.push("action.disconnect.start");
        flow.push("action.disconnect.close");
        this.getChannel().getGuild().getAudioManager().closeAudioConnection();
        flow.push("action.disconnect.success");
        return CompletableFuture.completedFuture(new LoggedInApplication(service, jda));
    }

    @Override
    public CompletableFuture<ApplicationState> logout() {
        return this.disconnect().thenCompose(ApplicationState::disconnect);
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public VoiceChannel getChannel() {
        VoiceChannel channel = jda.getVoiceChannelById(channelId);
        if (channel == null) {
            disconnect();
            return Exceptions.inconsistentState();
        }
        return channel;
    }
}
