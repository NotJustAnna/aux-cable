package net.notjustanna.auxcable.api

import com.linecorp.armeria.server.annotation.ConsumesJson
import com.linecorp.armeria.server.annotation.Post
import net.notjustanna.auxcable.api.action.ConnectAction
import net.notjustanna.auxcable.api.action.LoginAction
import net.notjustanna.auxcable.api.action.StreamAction
import net.notjustanna.auxcable.state.State

class ActionService(private val state: () -> State, private val shutdownHook: () -> Unit) {
    @ConsumesJson
    @Post("/login")
    fun login(action: LoginAction) {
        state().login(action.token, action.remember)
    }

    @Post("/logout")
    fun logout() {
        state().logout()
    }

    @ConsumesJson
    @Post("/connect")
    fun connect(action: ConnectAction) {
        state().connect(action.channelId)
    }

    @Post("/disconnect")
    fun disconnect() {
        state().disconnect()
    }

    @ConsumesJson
    @Post("/stream")
    fun stream(action: StreamAction) {
        state().stream(action.input)
    }

    @Post("/shutdown")
    fun shutdown() {
        shutdownHook()
    }
}
