package edu.cit.lim.gymtrack.mobile.feature.payments

import edu.cit.lim.gymtrack.mobile.data.model.CheckoutRequest
import edu.cit.lim.gymtrack.mobile.data.model.CheckoutResponse
import edu.cit.lim.gymtrack.mobile.data.model.PaymentResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService
import edu.cit.lim.gymtrack.mobile.data.repository.AuthException

class PaymentRepository(private val apiService: ApiService) {

    suspend fun checkout(planId: Long): CheckoutResponse =
        unwrap(apiService.checkout(CheckoutRequest(planId)))

    suspend fun myPayments(): List<PaymentResponse> =
        unwrap(apiService.myPayments())

    suspend fun allPayments(): List<PaymentResponse> =
        unwrap(apiService.allPayments())

    suspend fun confirmMockPayment(reference: String) {
        unwrap(apiService.confirmMockPayment(reference))
    }

    private suspend fun <T> unwrap(response: retrofit2.Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw AuthException(response.code(), "Empty response from server.")
        }
        val message = response.errorBody()?.string()?.trim('"').orEmpty()
            .ifBlank { "Request failed (${response.code()})." }
        throw AuthException(response.code(), message)
    }
}
