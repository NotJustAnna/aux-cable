package net.notjustanna.state;

import io.micronaut.core.annotation.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.notjustanna.exceptions.Exceptions;
import net.notjustanna.models.common.StateType;
import net.notjustanna.services.StateService;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class LoggedInApplication extends ApplicationState {
    private final JDA jda;

    public LoggedInApplication(@NonNull StateService service, JDA jda) {
        super(StateType.LOGGED_IN, service);
        this.jda = jda;
        this.flow.push("state.logged-in.init");
        this.service.updateState(this);
    }

    @Override
    public CompletableFuture<ApplicationState> logout() {
        flow.push("action.logout.start");
        var self = jda.getSelfUser();
        service.getAccountService().update(self.getId(), self.getEffectiveName(), self.getEffectiveAvatarUrl());
        flow.push("action.logout.disconnect");
        jda.shutdown();
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!jda.awaitShutdown(10, TimeUnit.SECONDS)) {
                    flow.push("action.logout.force-disconnect");
                    jda.shutdownNow();
                }
            } catch (InterruptedException ignored) {
            }
            flow.push("action.logout.success");
            return new InitialApplication(service);
        });
    }

    @Override
    public CompletableFuture<ApplicationState> connect(String channelId) {
        flow.push("action.connect.start");
        VoiceChannel channel = jda.getVoiceChannelById(channelId);
        if (channel == null) {
            flow.push("action.connect.no-such-channel");
            return Exceptions.noSuchChannel();
        }

        Mono<?> joined = Mono.create(sink -> jda.listenOnce(GuildVoiceUpdateEvent.class)
            .filter(LoggedInApplication.joinedChannel(channel))
            .subscribe(sink::success)
        );
        channel.getGuild().getAudioManager().openAudioConnection(channel);

        return CompletableFuture.supplyAsync(() -> {
            try {
                joined.timeout(Duration.ofSeconds(10)).block();
                flow.push("action.connect.joined");
            } catch (Exception e) {
                e.printStackTrace();
                flow.push("action.connect.timeout");
                channel.getGuild().getAudioManager().closeAudioConnection();
                return this;
            }

            flow.push("action.connect.success");
            return new ConnectedApplication(service, jda, channelId);
        });
    }

    @Override
    public JDA getJDA() {
        return jda;
    }

    @Override
    public VoiceChannel getChannel() {
        return null;
    }

    private static Predicate<GuildVoiceUpdateEvent> joinedChannel(VoiceChannel channel) {
        String channelId = channel.getId();
        String selfId = channel.getJDA().getSelfUser().getId();
        return event -> {
            String userId = event.getMember().getUser().getId();
            AudioChannelUnion joined = event.getChannelJoined();
            if (!(joined instanceof VoiceChannel)) {
                return false;
            }
            return userId.equals(selfId) && joined.getId().equals(channelId);
        };
    }
}
