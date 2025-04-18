package net.notjustanna.services;

import io.micronaut.context.annotation.Requires;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.notjustanna.state.ApplicationState;
import net.notjustanna.webview.WebviewStandalone;
import net.notjustanna.webview.interop.JacksonWebviewInterop;
import org.jetbrains.annotations.NotNull;
import reactor.core.Disposable;

import java.awt.Desktop;
import java.net.URI;

@Singleton
@Requires(beans = {WebviewStandalone.class})
public class WebviewService {
    private final EmbeddedServer server;
    private final StateService stateService;
    private final WebviewStandalone webview;
    private Disposable disposable;

    public WebviewService(EmbeddedServer server, StateService stateService, WebviewStandalone webview) {
        this.server = server;
        this.stateService = stateService;
        this.webview = webview;
    }

    public void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception e) {
            // Handle exception
        }
    }

    public void callShutdown() {
        webview.close();
    }

    @EventListener
    public void onStart(ApplicationStartupEvent event) {
        webview.setTitle("Aux Cable");

        new JacksonWebviewInterop(webview.getWebview())
            .bindMethod("Webview__openUrl", this, "openUrl")
            .bindMethod("Webview__shutdown", this, "callShutdown")
            .bindMethod("Webview__applicationUrl", this, "applicationUrl");

        disposable = this.stateService.getFlux().subscribe(this::updateTitle);

        webview.navigate(applicationUrl());
    }

    private void updateTitle(ApplicationState state) {
        switch (state.getType()) {
            case IDLE -> webview.setTitle("Aux Cable");
            case LOGGED_IN -> {
                String name = state.getJDA().getSelfUser().getEffectiveName();
                webview.setTitle("Aux Cable | Logged in: " + name);
            }
            case CONNECTED -> {
                VoiceChannel channel = state.getChannel();
                String name = channel.getName() + " (" + channel.getGuild().getName() + ")";
                webview.setTitle("Aux Cable | Connected: " + name);
            }
        }
    }

    @NotNull
    public String applicationUrl() {
        return "http://localhost:" + server.getPort();
    }

    @EventListener
    public void onShutdown(ApplicationShutdownEvent event) {
        if (disposable != null) {
            disposable.dispose();
        }
        callShutdown();
    }
}
