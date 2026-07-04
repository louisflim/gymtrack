package edu.cit.lim.gymtrack.mobile.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenProvider()
        val isPublicAuthRoute = request.url.encodedPath.contains("/api/auth/login")
            || request.url.encodedPath.contains("/api/auth/register")
        val authenticatedRequest = if (!token.isNullOrBlank() && !isPublicAuthRoute) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(authenticatedRequest)
    }
}
