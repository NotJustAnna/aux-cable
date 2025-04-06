package net.notjustanna.auxcable.api

import com.linecorp.armeria.server.annotation.Get
import io.reactivex.rxjava3.core.Single
import net.dv8tion.jda.api.Permission
import net.notjustanna.auxcable.state.State
import net.notjustanna.auxcable.state.util.HttpResponseExceptions

class InviteService(private val state: () -> State) {
    @Get("/")
    fun getInvite(): Single<String> {
        val jda = state().jda ?: throw HttpResponseExceptions.unsupportedAction
        return Single.fromCompletionStage(jda.retrieveApplicationInfo().submit())
            .map {
                it.getInviteUrl(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)
            }
    }
}