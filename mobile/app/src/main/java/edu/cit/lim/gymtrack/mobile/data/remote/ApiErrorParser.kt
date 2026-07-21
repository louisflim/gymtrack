package edu.cit.lim.gymtrack.mobile.data.remote

import java.io.IOException

object ApiErrorParser {
    const val GENERIC = "Something went wrong. Please try again."
    const val NETWORK = "We couldn't connect right now. Check your internet connection and try again."
    const val EMPTY = "We couldn't complete that request. Please try again."
    const val CHECK_DETAILS = "Please check your details and try again."

    fun parse(body: String?, statusCode: Int, fallback: String = GENERIC): String {
        val clean = body?.trim()?.trim('"').orEmpty()
        if (clean.isNotBlank()) return clean
        return when (statusCode) {
            400 -> CHECK_DETAILS
            401 -> "Please sign in again to continue."
            403 -> "You don't have permission to do that."
            404 -> "We couldn't find what you were looking for."
            in 500..599 -> "Something went wrong on our side. Please try again in a moment."
            else -> fallback
        }
    }

    fun networkMessage(cause: Throwable?): String? = when (cause) {
        is IOException -> NETWORK
        else -> null
    }

    /** Prefer a clear network/API message over a silent generic fallback. */
    fun friendlyExceptionMessage(cause: Throwable?): String {
        networkMessage(cause)?.let { return it }
        var current: Throwable? = cause
        while (current != null) {
            networkMessage(current)?.let { return it }
            val msg = current.message?.trim().orEmpty()
            if (msg.contains("Unable to resolve host", ignoreCase = true) ||
                msg.contains("failed to connect", ignoreCase = true) ||
                msg.contains("timeout", ignoreCase = true)
            ) {
                return NETWORK
            }
            if (msg.contains("BEGIN_OBJECT") ||
                msg.contains("Expected BEGIN") ||
                msg.contains("JsonReader") ||
                msg.contains("malformed")
            ) {
                return "The server returned an unexpected response. Open the API URL in a browser to wake it, then try again."
            }
            current = current.cause
        }
        val detail = cause?.message?.trim().orEmpty()
        return if (detail.isNotBlank()) detail else GENERIC
    }

    fun fromFailedResponse(statusCode: Int, errorBody: String?): String =
        parse(errorBody, statusCode, GENERIC)
}
