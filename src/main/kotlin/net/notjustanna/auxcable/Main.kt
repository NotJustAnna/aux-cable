package net.notjustanna.auxcable

import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.cors.CorsService
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.file.FileService
import com.linecorp.armeria.server.websocket.WebSocketService
import net.notjustanna.auxcable.api.*
import net.notjustanna.auxcable.state.State
import net.notjustanna.webview.WebviewStandalone
import net.notjustanna.webview.interop.JacksonWebviewInterop
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.net.URI
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Main")

    val shutdownHooks = CopyOnWriteArrayList<() -> Unit>() // Thread-safe list for shutdown hooks
    val shutdown = shutdown@ {
        // ensure that the program doesn't try to shut itself down multiple times
        // subsequent calls to shutdown will be no-op.

        val all = shutdownHooks.toList()
        shutdownHooks.clear()
        if (all.isEmpty()) return@shutdown
        all.forEach {
            try { it() } catch (ignored: Exception) {}
        }
        exitProcess(0)
    }

    val port = args.find { it.startsWith("--port=") }?.substringAfter("=")?.toIntOrNull()
        ?: Random.nextInt(49152..65535)

    val state = State.start()
    shutdownHooks += { state().jda?.shutdown() }

    val server = Server.builder().apply {
        http(port)

        annotatedService("/api/accounts", AccountService())
        annotatedService("/api/actions", ActionService(state, shutdown))
        annotatedService("/api/invite", InviteService(state))
        service(
            "/api/gateway", WebSocketService.builder(GatewayService(state))
                .allowedOrigins("*")
                .aggregateContinuation(true)
                .build()
        )

        routeDecorator().pathPrefix("/api").build(
            CorsService.builder("*")
                .allowAllRequestHeaders(true)
                .newDecorator()
        )

        serviceUnder("/docs", DocService())

        serviceUnder("/", FileService.of(ClassLoader.getSystemClassLoader(), "/net/notjustanna/auxcable/frontend"))
    }.build()

    server.start().join()
    shutdownHooks += { server.stop() }

    if (!args.contains("--no-webview") && Desktop.isDesktopSupported()) {
        val url = "http://localhost:$port"
        var flag = true
        try {
            val webview = WebviewStandalone(true)
                .setSize(800, 600)
                .setMinSize(400, 300)
                .setTitle("Aux Cable")
                .navigate(url)
                .setDarkMode(true)

            webview.dispatch { webview.bringToFront() }

            val interop = object {
                fun callShutdown() {
                    shutdown()
                }
                fun openUrl(url: String) {
                    try {
                        Desktop.getDesktop().browse(URI(url))
                    } catch (e: Exception) {
                        logger.error("Could not load browser.", e)
                    }
                }
            }

            JacksonWebviewInterop(webview.webview)
                .bindMethod("Webview__openUrl", interop, "openUrl")
                .bindMethod("Webview__shutdown", interop, "callShutdown")

            val webviewShutdown = { webview.close() }
            shutdownHooks += webviewShutdown
            flag = true
            webview.run()
            flag = false
            shutdownHooks.remove(webviewShutdown)
            shutdown()
        } catch (ex: Exception) {
            if (flag) {
                logger.warn("Could not start webview, falling back to browser.", ex)
                // Somehow the webview managed to fail to load. Great.
                try {
                    Desktop.getDesktop().browse(URI(url))
                } catch (e: Exception) {
                    logger.error("Could not load browser.", e)
                }
            }
        }
    }
}