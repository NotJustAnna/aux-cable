package net.notjustanna.application;

import net.notjustanna.webview.WebviewStandalone;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletionException;

public class EarlyWebview {

    /*
     * DEVELOPER NOTES:
     * "Why inline HTML?"
     * Because Class.getResourceAsStream() is TOO MUCH for a loading screen.
     *
     * "Why BAD HTML/CSS/JS?"
     * Because 2MB limitation on webview. See https://github.com/webview/webview/issues/1104
     */

    private static final String INITIAL_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
        html,body {
          background: linear-gradient(to right bottom in oklab, rgb(30, 30, 47) 0%, rgb(0, 0, 16) 100%); color: #ffffff; height: 100%; width: 100%;
          display: flex; justify-content: center; align-items: center;
          font-family: ui-sans-serif, system-ui, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji';
          font-size: 16px;
        }
        </style>
        <script>if (typeof Webview__applicationUrl === 'function') Webview__applicationUrl().then(url => window.location.href = url);</script>
        </head>
        <body><h2>AuxCable is loading...</h2></body>
        </html>
        """;
    public static final String ERROR_HTML = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
        html,body {
          background: linear-gradient(to right bottom in oklab, rgb(30, 30, 47) 0%, rgb(0, 0, 16) 100%); color: #ffffff; height: 100%; width: 100%;
          display: flex; justify-content: center; align-items: center; flex-direction: column;
          font-family: ui-sans-serif, system-ui, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji';
          font-size: 16px;
        }
        pre {
          background-color: #000010; color: #f1f5f9; font-size: 12px; padding: 8px; border-radius: 5px;
          max-width: 95vw; max-height: 95vh; overflow-x: auto; overflow-y: auto;
        }
        h1,pre,code,p { margin: 8px; }
        </style>
        </head>
        <body>
          <h1>AuxCable failed to start</h1>
          <pre><code>__EXCEPTION__</code></pre>
          <p>Please, report this issue on https://github.com/NotJustAnna/aux-cable</p>
        </body>
        </html>
        """;

    public static @Nullable WebviewStandalone create() {
        if (!Desktop.isDesktopSupported()) {
            return null;
        }
        try {
            return new WebviewStandalone(true)
                .setSize(800, 600)
                .setMinSize(400, 300)
                .setTitle("Aux Cable | Loading...")
                .setHtml(INITIAL_HTML);
        } catch (Throwable e) {
            return null;
        }
    }

    public static void onError(WebviewStandalone webview, Throwable throwable) {
        if (throwable instanceof CompletionException) {
            onError(webview, throwable.getCause());
            return;
        }
        try {
            webview.setTitle("Aux Cable | Failed to start");
            webview.setHtml(
                ERROR_HTML.replace("__EXCEPTION__",
                    stackTraceToString(throwable)
                        .replace("\t", "    ")
                        .replace("java.base/", "")
                )
            );
        } catch (Throwable e) {
            System.err.println("Failed to set error HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String stackTraceToString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
