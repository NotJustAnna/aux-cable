package net.notjustanna.auxcable.state.util

import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.common.MediaType
import com.linecorp.armeria.server.HttpResponseException

object HttpResponseExceptions {
    private fun response(status: HttpStatus, type: String) =
        HttpResponse.of(status, MediaType.JSON, """{"errorType": "$type"}""")

    val inconsistentState: RuntimeException
        get() = HttpResponseException.of(response(HttpStatus.INTERNAL_SERVER_ERROR, "INCONSISTENT_STATE"))

    val noSuchChannel: RuntimeException
        get() = HttpResponseException.of(response(HttpStatus.BAD_REQUEST, "NO_SUCH_CHANNEL"))

    val invalidToken: RuntimeException
        get() = HttpResponseException.of(response(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN"))

    val emptyToken: RuntimeException
        get() = HttpResponseException.of(response(HttpStatus.UNAUTHORIZED, "EMPTY_TOKEN"))

    val rememberMeFailed: RuntimeException
        get() = HttpResponseException.of(response(HttpStatus.BAD_REQUEST, "REMEMBER_ME_FAILED"))

    val unsupportedAction: RuntimeException
        get() = HttpResponseException.of(response(HttpStatus.BAD_REQUEST, "UNSUPPORTED_ACTION"))

    fun unknown(at: String, cause: Throwable): RuntimeException {
        return HttpResponseException.of(
            HttpResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                MediaType.JSON,
                """{"errorType": "UNKNOWN", "at": "$at"}"""
            ),
            cause
        )
    }
}