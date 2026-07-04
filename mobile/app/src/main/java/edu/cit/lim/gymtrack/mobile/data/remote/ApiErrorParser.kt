package edu.cit.lim.gymtrack.mobile.data.remote

import java.io.IOException

object ApiErrorParser {
    fun parse(body: String?, statusCode: Int, fallback: String = "Something went wrong. Try again."): String {
        val clean = body?.trim()?.trim('"').orEmpty()
        if (clean.isNotBlank()) return clean
        return when (statusCode) {
            400 -> "Request failed. Check your details."
            401 -> "Invalid email or password."
            403 -> "You do not have permission to perform this action."
            else -> fallback
        }
    }

    fun networkMessage(cause: Throwable?): String? = when (cause) {
        is IOException -> "Cannot reach the server. Make sure the backend is running."
        else -> null
    }
}
