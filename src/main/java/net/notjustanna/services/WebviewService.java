package net.notjustanna.services;

import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;
import net.notjustanna.webview.WebviewStandalone;
import net.notjustanna.webview.interop.JacksonWebviewInterop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Desktop;
import java.net.URI;

@Singleton
public class WebviewService {
    private static final Logger log = LoggerFactory.getLogger(WebviewService.class);

    private final Thread thread;
    private final EmbeddedServer server;
    private AutoCloseable closeable;

    public WebviewService(EmbeddedServer server) {
        this.server = server;
        this.thread = Thread.ofPlatform()
            .name("Webview Thread")
            .daemon(false)
            .unstarted(this::startWebview);
    }

    public void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception e) {
            // Handle exception
        }
    }

    public void callShutdown() {
        AutoCloseable webview = this.closeable;
        this.closeable = null;
        if (webview != null) {
            try {
                webview.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
                .setDarkMode(true)
                .dispatch(webview::bringToFront);

            new JacksonWebviewInterop(webview.getWebview())
                .bindMethod("Webview__openUrl", this, "openUrl")
                .bindMethod("Webview__shutdown", this, "callShutdown");

            this.closeable = webview;
            webview.run();
            webview.close();
            if (this.closeable != null) {
                this.closeable = null;
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
        if (thread.isAlive()) {
            return;
        }
        thread.start();
    }

    @EventListener
    public void onShutdown(ApplicationShutdownEvent event) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error("Failed to close webview", e);
            }
        }
        if (!thread.isAlive()) {
            return;
        }
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
