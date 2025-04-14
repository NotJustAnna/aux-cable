package net.notjustanna.services;

import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;
import net.notjustanna.utils.MainThreadExecutor;
import net.notjustanna.webview.WebviewStandalone;
import net.notjustanna.webview.interop.JacksonWebviewInterop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Desktop;
import java.net.URI;

@Singleton
public class WebviewService {
    private static final Logger log = LoggerFactory.getLogger(WebviewService.class);

    private final EmbeddedServer server;
    private final MainThreadExecutor executor;
    private Runnable webviewShutdown;

    public WebviewService(EmbeddedServer server, MainThreadExecutor executor) {
        this.server = server;
        this.executor = executor;
    }

    public void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception e) {
            // Handle exception
        }
    }

    public void callShutdown() {
        Runnable webviewShutdown = this.webviewShutdown;
        this.webviewShutdown = null;
        if (webviewShutdown != null) {
            webviewShutdown.run();
        }
        server.stop();
    }

    private void startWebview() {
        String url = "http://localhost:" + server.getPort();
        boolean flag_webviewFault = true;

        try {
            WebviewStandalone webview = new WebviewStandalone(true);

            webview.setSize(800, 600)
                .setMinSize(400, 300)
                .setTitle("Aux Cable")
                .navigate(url)
                .setDarkMode(true);

            new JacksonWebviewInterop(webview.getWebview())
                .bindMethod("Webview__openUrl", this, "openUrl")
                .bindMethod("Webview__shutdown", this, "callShutdown");

            this.webviewShutdown = () -> {
                webview.close();
                executor.shutdown();
            };
            webview.run();
            webview.close();
            executor.shutdown();
            if (this.webviewShutdown != null) {
                this.webviewShutdown = null;
                flag_webviewFault = false;
                callShutdown();
            }
        } catch (Exception e) {
            if (flag_webviewFault) {
                log.warn("Could not start webview, falling back to browser.", e);
                // Somehow the webview managed to fail to load. Great.
                try {
                    Desktop.getDesktop().browse(URI.create(url));
                } catch (Exception ex) {
                    log.error("Could not load browser.", ex);
                }
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @EventListener
    public void onStart(ApplicationStartupEvent event) {
        executor.execute(this::startWebview);
    }

    @EventListener
    public void onShutdown(ApplicationShutdownEvent event) {
        if (this.webviewShutdown != null) {
            this.webviewShutdown.run();
        }
    }
}
