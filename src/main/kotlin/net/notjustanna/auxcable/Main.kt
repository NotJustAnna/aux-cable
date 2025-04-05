package net.notjustanna.auxcable

import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.cors.CorsService
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.file.FileService
import com.linecorp.armeria.server.websocket.WebSocketService
import dev.webview.Webview
import net.notjustanna.auxcable.api.AccountService
import net.notjustanna.auxcable.api.ActionService
import net.notjustanna.auxcable.api.GatewayService
import net.notjustanna.auxcable.state.State
import kotlin.random.Random
import kotlin.random.nextInt

fun main(args: Array<String>) {
    val port = args.find { it.startsWith("--port=") }?.substringAfter("=")?.toIntOrNull()
        ?: Random.nextInt(49152..65535)

    val state = State.start()

    val server = Server.builder().apply {
        http(port)

        annotatedService("/api/accounts", AccountService())
        annotatedService("/api/actions", ActionService(state))
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

    if (!args.contains("--no-webview")) {
        val webview = Webview(true).apply {
            loadURL("http://localhost:$port/index.html")
            setTitle("Aux Cable")
        }

        webview.run()
        webview.close()
    }
}