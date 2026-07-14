package edu.cit.lim.gymtrack.mobile.data.remote

import edu.cit.lim.gymtrack.mobile.data.repository.AuthException
import retrofit2.Response

object ApiResponses {
    fun <T> unwrap(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw AuthException(response.code(), ApiErrorParser.EMPTY)
        }
        throw AuthException(
            response.code(),
            ApiErrorParser.fromFailedResponse(response.code(), response.errorBody()?.string())
        )
    }

    fun unwrapUnit(response: Response<*>) {
        if (response.isSuccessful) return
        throw AuthException(
            response.code(),
            ApiErrorParser.fromFailedResponse(response.code(), response.errorBody()?.string())
        )
    }
}
