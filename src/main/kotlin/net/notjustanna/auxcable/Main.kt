package net.notjustanna.auxcable

import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.cors.CorsService
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.file.FileService
import com.linecorp.armeria.server.graphql.GraphqlService
import dev.webview.Webview
import net.notjustanna.auxcable.api.GraphqlApi
import net.notjustanna.auxcable.state.InitialState

fun main() {
//    val port = Random.nextInt(49152..65535)
    val port = 3000

    val server = Server.builder().apply {
        http(port)

//        serviceUnder("/docs", DocService())

        val cors = CorsService.builder("*")
            .allowAllRequestHeaders(true)
            .newDecorator()

        service("/graphql", GraphqlService.builder()
            .enableWebSocket(true)
            .webSocketServiceCustomizer { it.allowedOrigins("*") } // TODO: Remove on prod
            .graphql(GraphqlApi.init(InitialState()))
            .build()
            .decorate(cors)
        )

        serviceUnder("/", FileService.of(ClassLoader.getSystemClassLoader(), "/net/notjustanna/auxcable/frontend"))
    }.build()

    server.start().join()

//    val webview = Webview(true).apply {
//        loadURL("http://localhost:$port/index.html")
//        setTitle("Aux Cable")
//    }
//
//    webview.run()
//    webview.close()
//    server.stop().join()
}