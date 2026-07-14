package edu.cit.lim.gymtrack.mobile.feature.payments

import edu.cit.lim.gymtrack.mobile.data.model.CheckoutRequest
import edu.cit.lim.gymtrack.mobile.data.model.CheckoutResponse
import edu.cit.lim.gymtrack.mobile.data.model.PaymentResponse
import edu.cit.lim.gymtrack.mobile.data.model.PaymentStatusResponse
import edu.cit.lim.gymtrack.mobile.data.remote.ApiResponses
import edu.cit.lim.gymtrack.mobile.data.remote.ApiService

class PaymentRepository(private val apiService: ApiService) {

    suspend fun checkout(planId: Long): CheckoutResponse =
        ApiResponses.unwrap(apiService.checkout(CheckoutRequest(planId)))

    suspend fun myPayments(): List<PaymentResponse> =
        ApiResponses.unwrap(apiService.myPayments())

    suspend fun allPayments(): List<PaymentResponse> =
        ApiResponses.unwrap(apiService.allPayments())

    suspend fun confirmMockPayment(reference: String) {
        ApiResponses.unwrap(apiService.confirmMockPayment(reference))
    }

    suspend fun paymentStatus(reference: String): PaymentStatusResponse =
        ApiResponses.unwrap(apiService.paymentStatus(reference))
}
