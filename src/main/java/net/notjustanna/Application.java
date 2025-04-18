package net.notjustanna;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import net.notjustanna.application.EarlyWebview;
import net.notjustanna.webview.WebviewStandalone;

import java.util.concurrent.CompletableFuture;

public class Application {
    public static void main(String[] args) {
        var webview = EarlyWebview.create();
        if (webview != null) {
            appRun(webview, args);
        } else {
            serverRun(args);
        }
    }

    private static void appRun(WebviewStandalone webview, String[] args) {
        var ctx = CompletableFuture.supplyAsync(() -> Micronaut.build(args)
            .banner(false)
            .mainClass(Application.class)
            .singletons(webview)
            .start());

        ctx.exceptionally(throwable -> {
            EarlyWebview.onError(webview, throwable);
            return null;
        });

        try (webview) {
            webview.run();
        } finally {
            ctx.thenAccept(ApplicationContext::stop);
        }
    }

    private static void serverRun(String[] args) {
        Micronaut.run(Application.class, args);
    }
}