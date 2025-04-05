package net.notjustanna.auxcable.api.gateway

/**
 * Poor man's version of a HttpResponseException.
 */
class GatewayException(val type: String) : Exception()